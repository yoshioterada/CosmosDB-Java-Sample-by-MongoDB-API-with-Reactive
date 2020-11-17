package com.yoshio3;

import javax.inject.Inject;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.yoshio3.dao.ContainerManage;
import com.yoshio3.dao.DBManagement;
import com.yoshio3.dao.ItemManagement;
import com.yoshio3.model.Person;

import org.bson.Document;
import org.reactivestreams.Publisher;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;

// http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/getting-started/quick-start-pojo/
// http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/getting-started/quick-start/
// http://mongodb.github.io/mongo-java-driver/4.1/driver-reactive/getting-started/installation/

public class MongoResource {

    @Inject
    DBManagement dbManage;

    @Inject
    ContainerManage containerManage;

    @Inject
    ItemManagement itemManage;

    /**
     * List All Database Name
     * 
     * curl http://localhost:8080/react-route/database
     */
    @Route(path = "/react-route/database", methods = HttpMethod.GET, produces = "application/json")
    public Multi<String> listDataBases() {
        Publisher<String> listAllDatabases = dbManage.listAllDatabases();
        // TODO String を JSON に変換する必要があり
        return Multi.createFrom().publisher(listAllDatabases);
    }

    /**
     * Delete DataBase
     * 
     * curl -X DELETE http://localhost:8080/react-route/database/TEST_DB2
     * 
     * @param databaseName
     * @return
     */
    @Route(path = "/react-route/database/:database", methods = HttpMethod.DELETE, produces = "application/json")
    public Uni<String> deleteDataBases(@Param("database") String databaseName) {
        Publisher<Void> deleteDatabases = dbManage.deleteDataBase(databaseName);
        return Uni.createFrom().publisher(deleteDatabases).map(vd -> "Deleted Success");
    }

    /**
     * Create Container
     * 
     * curl -X POST
     * http://localhost:8080/react-route/database/TEST_DB/addContainer/container
     * 
     * @param database  コンテナを作成する対象のデータベース
     * @param container 作成するコンテナ名
     * @return コンテナ作成のメッセージ
     */
    @Route(path = "/react-route/database/:database/addContainer/:container", methods = HttpMethod.POST, produces = "application/json")
    public Uni<String> createContainer(@Param("database") String database, @Param("container") String container) {
        Publisher<Void> createContainer = containerManage.createContainer(database, container);
        return Uni.createFrom().publisher(createContainer).map(vd -> "Created Success");
    }

    /**
     * Delete Container
     * 
     * curl -X DELETE
     * http://localhost:8080/react-route/database/TEST_DB/deleteContainer/container
     * 
     * @param database  コンテナを削除する対象のデータベース
     * @param container 削除するコンテナ名
     * @return コンテナ削除のメッセージ
     */
    @Route(path = "/react-route/database/:database/deleteContainer/:container", methods = HttpMethod.DELETE, produces = "application/json")
    public Uni<String> deleteContainer(@Param("database") String database, @Param("container") String container) {
        Publisher<Void> deleteContainer = containerManage.deleteContainer(database, container);
        return Uni.createFrom().publisher(deleteContainer).map(vd -> "Deleted Success");
    }

    /**
     * List Containers
     * 
     * curl http://localhost:8080/react-route/database/TEST_DB/container
     * 
     * @param database コンテナが含まれるデータベース
     * @return コンテナの一覧
     */
    @Route(path = "/react-route/database/:database/container", methods = HttpMethod.GET, produces = "application/json")
    public Uni<String> deleteContainer(@Param("database") String database) {
        Publisher<String> listContainers = containerManage.listContainers(database);
        // TODO String から JSON を作成する必要があり
        return Uni.createFrom().publisher(listContainers);
    }

    /**
     * Create Item from Person
     * 
     * curl -X POST -H 'Content-Type:application/json'
     * http://localhost:8080/react-route/database/PERSON_DB/container/personmanage/item/addItem
     * -d '{"firstName": "a", "lastName": "b","age": 39}'
     * 
     * @param person
     * @param databaseName
     * @param containerName
     * @return
     */
    @Route(path = "/react-route/database/:database/container/:container/item/addItem", methods = HttpMethod.POST, produces = "application/json")
    public Uni<InsertOneResult> createMongoDBItem(@Body Person person, @Param("database") String databaseName,
            @Param("container") String containerName) {
        Publisher<InsertOneResult> insertItem = itemManage.insertItem(databaseName, containerName, person);
        return Uni.createFrom().publisher(insertItem);
    }

    /**
     * List all Items in the Container
     * 
     * @param databaseName  対象のデータベース名
     * @param containerName 対象のコンテナ名
     * @return アイテムの一覧
     */
    @Route(path = "/react-route/database/:database/container/:container/item", methods = HttpMethod.GET, produces = "application/json")
    public Multi<Document> listAllItems(@Param("database") String databaseName,
            @Param("container") String containerName) {
        FindPublisher<Document> listAllItems = itemManage.listAllItems(databaseName, containerName);
        // TODO Document から JSON への変換が必要
        return Multi.createFrom().publisher(listAllItems);
    }
}