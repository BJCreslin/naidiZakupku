pipeline {
    agent any

    environment {
        APP_DIR = "/opt/myapp"
        PORT = "9000"
        IMAGE_NAME = "myapp"
        CONTAINER_NAME = "myapp-container"
        NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN = credentials('NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN')
        GIGACHAT_AUTH_ID = credentials('GIGACHAT_AUTH_ID')
        GIGACHAT_AUTH_CLIENT_SECRET = credentials('GIGACHAT_AUTH_CLIENT_SECRET')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:BJCreslin/naidiZakupku.git'
            }
        }

        stage('Build') {
                   steps {
                       sh '''
                           #!/bin/bash
                           chmod +x gradlew

                           # Запуск heartbeat в фоне
                           (while true; do echo ">>> Jenkins is alive: $(date)"; sleep 30; done) &
                           HEARTBEAT_PID=$!

                           # Запуск сборки
                           ./gradlew clean build -x test --no-daemon --console=plain | tee build_output.log
                           BUILD_EXIT_CODE=${PIPESTATUS[0]}

                           # Завершаем heartbeat
                           kill $HEARTBEAT_PID

                           # Возвращаем код завершения сборки
                           exit $BUILD_EXIT_CODE
                       '''
                   }
                   post {
                       always {
                           echo '=== Gradle Build Output ==='
                           sh 'cat build_output.log || true'
                       }
                   }
               }

        stage('Set JAR_NAME') {
            steps {
                script {
                    env.JAR_NAME = sh(script: "ls build/libs/*.jar | head -n 1", returnStdout: true).trim()
                    echo "Found JAR: $JAR_NAME"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t myapp .'
            }
        }

        stage('Run Docker Container') {
            steps {
                sh '''
                    docker stop myapp-container || true
                    docker rm myapp-container || true
                    docker run -d --name myapp-container \
                        -e NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN="$NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN" \
                        -e GIGACHAT_AUTH_ID="$GIGACHAT_AUTH_ID" \
                        -e GIGACHAT_AUTH_CLIENT_SECRET="$GIGACHAT_AUTH_CLIENT_SECRET" \
                        -p 9000:9000 myapp
                '''
            }
        }
    }
}