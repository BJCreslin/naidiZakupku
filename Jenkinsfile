pipeline {
    agent any

    environment {
        JAR_NAME = sh(script: "ls build/libs/*.jar | head -n 1", returnStdout: true).trim()  // Автоматически находит первый JAR
        APP_DIR = "/opt/myapp"
        PORT = "9000"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'git@github.com:BJCreslin/naidiZakupku.git'  // Укажи свой репозиторий
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    pkill -f $JAR_NAME || true
                    mkdir -p $APP_DIR
                    cp $JAR_NAME $APP_DIR/myapp.jar
                    nohup java -jar $APP_DIR/myapp.jar --server.port=$PORT > $APP_DIR/app.log 2>&1 &
                '''
            }
        }
    }
}
