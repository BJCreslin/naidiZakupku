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
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:BJCreslin/naidiZakupku.git'
            }
        }

        stage('Build') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    script {
                        def gradleScript = '''#!/bin/bash
                        chmod +x gradlew

                        heartbeat() {
                            while true; do
                                echo "[Jenkins Heartbeat] $(date)"
                                sleep 30
                            done
                        }

                        heartbeat &
                        HB_PID=$!

                        # Используем временный файл для кода выхода
                        EXIT_CODE_FILE=gradle-exit-code.tmp

                        (./gradlew clean build --build-cache --daemon --console=plain; echo $? > $EXIT_CODE_FILE) | tee build_output.log

                        kill $HB_PID || true
                        wait $HB_PID 2>/dev/null || true

                        # Считываем код выхода
                        BUILD_EXIT_CODE=$(cat $EXIT_CODE_FILE)
                        rm -f $EXIT_CODE_FILE

                        exit $BUILD_EXIT_CODE
                        '''

                        def result = sh(script: gradleScript, returnStatus: true)
                        if (result != 0) {
                            error("Gradle build failed with exit code ${result}")
                        }
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

        stage('Build Docker Image') {
            steps {
                script {
                    def jar = sh(script: "ls build/libs/*.jar | head -n 1", returnStdout: true).trim()
                    if (!jar) {
                        error("No JAR found in build/libs/")
                    }
                    env.JAR_NAME = jar
                    echo "Using JAR: $JAR_NAME"

                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh """
                           docker stop ${CONTAINER_NAME} || true
                           docker rm ${CONTAINER_NAME} || true
                           docker run -d --name ${CONTAINER_NAME} \\
                               -v /var/h2data:/data \\
                               -e NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN="${NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN}" \\
                               -e GIGACHAT_AUTH_ID="${GIGACHAT_AUTH_ID}" \\
                               -e GIGACHAT_AUTH_CLIENT_SECRET="${GIGACHAT_AUTH_CLIENT_SECRET}" \\
                               --network monitoring-network \\
                               --add-host=host.docker.internal:host-gateway \\
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
