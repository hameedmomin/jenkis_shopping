pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        SAMPLE_URL = 'google.com'
        USER_PASS = credentials("My_Credentials")
    }
    stages {
        stage (one) {
            steps {
                echo 'hello world'
                echo SAMPLE_URL
                sh "echo ${USER_PASS} | base64"
            }
        }
        stage (two) {
            steps {
            echo 'my universe'
            }
        }
        stage ('Maven') {
            when {
                beforeInput true
                branch 'production'
            }
            input {
                message "Should we continue?"
                ok "Yes, we should."
                submitter "alice,bob"
                parameters {
                    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
                }
            }
            steps {
                sh "mvn --version"
            }
        }
    }
}
