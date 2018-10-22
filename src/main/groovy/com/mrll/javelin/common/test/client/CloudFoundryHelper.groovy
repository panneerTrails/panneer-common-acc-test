package com.mrll.javelin.common.test.client

class CloudFoundryHelper {
    PcfTestClient pcfTestClient

    CloudFoundryHelper() {
        pcfTestClient = new PcfTestClient()
    }

    /**
     *
     * @param appName
     * @return
     */
    String getRabbitUriFromPCF(String appName){
        Map envProps = pcfTestClient.getAppEnv(appName)
        String uri
        int length = envProps['systemProvided'].VCAP_SERVICES['p-rabbitmq'].credentials['uri'].size
        envProps['systemProvided'].VCAP_SERVICES['p-rabbitmq'].credentials['uri'].each { it ->
            if (it !=null){
                uri = it
            }
        }
        return uri
    }

    /**
     * 
     * @param appName
     * @return
     */
    String getMongoUriFromPCF(String appName){
        Map envProps = pcfTestClient.getAppEnv(appName)
        String uri
        int length = envProps['systemProvided'].VCAP_SERVICES['user-provided'].credentials['uri'].size
        envProps['systemProvided'].VCAP_SERVICES['user-provided'].credentials['uri'].each { it ->
            if (it !=null){
                uri = it
            }
        }
        return uri
    }

}
