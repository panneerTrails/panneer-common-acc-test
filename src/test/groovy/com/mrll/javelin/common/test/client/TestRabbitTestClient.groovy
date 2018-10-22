package com.mrll.javelin.common.test.client

import com.mrll.javelin.common.test.embededMQ.EmbeddedAmqpBroker
import spock.lang.Shared
import spock.lang.Specification

class TestRabbitTestClient extends Specification {


    @Shared
    public EmbeddedAmqpBroker embeddedAMQPBroker

    void setup() {
        embeddedAMQPBroker = new EmbeddedAmqpBroker()
        embeddedAMQPBroker.before()
    }

    void cleanup() {
        embeddedAMQPBroker.after()
    }

    void 'create default object'() {
        given:
        def mqUri = "amqp://localhost:5672"
        RabbitTestClient subject = new RabbitTestClient(mqUri)

        expect:
        subject != null
        subject.getNewChannel() != null

        cleanup:
        subject.close()
    }

}
