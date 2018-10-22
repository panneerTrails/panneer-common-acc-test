package com.mrll.javelin.common.test.client

import com.mongodb.MongoClient
import spock.lang.Specification

class TestMongoTestClient extends Specification {
    void 'create default object'() {
        given:

        def mqUri = "mongodb://drmAudit_user:D5Z5RifcLOFhKf0W@mongodb-dev.adminsys.mrll.com:27017/drmAudit?maxIdleTimeMS=60000"
        MongoTestClient mongoTestClient = new MongoTestClient()

        when:
        MongoClient mongoClient = mongoTestClient.createMongoClient(mqUri)

        then:
        mongoClient != null

        cleanup:
        mongoTestClient.close(mongoClient)
    }
}
