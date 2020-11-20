# Quarkus 上で CosmosDB Mongo DB API を使用して Reactive アプリを App Service にデプロイする

このチュートリアルでは、Quarkus 上で CosmosDB Mongo DB API の Reactive な非同期・ノンブロッキング API を使用して DB、コンテナ、Item の作成を行う Java Web アプリを作成し、App Service Linux  にデプロイする方法について説明します。  
このチュートリアルは、Web のフロント・エンド実装で [Quarkus](https://quarkus.io/) の [Reactive ROUTE](https://quarkus.io/guides/reactive-routes) を使用し、バックエンドの Cosmos DB の操作に [MongoDB Reactive Streams Java Driver](http://mongodb.github.io/mongo-java-driver/) を使用します。  
まず、アプリケーションの動作確認をローカルの環境で行い、その後 Azure App Service にデプロイします。

## 前提条件

* [Azure CLI]()  
* [Java 11]()  
* [Maven 3]()  
* [Git]()  
* [Quarkus Reactive ROUTE]()  
* [MongoDB Reactive Streams Java Driver]()  

## サンプルを入手する

サンプル アプリを使い始める場合は、次のコマンドを使用してソース リポジトリを複製し、準備します。

```bash
git clone https://github.com/yoshioterada/CosmosDB-Java-Sample-by-MongoDB-API-with-Reactive
```

## リアクティブ・プログラミングの概要

### Reactive Streams の概要
[Reactive Streams](https://www.reactive-streams.org/)

#### Publisher

#### Subscriber

### MongoDB Reactive Streams Java Driver
[MongoDB Reactive Streams Java Driver](http://mongodb.github.io/mongo-java-driver/)

### Quarkus Reactive ROUTE

[Quarkus Reactive ROUTE](https://quarkus.io/guides/reactive-routes)


## サンプル・ソースコードの説明

### MongoDB への接続

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@ApplicationScoped
public class MongoDBClientInitializer {

    private final static String MONGO_DB_CONNECTION_STRING = "mongodb://****:****@****.mongo.cosmos.azure.com:10255/?ssl=true&sslInvalidHostNameAllowed=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@*****@";

    @PostConstruct
    public void init() {
        ConnectionString connection = new ConnectionString(MONGO_DB_CONNECTION_STRING);
        mongoClient = MongoClients.create(connection);
    }
```
### DB の一覧表示

```java
    public Publisher<String> listAllDatabases() {
        return mongoClient.listDatabaseNames();
    }
```

```java
    @Route(path = "/react-route/database", methods = HttpMethod.GET, produces = "application/json")
    public Multi<String> listDataBases() {
        Publisher<String> listAllDatabases = dbManage.listAllDatabases();
        // TODO String を JSON に変換する必要があり
        return Multi.createFrom().publisher(listAllDatabases);
    }
```


### DB の一削除

```java
    public Publisher<Void> deleteDataBase(String database) {
        return mongoClient.getDatabase(database).drop();
    }
```

```java
    @Route(path = "/react-route/database/:database", methods = HttpMethod.DELETE, produces = "application/json")
    public Uni<String> deleteDataBases(@Param("database") String databaseName) {
        Publisher<Void> deleteDatabases = dbManage.deleteDataBase(databaseName);
        return Uni.createFrom().publisher(deleteDatabases).map(vd -> "Deleted Success");
    }
```


### コンテナの作成

```javas
    public Publisher<Void> createContainer(String database, String container) {
        MongoDatabase mongoDb = mongoClient.getDatabase(database);
        return mongoDb.createCollection(container);
    }
```

```
    @Route(path = "/react-route/database/:database/addContainer/:container", methods = HttpMethod.POST, produces = "application/json")
    public Uni<String> createContainer(@Param("database") String database, @Param("container") String container) {
        Publisher<Void> createContainer = containerManage.createContainer(database, container);
        return Uni.createFrom().publisher(createContainer).map(vd -> "Created Success");
    }

```


### コンテナの一覧表示

```java
    public Publisher<String> listContainers(String database) {
        MongoDatabase mongoDb = mongoClient.getDatabase(database);
        return mongoDb.listCollectionNames();
    }
```

```
    @Route(path = "/react-route/database/:database/container", methods = HttpMethod.GET, produces = "application/json")
    public Uni<String> listContainer(@Param("database") String database) {
        Publisher<String> listContainers = containerManage.listContainers(database);
        // TODO String から JSON を作成する必要があり
        return Uni.createFrom().publisher(listContainers);
    }
```


### コンテナの一削除

```java
    public Publisher<Void> deleteContainer(String database, String container) {
        MongoDatabase mongoDb = mongoClient.getDatabase(database);
        return mongoDb.getCollection(container).drop();
    }
```

```
    @Route(path = "/react-route/database/:database/deleteContainer/:container", methods = HttpMethod.DELETE, produces = "application/json")
    public Uni<String> deleteContainer(@Param("database") String database, @Param("container") String container) {
        Publisher<Void> deleteContainer = containerManage.deleteContainer(database, container);
        return Uni.createFrom().publisher(deleteContainer).map(vd -> "Deleted Success");
    }
```

### アイテムの作成

```java
    public Publisher<InsertOneResult> insertItem(String database, String container, Person person) {
        Document document = new Document();
        document.append("id", person.getId()).append("age", person.getAge()).append("firstName", person.getFirstName())
                .append("lastName", person.getLastName());

        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(container);
        return collection.insertOne(document);
    }
```

```
    @Route(path = "/react-route/database/:database/container/:container/item/addItem", methods = HttpMethod.POST, produces = "application/json")
    public Uni<InsertOneResult> createMongoDBItem(@Body Person person, @Param("database") String databaseName,
            @Param("container") String containerName) {
        Publisher<InsertOneResult> insertItem = itemManage.insertItem(databaseName, containerName, person);
        return Uni.createFrom().publisher(insertItem);
    }
```


### アイテムの一覧表示

```java
    public FindPublisher<Document> listAllItems(String database, String container) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(container);
        return collection.find();
    }
```

```java
    @Route(path = "/react-route/database/:database/container/:container/item", methods = HttpMethod.GET, produces = "application/json")
    public Multi<Document> listAllItems(@Param("database") String databaseName,
            @Param("container") String containerName) {
        FindPublisher<Document> listAllItems = itemManage.listAllItems(databaseName, containerName);
        // TODO Document から JSON への変換が必要
        return Multi.createFrom().publisher(listAllItems);
    }
```

### アイテムの一削除

***-------TODO---------***


## MongoDB Reactive App サンプルをローカルでビルドして実行する

```bash
mvn clean package
```

```
mvn quarkus:dev
```

## MongoDB Reactive App サンプルをローカルで動作確認をする

### DB の一覧表示

```bash
curl http://localhost:8080/react-route/database
```

### DB の一削除

```bash
curl -X DELETE http://localhost:8080/react-route/database/TEST_DB2
```

### コンテナの作成

```bash
http://localhost:8080/react-route/database/TEST_DB/addContainer/container
```

### コンテナの一覧表示

```bash
curl http://localhost:8080/react-route/database/TEST_DB/container
```

### コンテナの一削除

```bash
curl -X DELETE http://localhost:8080/react-route/database/TEST_DB/deleteContainer/container
```

### アイテムの作成

```bash
curl -X POST -H 'Content-Type:application/json' \
 http://localhost:8080/react-route/database/PERSON_DB/container/personmanage/item/addItem \
 -d '{"id":"1", "firstName": "Taro", "lastName": "Yamada","age": 39}'
```

### アイテムの一覧表示

```bash
curl http://localhost:8080/react-route/database/:database/container/:container/item
```

### アイテムの一削除

***----------TODO----------***
