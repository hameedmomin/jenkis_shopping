def call(String COMPONENT) {
  pipeline {
    agent any
//    tools {
//      maven 'Maven'
//    }

    stages {

//      stage("Submit Code to Sonarqube"){
//        steps {
//          sh "sonar-scanner -Dsonar.projectKey=${COMPONENT} -Dsonar.sources=. -Dsonar.java.binaries=./target -Dsonar.host.url=http://172.31.85.253:9000 -Dsonar.login=74bd8706a850538692620d211d112ab674c8a65a"
//        }
//      }
//      stage('Quality Gate Status') {
//        steps {
//          sh "sonar-quality-gate.sh admin hameed 172.31.85.253 ${COMPONENT}"
//        }
//      }
      stage('Prepare Archive' ) {
        when { expression { sh([returnStdout: true, script: 'echo ${GIT_BRANCH} | grep tags || true' ]) } }
        steps {
          script {
            addShortText background: 'yellow', color: 'black', borderColor: 'yellow', text: "${GIT_BRANCH}"
          }
          sh """     
            cd static
            zip -r ../${COMPONENT}-`echo ${GIT_BRANCH}| awk -F / '{print \$NF}'`.zip * 
          """
        }
      }

      stage('Upload to Nexus') {
        when { expression { sh([returnStdout: true, script: 'echo ${GIT_BRANCH} | grep tags || true' ]) } }
        steps {
          sh "curl -f -v -u admin:hameed --upload-file ${COMPONENT}-`echo ${GIT_BRANCH}| awk -F / '{print \$NF}'`.zip http://172.31.82.132:8081/repository/${COMPONENT}/${COMPONENT}-`echo ${GIT_BRANCH}| awk -F / '{print \$NF}'`.zip"
        }
      }

      stage('Trigger Dev Deployment') {
        steps {
          script {
            env.APP_VERSION=sh([returnStdout: true, script: "echo -n ${GIT_BRANCH}| awk -F / '{print \$NF}'" ]).trim()
            print APP_VERSION
          }
          build job: 'Deployment', parameters: [string(name: 'ENVIRONMENT', value: 'dev'), string(name: 'COMPONENT', value: "${COMPONENT}"), string(name: 'APP_VERSION', value: "${APP_VERSION}")]
        }
      }
    }
  }
}