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

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        ansiColor('xterm')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:BJCreslin/naidiZakupku.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // Запуск билда с псевдо-хартбитом и контролем ошибок
                    def result = sh(
                        script: '''#!/bin/bash
                            chmod +x gradlew

                            (
                              while true; do
                                echo "[Jenkins Heartbeat] $(date)"
                                sleep 30
                              done
                            ) &
                            HB_PID=$!

                            ./gradlew clean build -x test --no-daemon --console=plain | tee build_output.log
                            BUILD_EXIT_CODE=${PIPESTATUS[0]}

                            kill $HB_PID || true
                            wait $HB_PID 2>/dev/null || true

                            exit $BUILD_EXIT_CODE
                        ''',
                        returnStatus: true
                    )

                    if (result != 0) {
                        error("Gradle build failed with exit code ${result}")
                    }
                }
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
                    def jar = sh(script: "ls build/libs/*.jar | head -n 1", returnStdout: true).trim()
                    if (!jar) {
                        error("No JAR found in build/libs/")
                    }
                    env.JAR_NAME = jar
                    echo "Found JAR: $JAR_NAME"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Run Docker Container') {
            steps {
                sh """
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                    docker run -d --name ${CONTAINER_NAME} \\
                        -e NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN="${NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN}" \\
                        -e GIGACHAT_AUTH_ID="${GIGACHAT_AUTH_ID}" \\
                        -e GIGACHAT_AUTH_CLIENT_SECRET="${GIGACHAT_AUTH_CLIENT_SECRET}" \\
                        -p ${PORT}:${PORT} ${IMAGE_NAME}
                """
            }
        }
    }

    post {
        failure {
            echo "Pipeline failed. Check logs above for more details."
        }
    }
}