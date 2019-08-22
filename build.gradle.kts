

val jcommanderVersion = "1.78"

allprojects {
    group = "com.beust"
    version = jcommanderVersion
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

buildscript {
    val kotlinVer by extra { "1.3.41" }

    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2") }
    }

//    dependencies {
//        classpath(kotlin("gradle-plugin", kotlinVer))
//    }
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

val kotlinVer by extra { "1.3.41" }

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
    publish = true

    setPublications("custom")

    with(pkg) {
        repo = "maven"
        name = "jcommander"
        with(version) {
            name = jcommanderVersion
            desc = "Command line parsing library for Java"
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
            groupId = "com.beust"
            artifactId = "jcommander"
            version = project.version.toString()
            afterEvaluate {
                from(components["java"])
            }
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name.set("jcommander")
                description.set("Command line parser library for Java")
                url.set("https://jcommander.org")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/cbeust/jcommander/issues")
                }
                developers {
                    developer {
                        id.set("cbeust")
                        name.set("Cedric Beust")
                        email.set("cedric@beust.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://example.com/my-library.git")
                    developerConnection.set("scm:git:ssh://example.com/my-library.git")
                    url.set("http://example.com/my-library/")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "sonatype"
            url = if (false) // isSnapshot
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
