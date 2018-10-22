package com.mrll.javelin.common.test.spec

import com.mrll.javelin.common.test.config.AbstractAcceptanceSpecConfigurationDefault
import com.mrll.javelin.common.test.config.JwtEngineerotron
import com.mrll.javelin.common.test.config.RestClientConfigurer
import com.mrll.javelin.common.test.config.TestUser
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

abstract class JavelinRestSpecification extends Specification {

    @Shared
    RestClientConfigurer restClientConfig = new RestClientConfigurer()
    @Shared
    RESTClient restClient
    @Shared
    JwtEngineerotron tokenator

    abstract <T extends AbstractAcceptanceSpecConfigurationDefault> T getConfig()

    /**
     * Call this after setting up your AbstractAcceptanceSpecConfigurationDefault
     */
    def setupSpecHelper(Map defaultHeaders) {
        restClient = restClientConfig.configure(getConfig().targetHost, defaultHeaders)
        tokenator = new JwtEngineerotron(getConfig())
    }

    def setupSpecHelper() {
        setupSpecHelper(null)
    }

    def String getToken() {
        return getToken(getConfig().environmentCommon.adminUser)
    }

    def String getToken(TestUser testUser){
        return new JwtEngineerotron(config).generateToken(testUser)
    }

    def Map generatePostBody(metadataId, payload, viewer = 'none', createDate = null, email = "valid@email.io") {
        return [
                metadataId : metadataId,
                authorEmail: email,
                createDate : createDate,
                viewer     : viewer,
                payload    : payload
        ]
    }
}