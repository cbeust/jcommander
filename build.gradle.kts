

object This {
    val version = "1.79"
    val artifactId = "jcommander"
    val groupId = "com.beust"
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
}

dependencies {
    listOf("org.testng:testng:7.0.0")
        .forEach { testCompile(it) }
}

//
// Releases:
// ./gradlew bintrayUpload (to JCenter)
// ./gradlew publish (to Sonatype, then go to https://oss.sonatype.org/index.html#stagingRepositories to publish)
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
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name.set(This.artifactId)
                description.set(This.description)
                url.set(This.url)
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
        mavenLocal()
        maven {
            name = "sonatype"
            url = if (This.version.contains("SNAPSHOT"))
                uri("https://oss.sonatype.org/content/repositories/snapshots/") else
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("sonatypeUser") as? String
                password = project.findProperty("sonatypePassword") as? String
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
