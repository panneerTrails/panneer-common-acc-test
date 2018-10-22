package com.mrll.javelin.common.test.client

import spock.lang.Specification

class TestPcfTestClient extends Specification {


    void 'uses default props'() {
        given:
        System.properties['PCF_PWD'] = 'test'

        PcfTestClient subject = new PcfTestClient()

        expect:
        subject.pcfApiDomain == PcfTestClient.LOCAL_PCF_API_DOMAIN
        subject.pcfOrg == PcfTestClient.LOCAL_PCF_ORG
        subject.pcfSpace == PcfTestClient.LOCAL_PCF_SPACE
        subject.pcfUser == PcfTestClient.LOCAL_PCF_USER
        subject.pcfPwd == 'test'
    }

    void 'uses all props from system.env'() {
        given:
        System.properties['PCF_API_DOMAIN'] = 'test'
        System.properties['PCF_ORG'] = 'test'
        System.properties['PCF_SPACE'] = 'test'
        System.properties['PCF_USER'] = 'test'
        System.properties['PCF_PWD'] = 'test'

        PcfTestClient subject = new PcfTestClient()

        expect:
        subject.pcfApiDomain == 'test'
        subject.pcfOrg == 'test'
        subject.pcfSpace == 'test'
        subject.pcfUser == 'test'
        subject.pcfPwd == 'test'
    }
}
