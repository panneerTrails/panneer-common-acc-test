package com.mrll.javelin.common.test.config

import groovyx.net.http.RESTClient
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.StandardHttpRequestRetryHandler
import org.apache.http.protocol.HttpContext

class RestClientConfigurer {

    String hitIdPrefix = 'acc-test-'

    def generateHitId() {
        return hitIdPrefix + UUID.randomUUID().toString()
    }

    RESTClient configure(host, Map defaultHeaders = null) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setRetryHandler(new StandardHttpRequestRetryHandler(3, true))
                .addInterceptorFirst(new HttpRequestInterceptor() {
            void process(HttpRequest httpRequest, HttpContext context) {
                httpRequest.setHeader('X-CLIENT_HIT_ID', generateHitId())
                defaultHeaders?.each { String key, String value ->
                    httpRequest.setHeader(key, value)
                }
            }
        })
        def client = new RESTClient(host)
        client.handler.failure = { resp, data ->
            resp.setData(data)
            return resp
        }
        client.client = httpClientBuilder.build()
        return client
    }
}
