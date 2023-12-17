

object This {
    val version = "1.84"
    val artifactId = "jcommander"
    val groupId = "org.jcommander"
    val description = "Command line parsing library for Java"
    val url = "https://jcommander.org"
    val scm = "github.com/cbeust/jcommander"

    // Should not need to change anything below
    val issueManagementUrl = "https://$scm/issues"
}


val kotlinVersion = "1.3.50"

allprojects {
    group = This.groupId
    version = This.version
    apply<MavenPublishPlugin>()
    tasks.withType<Javadoc> {
        options {
            quiet()
//            outputLevel = JavadocOutputLevel.QUIET
//            jFlags = listOf("-Xdoclint:none", "foo")
//            "-quiet"
        }
    }
}

val kotlinVer by extra { kotlinVersion }

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2") }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    jcenter()
    mavenCentral()
    maven { setUrl("https://plugins.gradle.org/m2") }
}

plugins {
    java
    `java-library`
    `maven-publish`
    signing
    id("com.jfrog.bintray") version "1.8.3" // Don't use 1.8.4, crash when publishing
    id("biz.aQute.bnd.builder") version "5.1.2"
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Bundle-Description" to "A Java library to parse command line options",
                    "Bundle-License" to "http://www.apache.org/licenses/LICENSE-2.0.txt",
                    "Bundle-Name" to "com.beust.jcommander",
                    "Export-Package" to "*;-split-package:=merge-first;-noimport:=true"
                )
            )
        }
    }
}

dependencies {
    listOf("org.testng:testng:7.0.0", "com.fasterxml.jackson.core:jackson-core:2.13.1",
        "com.fasterxml.jackson.core:jackson-annotations:2.13.1")
            .forEach { testImplementation(it) }
}

tasks.withType<Test> {
     useTestNG()
}

//
// Releases:
// ./gradlew bintrayUpload (to JCenter)
// ./gradlew publish (to Sonatype, then go to https://s01.oss.sonatype.org/index.html#stagingRepositories to publish)
// Make sure that ~/.gradle/gradle.properties:
//     signing.keyId=XXXXXXXX
//     signing.password=
//     signing.secretKeyRingFile=../../.gnupg/secring.gpg
// lists a key that's listed in the keyring
// (gpg --list-keys, last eight digits of the key)
//

bintray {
    user = project.findProperty("bintrayUser")?.toString()
    key = project.findProperty("bintrayApiKey")?.toString()
    dryRun = false
    publish = false

    setPublications("custom")

    with(pkg) {
        repo = "maven"
        name = This.artifactId
        with(version) {
            name = This.version
            desc = This.description
            with(gpg) {
                sign = true
            }
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

with(publishing) {
    publications {
        create<MavenPublication>("custom") {
            groupId = This.groupId
            artifactId = This.artifactId
            version = project.version.toString()
            afterEvaluate {
                from(components["java"])
            }
            suppressAllPomMetadataWarnings()
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name.set(This.artifactId)
                description.set(This.description)
                url.set(This.url)
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set(This.issueManagementUrl)
                }
                developers {
                    developer {
                        id.set("cbeust")
                        name.set("Cedric Beust")
                        email.set("cedric@beust.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://${This.scm}.git")
                    url.set("https://${This.scm}")
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            url = if (This.version.contains("SNAPSHOT"))
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") else
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("sonatypeUser")?.toString() ?: System.getenv("SONATYPE_USER")
                password = project.findProperty("sonatypePassword")?.toString() ?: System.getenv("SONATYPE_PASSWORD")
            }
        }
        maven {
            name = "myRepo"
            url = uri("file://$buildDir/repo")
        }
    }
}

with(signing) {
    sign(publishing.publications.getByName("custom"))
}
