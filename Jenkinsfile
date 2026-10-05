// Publishes core, date-extension and bitsstrings-generator to Maven Central.
//
// Required Jenkins credentials:
//   maven-central-token              Username with password  Central Portal user token (name / password)
//   bitsstrings-signing-key          Secret file             ASCII-armored private key (gpg --armor --export-secret-keys)
//   bitsstrings-signing-key-password Secret text             passphrase of the signing key
//
// The version comes from `bitsstrings` in gradle/libs.versions.toml. Bump it before running this job.

pipeline {
    // iOS klibs (iosX64, iosArm64, iosSimulatorArm64) are built with Kotlin/Native, so the agent must run macOS with Xcode.
    agent { label 'macos' }

    parameters {
        booleanParam(
            name: 'AUTO_RELEASE',
            defaultValue: false,
            description: 'Release automatically after validation. If unchecked, the deployment must be published manually on central.sonatype.com.'
        )
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
    }

    environment {
        // Not secret: last 8 chars of key 8A34A6BA34A5A24D.
        ORG_GRADLE_PROJECT_signingInMemoryKeyId = '34A5A24D'
    }

    stages {
        stage('Test') {
            steps {
                sh './gradlew --no-daemon :bitsstrings-generator:test :date-extension:allTests'
            }
        }

        stage('Publish') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'maven-central-token',
                        usernameVariable: 'ORG_GRADLE_PROJECT_mavenCentralUsername',
                        passwordVariable: 'ORG_GRADLE_PROJECT_mavenCentralPassword'
                    ),
                    file(credentialsId: 'bitsstrings-signing-key', variable: 'SIGNING_KEY_FILE'),
                    string(credentialsId: 'bitsstrings-signing-key-password', variable: 'ORG_GRADLE_PROJECT_signingInMemoryKeyPassword'),
                ]) {
                    // Single-quoted so Groovy never interpolates the secrets, the shell expands them.
                    sh '''
                        set +x
                        export ORG_GRADLE_PROJECT_signingInMemoryKey="$(cat "$SIGNING_KEY_FILE")"
                        if [ "$AUTO_RELEASE" = "true" ]; then
                            TASK=publishAndReleaseToMavenCentral
                        else
                            TASK=publishToMavenCentral
                        fi
                        ./gradlew --no-daemon "$TASK"
                    '''
                }
            }
        }
    }
}
