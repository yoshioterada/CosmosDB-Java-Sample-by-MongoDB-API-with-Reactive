package com.yoshio3.dao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.bson.Document;
import org.reactivestreams.Publisher;

import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.yoshio3.model.Person;

@RequestScoped
public class ItemManagement {

    @Inject
    private MongoDBClientInitializer mongoInit;

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        mongoClient = mongoInit.getMongoClientInstance();
    }

    /**
     * Create Publisher for creating an Item in specified container in DB
     * 
     * @param database  挿入対象のデータベース
     * @param container 挿入対象のコレクション
     * @param person    挿入するデータ
     * @return 挿入結果を返す Publisher
     */
    public Publisher<InsertOneResult> insertItem(String database, String container, Person person) {
        Document document = new Document();
        document.append("id", person.getId()).append("age", person.getAge()).append("firstName", person.getFirstName())
                .append("lastName", person.getLastName());

        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(container);
        return collection.insertOne(document);
    }

    /**
     * List All Items in specified container in DB
     * 
     * @param database  対象のデータベース名
     * @param container 対象のコンテナ名
     * @return アイテム一覧取得用の Publisher
     */
    public FindPublisher<Document> listAllItems(String database, String container) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(container);
        return collection.find();
    }
}
