plugins {
    `java-library`
    `maven-publish`
    signing
    jacoco
}

group = "su.nlq"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("com.hazelcast:hazelcast:3.12.12")
    api("com.hazelcast:hazelcast-client:3.12.12")
    api("org.testcontainers:testcontainers:1.15.3")
}

tasks {
    jacocoTestReport {
        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        reports {
            sourceDirectories.setFrom(files(sourceSets["main"].allSource.srcDirs))
            classDirectories.setFrom(files(sourceSets["main"].output))
            csv.required.set(false)
            html.required.set(false)
            xml.required.set(true)
            xml.outputLocation.set(File("$buildDir/reports/jacoco/report.xml"))
        }

        dependsOn("test")
    }

    test {
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set(project.name)
                description.set("Testcontainer for Hazelcast IMDG.")
                url.set("https://github.com/nolequen/hazelcast-testcontainer")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer {
                        name.set("Anton Potsyus")
                        email.set("nolequen@gmail.com")
                        url.set("http://www.nlq.su/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/nolequen/hazelcast-testcontainer.git")
                    developerConnection.set("scm:git:ssh://github.com/nolequen/hazelcast-testcontainer.git")
                    url.set("https://github.com/nolequen/hazelcast-testcontainer")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                password = project.findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
