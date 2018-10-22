package com.mrll.javelin.common.test.config

import spock.util.concurrent.PollingConditions

/**
 * Extend this class and replace the configByEnvironment Map with the values you need.
 * @author bshults
 */
abstract class AbstractAcceptanceSpecConfigurationDefault {
    final String DEFAULT_LOCAL_DEV_DOMAIN = 'apps.us2.devg.foundry.mrll.com'

    String targetEnvironment
    String targetService
    String targetDomain
    String targetSuffix
    String targetHost
    String environmentKey = 'dev'

    Map configByEnvironment
    Map jwtConfigByEnvironment
    EnvironmentCommon environmentCommon

    /**
     *  Override this in the extending class to set this map with service specific environment variables!!!
     */
    protected void initMap() {
        configByEnvironment = [
                local : [],
                dev   : [],
                pr    : [],
                stage : [],
                prod  : [],
                prodeu: []
        ]

    }

    /**
     *  This can be overridden if necessary, but services calling should probably be okay with defaults
     */
    protected void initJwtMap() {
        jwtConfigByEnvironment = [
                local : [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectDetSvcAccDev-Boss@mailinator.com', password: 'Password1!', projectId: '58da52cdf838160014450b41',
                                        userId: '424a91bf-e69e-42ff-b906-fa1eef9dc850', firstName: 'ProjectDetSvc', lastName: 'AccDev', roleId: '58da52cdb7b5890014cca2db', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ],

                pr    : [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectDetSvcAccDev-Boss@mailinator.com', password: 'Password1!', projectId: '58da52cdf838160014450b41',
                                        userId: '424a91bf-e69e-42ff-b906-fa1eef9dc850', firstName: 'ProjectDetSvc', lastName: 'AccDev', roleId: '58da52cdb7b5890014cca2db', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ],

                dev   : [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectDetSvcAccDev-Boss@mailinator.com', password: 'Password1!', projectId: '58da52cdf838160014450b41',
                                        userId: '424a91bf-e69e-42ff-b906-fa1eef9dc850', firstName: 'ProjectDetSvc', lastName: 'AccDev', roleId: '58da52cdb7b5890014cca2db', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ],

                stage : [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectdetsvcaccstage-boss@mailinator.com', password: 'Password1!', projectId: '58f4d0abfb8f37000f2bb4ae',
                                        userId: 'b4f9b606-540b-43dc-ad9e-bed86d647da3', firstName: 'AccTestUser', lastName: 'Stage', roleId: '58f4d0ab25f7ed000f0c6e9f', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ],

                produs: [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectdetsvcaccprod-boss@mailinator.com', password: 'Password1!', projectId: '58f4d2556fa7940013dd9cea',
                                        userId: 'aca93459-5e9e-4cf4-b9cd-77a4fdc1ca3e', firstName: 'AccTest', lastName: 'UserProd', roleId: '58f4d255d69c1b0011485a10', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ],

                prodeu: [
                        environmentCommon: new EnvironmentCommon(
                                adminUser: new TestUser(email: 'projectdetsvcaccprod-boss@mailinator.com', password: 'Password1!', projectId: '5b4d036a315bc700137b5f96',
                                        userId: 'aca93459-5e9e-4cf4-b9cd-77a4fdc1ca3e', firstName: 'AccTest', lastName: 'UserProd', roleId: '﻿5b4d036b70ab86000eb71396', roleName: 'Project Admin'),
                                crudRetryInSeconds: 4d,
                                pollingConditions: new PollingConditions(timeout: 8d, initialDelay: 0.5d, factor: 1.5d)
                        )
                ]
        ]
    }

    AbstractAcceptanceSpecConfigurationDefault(localPort) {
        targetService = getSystemValue('TARGET_SERVICE')
        targetDomain = getSystemValue('TARGET_DOMAIN', DEFAULT_LOCAL_DEV_DOMAIN)
        targetEnvironment = getSystemValue('TARGET_ENVIRONMENT', 'local')
        targetSuffix = buildTargetSuffix()
        targetHost = (targetEnvironment == 'local') ? "http://localhost:$localPort" : "http://${targetService}${targetSuffix}.${targetDomain}"
        println "INFO: target-service: $targetHost"

        setupEnvironmentKey()
        initMap()
        initJwtMap()

        environmentCommon = jwtConfigByEnvironment[environmentKey].environmentCommon
        environmentCommon.tokenServiceUrl = buildServiceUrl('token-service')
    }

    protected String buildServiceUrl(String serviceName) {
        "http://${serviceName}${targetSuffix}.${targetDomain}"
    }

    protected String buildTargetSuffix() {
        String targetSuffix = ''
        if (targetEnvironment == 'stage') {
            targetSuffix = '-stage'
        } else if (targetEnvironment.toLowerCase().contains('prod')) {
            targetSuffix = '-prod'
        }
        targetSuffix
    }

    protected def getSystemValue(String systemValueKey, String defaultValue = '') {
        System.env."$systemValueKey" ?: System.properties."$systemValueKey" ?: defaultValue
    }

    protected void setupEnvironmentKey() {
        boolean pr = getSystemValue('PR')
        if (targetEnvironment == 'dev') {
            environmentKey = pr ? 'pr' : 'dev'
        } else {
            environmentKey = targetEnvironment.toLowerCase()
        }
    }
}
