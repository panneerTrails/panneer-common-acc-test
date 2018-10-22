def pipeline

node('javelin') {
    // Set Variables needed by template
    env.githubRepoUrl="git@us2-github-1.adminsys.mrll.com:Javelin/javelin-common-acc-test.git"
    env.jarGroupId="com.mrll.javelin"
    env.jarArtifactId="javelin-common-acc-test"
    env.sonarBuildName="default"
    env.prNotificationRoom="3737072"
    // Fetch and execute template
    env.sonarBuildName="default"
    env.hasAcceptanceTests=false
    git url: "git@us2-github-1.adminsys.mrll.com:Javelin/jenkins-templates.git"
    pipeline = load 'jenkins-pipeline-tools.groovy'
}

pipeline.libraryPipeline()
