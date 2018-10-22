package com.mrll.javelin.common.test.client;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoTestClient {

    MongoClient createMongoClient(String mongoUri) {
        return new MongoClient(new MongoClientURI(mongoUri));
    }

    void close(MongoClient client) {
        client.close();
    }

}
