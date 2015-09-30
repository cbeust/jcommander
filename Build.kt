import com.beust.kobalt.*
import com.beust.kobalt.internal.testConfig
import com.beust.kobalt.plugin.java.*
import com.beust.kobalt.plugin.packaging.packaging
import com.beust.kobalt.plugin.publish.jcenter

val jcommander = javaProject {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.99"
    directory = homeDir("java/jcommander")

    sourceDirectories {
        path("src/main/java")
    }
    sourceDirectoriesTest {
        path("src/test/java")
        path("src/test/resources")
    }
    dependencies {
        compile("com.beust:jcommander:1.48")
    }
    dependenciesTest {
        compile("org.testng:testng:6.9.5")
    }
}

val pack = packaging(jcommander) {
    jar {
    }
}
