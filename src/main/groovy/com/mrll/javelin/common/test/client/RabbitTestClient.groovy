package com.mrll.javelin.common.test.client

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory


class RabbitTestClient {

    private ConnectionFactory factory
    private Connection conn
    private Channel channel


    RabbitTestClient(String rabbitUri) {
        factory = new ConnectionFactory()
        factory.setUri(rabbitUri)
    }


    Channel getNewChannel() {
        if (factory == null || conn == null) {
            conn = factory.newConnection()
        }
        return conn.createChannel()
    }


    void close() throws IOException {
        channel?.close()
        conn?.close()
    }

}
