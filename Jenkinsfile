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
                git branch: 'main', url:'git@github.com:BJCreslin/naidiZakupku.git'  // Укажи свой репозиторий
            }
        }

    stage('Build') {
       steps {
           sh 'chmod +x gradlew'
           sh './gradlew clean build -x test --no-daemon --console=plain || cat build/reports/*.log || true'
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

   stage('Build Docker Image') {
                    steps {
                        sh 'docker build -t myapp .'
                    }
                }

   stage('Run Docker Container') {
            steps {
                sh 'docker stop myapp-container || true'
                sh 'docker rm myapp-container || true'
                sh 'docker run -d --name myapp-container \
                -e NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN="$NAIDI_ZAKUPKU_TELEGRAM_BOT_TOKEN" \
                -e GIGACHAT_AUTH_ID="GIGACHAT_AUTH_ID" \
                -e GIGACHAT_AUTH_CLIENT_SECRET="GIGACHAT_AUTH_CLIENT_SECRET" \
                -p 9000:9000 myapp'
            }
        }
    }
}

