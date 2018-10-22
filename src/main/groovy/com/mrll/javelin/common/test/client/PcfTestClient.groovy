package com.mrll.javelin.common.test.client

import org.cloudfoundry.operations.CloudFoundryOperations
import org.cloudfoundry.operations.DefaultCloudFoundryOperations
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest
import org.cloudfoundry.reactor.DefaultConnectionContext
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider
import org.cloudfoundry.reactor.uaa.ReactorUaaClient


class PcfTestClient {

    static final String LOCAL_PCF_API_DOMAIN = 'api.sys.us2.devb.foundry.mrll.com'
    static final String LOCAL_PCF_ORG = 'us2-datasiteone'
    static final String LOCAL_PCF_SPACE = 'devb'
    static final String LOCAL_PCF_USER = 'svc_inf_jenkins'

    private String pcfApiDomain
    private String pcfOrg
    private String pcfSpace
    private String pcfUser
    private String pcfPwd

    private CloudFoundryOperations cloudFoundryOperations

    PcfTestClient() {
        pcfApiDomain = System.env.'PCF_API_DOMAIN' ?: System.properties.'PCF_API_DOMAIN' ?: System.env.'PCF_URI' ?: System.properties.'PCF_URI' ?: LOCAL_PCF_API_DOMAIN
        pcfOrg = System.env.'PCF_ORG' ?: System.properties.'PCF_ORG' ?: LOCAL_PCF_ORG
        pcfSpace = System.env.'PCF_SPACE' ?: System.properties.'PCF_SPACE' ?: LOCAL_PCF_SPACE
        pcfUser = System.env.'PCF_USER' ?: System.properties.'PCF_USER' ?: LOCAL_PCF_USER
        pcfPwd = System.env.'PCF_PWD' ?: System.properties.'PCF_PWD'

        cloudFoundryOperations = cloudFoundryOperations()
    }


    Map getAppEnv(String appName) {
        GetApplicationEnvironmentsRequest request =
                GetApplicationEnvironmentsRequest.builder()
                        .name(appName)
                        .build()
        return cloudFoundryOperations.applications().getEnvironments(request).block().properties
    }


    private DefaultConnectionContext connectionContext() {
        return DefaultConnectionContext.builder()
                .apiHost(pcfApiDomain)
                .build()
    }

    private PasswordGrantTokenProvider tokenProvider() {
        return PasswordGrantTokenProvider.builder()
                .password(pcfPwd)
                .username(pcfUser)
                .build()
    }

    private ReactorCloudFoundryClient cloudFoundryClient() {
        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext())
                .tokenProvider(tokenProvider())
                .build()
    }

    ReactorDopplerClient dopplerClient() {
        return ReactorDopplerClient.builder()
                .connectionContext(connectionContext())
                .tokenProvider(tokenProvider())
                .build()
    }

    private ReactorUaaClient uaaClient() {
        return ReactorUaaClient.builder()
                .connectionContext(connectionContext())
                .tokenProvider(tokenProvider())
                .build()
    }


    private DefaultCloudFoundryOperations cloudFoundryOperations() {
        return DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cloudFoundryClient())
                .dopplerClient(dopplerClient())
                .uaaClient(uaaClient())
                .organization(pcfOrg)
                .space(pcfSpace)
                .build()
    }
}
