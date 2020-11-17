package com.yoshio3.dao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.reactivestreams.Publisher;

@RequestScoped
public class ContainerManage {
    @Inject
    private MongoDBClientInitializer mongoInit;

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        mongoClient = mongoInit.getMongoClientInstance();
    }

    /**
     * Create Container
     * 
     * @param database  作成するコンテナのデータベース名
     * @param container 作成するコンテナ名
     * @return コンテナを作成するための Publisher
     */
    public Publisher<Void> createContainer(String database, String container) {
        MongoDatabase mongoDb = mongoClient.getDatabase(database);
        return mongoDb.createCollection(container);
    }

    /**
     * Delete Container
     * 
     * @param database  削除するコンテナのデータベース名
     * @param container 削除するコンテナ名
     * @return コンテナを削除するための Publisher
     */
    public Publisher<Void> deleteContainer(String database, String container) {
        MongoDatabase mongoDb = mongoClient.getDatabase(database);
        return mongoDb.getCollection(container).drop();
    }
}
