import com.beust.kobalt.*
import com.beust.kobalt.plugin.java.*
import com.beust.kobalt.plugin.packaging.*
import com.beust.kobalt.plugin.publish.*

val jcommander = javaProject {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.54"
    directory = homeDir("java/jcommander")

    dependencies {
        compile("com.google.code.gson:gson:2.4")
    }

    dependenciesTest {
        compile("org.testng:testng:6.9.5")
    }


    assemble {
        mavenJars {
        }
    }

    jcenter {
        publish = true
    }
}
