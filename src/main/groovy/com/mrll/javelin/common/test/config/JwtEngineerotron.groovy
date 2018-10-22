package com.mrll.javelin.common.test.config

import groovyx.net.http.RESTClient
import org.springframework.http.HttpStatus
import spock.util.concurrent.PollingConditions

class JwtEngineerotron {
    RESTClient restClient
    AbstractAcceptanceSpecConfigurationDefault configurationDefault
    PollingConditions pollingConditions
    double crudRetry

    JwtEngineerotron(AbstractAcceptanceSpecConfigurationDefault config) {
        this.configurationDefault = config
        this.pollingConditions = config.environmentCommon.pollingConditions
        restClient = new RESTClient(config.environmentCommon.tokenServiceUrl)
        crudRetry = config.environmentCommon.crudRetryInSeconds
    }

    String generateToken(String hitId = null) {
        if (!!configurationDefault) {
            generateToken(
                    configurationDefault.environmentCommon.adminUser.email,
                    configurationDefault.environmentCommon.adminUser.password,
                    configurationDefault.environmentCommon.adminUser.projectId,
                    hitId
            )
        } else {
            generateToken(config.adminUser.email, config.adminUser.password, config.adminUser.projectId, hitId)
        }
    }

    String generateToken(String emailAddress, String password, String projectId, String hitId = null) {
        Map tokenRequestBody = [
                password : password,
                username : emailAddress,
                projectId: projectId
        ]

        def jwtResponse = restClient.post(path: "/api/tokens/user/javelinJwt",
                headers: [
                        'X-CLIENT_HIT_ID': hitId
                ],
                contentType: "application/json;charset=UTF-8",
                body: tokenRequestBody
        )
        pollingConditions.within(crudRetry) {
            assert (jwtResponse?.status == HttpStatus.OK.value() || jwtResponse?.status != HttpStatus.NOT_FOUND)
            assert jwtResponse?.data
            assert jwtResponse?.data?.jwt
        }
        assert jwtResponse?.status == HttpStatus.OK.value()

        jwtResponse?.data?.jwt
    }

    String generateToken(TestUser testUser, String hitId = null){
        return generateToken(testUser.email, testUser.password, testUser.projectId, hitId)
    }

    // This is for processes which require internal credentials, like AD User operations. Use sparingly.
    String generateInternalToken(String emailAddress, String password, String group, String hitId = null) {
        Map tokenRequestBody = [
                group   : group,
                password: password,
                username: emailAddress
        ]

        def jwtResponse = restClient.post(path: "/api/tokens/internaluser/javelinJwt",
                headers: [
                        'X-CLIENT_HIT_ID': hitId
                ],
                contentType: "application/json;charset=UTF-8",
                body: tokenRequestBody)
        pollingConditions.within(crudRetry) {
            assert (jwtResponse?.status == HttpStatus.OK.value() || jwtResponse?.status != HttpStatus.NOT_FOUND)
            assert jwtResponse?.data
            assert jwtResponse?.data?.jwt
        }
        assert jwtResponse?.status == HttpStatus.OK.value()

        jwtResponse?.data?.jwt
    }

    String generateInternalToken(TestUser testUser, String hitId = null){
        return generateInternalToken(testUser.email, testUser.password, testUser.group, hitId)
    }

    String generateServiceToken(String serviceId, String password, String hitId = null) {
        Map tokenRequestBody = [
                servicePassword: password,
                serviceId      : serviceId
        ]

        def jwtResponse = restClient.post(path: "/api/tokens/javelin/service",
                headers: [
                        'X-CLIENT_HIT_ID': hitId
                ],
                contentType: "application/json;charset=UTF-8",
                body: tokenRequestBody)

        pollingConditions.within(crudRetry) {
            assert (jwtResponse?.status == HttpStatus.OK.value() || jwtResponse?.status != HttpStatus.NOT_FOUND)
            assert jwtResponse?.data
            assert jwtResponse?.data?.jwt
        }
        assert jwtResponse?.status == HttpStatus.OK.value()

        jwtResponse?.data?.jwt
    }
}
