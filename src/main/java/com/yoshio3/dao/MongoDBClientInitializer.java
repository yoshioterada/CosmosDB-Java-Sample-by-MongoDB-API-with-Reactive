package com.yoshio3.dao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@ApplicationScoped
public class MongoDBClientInitializer {

    private final static String MONGO_DB_CONNECTION_STRING = "mongodb://****:****@****.mongo.cosmos.azure.com:10255/?ssl=true&sslInvalidHostNameAllowed=true&replicaSet=globaldb&retrywrites=false&maxIdleTimeMS=120000&appName=@*****@";

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        ConnectionString connection = new ConnectionString(MONGO_DB_CONNECTION_STRING);
        mongoClient = MongoClients.create(connection);
    }

    @PreDestroy
    public void destroy() {
        mongoClient.close();
    }

    public MongoClient getMongoClientInstance() {
        return this.mongoClient;
    }
}
