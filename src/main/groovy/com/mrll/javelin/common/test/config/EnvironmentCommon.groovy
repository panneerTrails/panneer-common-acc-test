package com.mrll.javelin.common.test.config

import spock.util.concurrent.PollingConditions

class EnvironmentCommon {
    TestUser adminUser
    String tokenServiceUrl
    PollingConditions pollingConditions
    double crudRetryInSeconds

}
