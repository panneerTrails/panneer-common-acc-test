# Common Groovy Acceptance Test 
Provides a groovy based PCF client and RabbitMQ client for enhancing acceptance tests

**Note: Do not use version 0.1.000418, this points to incorrect variables**


#Features
0. Provides API clients to access MongoDB or Cloud Foundry
0. Provides helper class to parse out vcap apps config information
   Include this code snippet in AcceptanceSpecConfiguration.groovy class 
```
        if (targetEnvironment == 'local') {
            rabbitUri = 'amqp://localhost:5672'
            mongoUri = 'mongodb://localhost:27017'
        } else {
            rabbitUri = new CloudFoundryHelper().getRabbitUriFromPCF(targetService)
            mongoUri = new CloudFoundryHelper().getMongoUriFromPCF(targetService)
        }
```

## Run acceptance tests
1. Run the LOCAL tests: `./gradlew runAcceptance`
0. Run the PR tests: `./gradlew -DTARGET-SERVICE=${service name dev} -DTARGET_ENVIRONMENT=dev -DTARGET_DOMAIN=${dev domain in PCF} -DPR=true -DPCF_PWD=${some jenkins variable} runAcceptance`
0. Run the DEV tests: `./gradlew -DTARGET-SERVICE=${service name dev} -DTARGET_ENVIRONMENT=dev -DTARGET_DOMAIN=${dev domain in PCF} -DPCF_PWD=${some jenkins variable} runAcceptance`
0. Run the STAGE tests: `./gradlew -DTARGET-SERVICE=${service name stage} -DTARGET_ENVIRONMENT=stage -DTARGET_DOMAIN=${stage domain in PCF} -DPCF_PWD=${some jenkins variable} -DPCF_SPACE=stage runAcceptance`
0. Run the PROD US tests: `./gradlew -DTARGET-SERVICE=${service name prod} -DTARGET_ENVIRONMENT=prod -DTARGET_DOMAIN=${prod US domain in PCF} -DPCF_PWD=${some jenkins variable} -DPCF_SPACE=prod runAcceptance`
0. Run the PROD EU tests: `./gradlew -DTARGET-SERVICE=${service name prod} -DTARGET_ENVIRONMENT=prod -DTARGET_DOMAIN=${prod EU domain in PCF} -DPCF_PWD=${some jenkins variable} -DPCF_SPACE=prod -DPCF_ORG=eu2-datasiteone runAcceptance`

### Examples
1. Dev: `./gradlew -DTARGET-SERVICE=project-composite-service -DTARGET_ENVIRONMENT=dev -DTARGET_DOMAIN=apps.us2.devb.foundry.mrll.com runAcceptance`
0. Stage: `./gradlew -DTARGET-SERVICE=project-composite-service -DTARGET_ENVIRONMENT=stage -DTARGET_DOMAIN=apps.us2.devb.foundry.mrll.com runAcceptance`
0. Prod: `./gradlew -DTARGET-SERVICE=project-composite-service -DTARGET_ENVIRONMENT=prod -DTARGET_DOMAIN=apps.us2.prod.foundry.mrll.com runAcceptance`
    
## Using and extending the helper classes

In the `com.mrll.javelin.common.test.config` package are some classes that are helpful when writing acceptance tests.  Up to now, these have been duplicated in every project.  We can stop that now.

The `AbstractAcceptanceSpecConfigurationDefault` class configures and provides the following properties:

1. `String environmentKey      // e.g. 'dev', 'pr', 'stage', 'produs', 'prodeu'`
1. `String targetEnvironment   // e.g. 'dev', 'stage', 'produs', 'prodeu'. No longer to be used for configByEnvironment map!!`
1. `String targetService       // the service root where the subject of the tests is running`
1. `String targetSuffix        // stage or prod have an additional service suffix i.e. 'user-service-stage'`
1. `String targetDomain        // the domain where the subject of the tests is running. this can be used when overriding environmentMappings for service URLs.`
1. `String targetHost          // the domain:port, or the combination of targetService and targetDomain into a host URL of the subject of the tests`
1. `String targetTokenService  // the URL of the token service`
1. `TestUser adminUser         // the credentials (including projectId) of the test user`

There are also additional methods available for simplifying acceptance test configurations:

1. `buildServiceUrl(String serviceName)  // this builds the full url, with domain and applicable suffix. Services no longer need entries in environment mappings`

The `JwtEngineerotron` class provides tokens with a call as simple as `getToken()` which will get a token for the `adminUser`.  It has other methods as well if you want to customize the token.

The `RestClientConfigurer` class provides the following methods:

1. `generateHitId()` which returns a random hitId prefixed with `acc-test-`
2. `RESTClient configure(host)` returns a RESTClient for the given host with an failure handler configured in a useful way.

Here is an example configuration class using these helpers:
```groovy
class AcceptanceSpecConfiguration extends AbstractAcceptanceSpecConfigurationDefault {

    String uploadService
    TestUser reviwerUser

    void initMap() {
        configByEnvironment = [
            local: [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-rev-dev@mailinator.com',
                    password: 'Password1!',
                    projectId: '59c41c22469d6e000fef5298'
                )
            ],
            pr: [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-rev-dev@mailinator.com',
                    password: 'Password1!',
                    projectId: '59c41c22469d6e000fef5298'
                )
            ],
            dev  : [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-rev-dev@mailinator.com',
                    password: 'Password1!',
                    projectId: '59c41c22469d6e000fef5298'
                )
            ],
            stage: [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-stage-rev@mailinator.com',
                    password: 'Password1!',
                    projectId: '5a3be6526d6908001437a5e0'
                )
            ],
            produs: [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-prod-rev@mailinator.com',
                    password: 'Password1!',
                    projectId: '5a4e6b4e4fd6890013fc5219'
                )
            ],
            prodeu: [
                reviewerUser : new TestUser(
                    email: 'drmpermsacc-prod-rev@mailinator.com',
                    password: 'Password1!',
                    projectId: '5a5e72ce15a89700142e7105'
                )
            ]
        ]
    }

    AcceptanceSpecConfiguration() {
        super(PORT)
        uploadService = buildServiceUrl('upload-composite-service')
        reviewUser = configByEnvironment[environmentKey].reviewUser
    }
    
}
```
***Note that adminUser and tokenService are not longer needed.  But they can be overridden if necessary.***



```groovy
class RestServicesSpec extends JavelinRestSpecification {

    @Shared
    AcceptanceSpecConfiguration acceptanceSpecConfiguration

    def setupSpec() {
        acceptanceSpecConfiguration = new AcceptanceSpecConfiguration()
        setupSpecHelper()
    } 

    def setup() {
        hitId = restClientConfig.generateHitId()
        LOG.info('================================================================================================================================================================')
        LOG.info("Starting test: ${specificationContext.currentIteration.name}")
        LOG.info('================================================================================================================================================================')
    }

    def cleanup() {
        LOG.info('================================================================================================================================================================')
        LOG.info("Completing test: ${specificationContext.currentIteration.name}")
        LOG.info('================================================================================================================================================================')
    }



    @Unroll
    def "POST /api/projectId/{projectId}/metadataId/{metadataId}/comment?viewer={viewerCode} returns meaningful error response when #scenario"() {
        given:
        String jwtToken = getToken()

        when:
        def response = restClient.post(
                path: "/api/projectId/${getConfig().adminUser.projectId}/metadataId/metadataId5/comment",
                contentType: "application/json;charset=UTF-8",
                query: [viewer:viewerCode],
                headers: ['Authorization': 'Bearer ' + jwtToken,
                    'X-CLIENT_HIT_ID': restClientConfig.generateHitId()],
                body: generatePostBody(metadataId: metadataId, payload: [ comment:"not enough!", x: 23, y: 46 ], email: userEmail, createDate: createDate))

        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        def errorBody = response.data
        errorBody.error == "Bad Request"
        errorBody.message.startsWith(message)

        where:
        scenario                                       | metadataId    | viewerCode | userEmail                                     | createDate                               | message
        'URL does not match payload: metadataId'       | 'metadataId1' | 'none'     | getConfig().environmentCommon.adminUser.email | null                                     | 'metadataId in payload not matched in query.'
        'JWT user does not match payload: authorEmail' | 'metadataId5' | 'none'     | "valid@email.io"                              | null                                     | 'Currently, you can only submit your own comments.'
        'createDate in body is in the future'          | 'metadataId5' | 'none'     | getConfig().environmentCommon.adminUser.email | Instant.now().plusSeconds(20).toString() | 'createDate (if provided) must not be in the future.'
        'viewer in payload not matched in query'       | 'metadataId5' | 'pdfTron'  | getConfig().environmentCommon.adminUser.email | null                                     | 'viewer in payload not matched in query.'
    }

    def Map generatePostBody(Map arg) {
        return  [
            metadataId: arg.metadataId,
            authorEmail: arg.email ?: getConfig().environmentCommon.adminUser.email,
            createDate: arg.createDate,
            viewer: arg.viewer ?: 'none',
            payload: arg.payload
        ]
    }

    @Override
    public <T extends AbstractAcceptanceSpecConfigurationDefault> T getConfig() {
        return acceptanceSpecConfiguration
    }
}
```
