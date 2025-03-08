pipeline {
    agent any

    environment {
        APP_DIR = "/opt/myapp"
        PORT = "9000"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url:'git@github.com:BJCreslin/naidiZakupku.git'  // Укажи свой репозиторий
            }
        }

   stage('Build') {
       steps {
           sh 'chmod +x gradlew'
           sh './gradlew clean build -x test'
           sh 'ls -l build/libs/'  // Выведет список файлов в build/libs
       }
   }

        stage('Set JAR_NAME') {
            steps {
                script {
                    // Получаем путь к первому JAR файлу
                    env.JAR_NAME = sh(script: "ls build/libs/*.jar | head -n 1", returnStdout: true).trim()
                    echo "Found JAR: $JAR_NAME"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (fileExists("$JAR_NAME")) {
                        sh '''
                            pkill -f $JAR_NAME || true
                            mkdir -p $APP_DIR
                            cp $JAR_NAME $APP_DIR/myapp.jar
                            printenv
                            echo "Starting application..."
                            nohup java -jar $APP_DIR/myapp.jar --server.port=$PORT > $APP_DIR/app.log 2>&1 </dev/null & disown
                        '''
                    } else {
                        error "JAR file not found: $JAR_NAME"
                    }
                }
            }
        }
    }
}
