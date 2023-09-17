pipeline{
    agent {
         label 'AgentDocker'
    }
    /*
    tools {
         maven 'maven 3.6'
         jdk 'java'
    }
    */
    environment {
        // This can be nexus3 or nexus2
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
        NEXUS_URL = "192.168.20.17:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "maven-releases"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus"
        
        // Workfolder
        //WORKFOLDER = "/usr/jenkins/node_agent/workspace"
    }

    stages{
        stage('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'Github_x64-vm02', url: 'git@github.com:gsmx64/holamundose.git']]])
            }
        }
        stage('Download artifact from nexus'){
            agent {
                label 'AgentDocker'
            }
            steps{
                sh '''
                    pwd 
                    curl -v -u admin:Aa12162389 -o app.jar http://192.168.20.17:8081/repository/maven-public/org/springframework/jb-hello-world-maven/0.2.1/jb-hello-world-maven-0.2.1.jar
                '''
            }
        }
        stage('Build container'){
            agent {
                label 'AgentDocker'
            }
            steps{
                sh '''
                    docker build -t holamundose .
                '''

            }
        } //fin stage build container
        stage('Deploy container'){
            agent {
                label 'AgentDocker'
            }
            steps{
                sh '''
                    docker run -d --name holamundose -p 8085:80 holamundose
                '''

            }
        } //fin stage build container
        
        stage("Post") {
            agent {
                label 'AgentDocker'
            }
            steps {
                sh '''
                    pwd
                    echo "Clean up workfolder"
                    rm -Rf *
                '''
            }
        } //fin stage post
        
    }
}
