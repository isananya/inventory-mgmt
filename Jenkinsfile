pipeline {
    agent any
    environment {
        DOCKER_HOST = 'tcp://localhost:2375'
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/isananya/inventory-mgmt.git'
            }
        }

        stage('Build'){
            steps{
                dir('backend') {
                    bat "mvn -Dmaven.test.failure.ignore=true -DskipTests=true package"
                    
                    bat "docker build -t api-gateway ./api-gateway"
                    bat "docker build -t billing-service ./billing-service"
                    bat "docker build -t order-service ./order-service"
                    bat "docker build -t config-server ./config-server"
                    bat "docker build -t email-service ./email-service"
                    bat "docker build -t eureka-server ./eureka-server"
                    bat "docker build -t inventory-service ./inventory-service"
                    bat "docker build -t user-service ./user-service"
                }
            }
        }
        stage('Docker Compose)') {
            steps {
                dir('backend') {
                    bat "docker-compose up -d"
                }
            }
        }
    }
}