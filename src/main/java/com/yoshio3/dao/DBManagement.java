package com.yoshio3.dao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.mongodb.reactivestreams.client.MongoClient;

import org.reactivestreams.Publisher;

@RequestScoped
public class DBManagement {

    @Inject
    private MongoDBClientInitializer mongoInit;

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        mongoClient = mongoInit.getMongoClientInstance();
    }

    /**
     * List All DataBases
     * 
     * @return データベース一覧を取得するための Publisher
     */
    public Publisher<String> listAllDatabases() {
        return mongoClient.listDatabaseNames();
    }

    /**
     * Delete Database
     * 
     * @param database 削除対象のデータベースめ
     * @return
     */
    public Publisher<Void> deleteDataBase(String database) {
        return mongoClient.getDatabase(database).drop();
    }
}
