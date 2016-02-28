import com.beust.kobalt.*
import com.beust.kobalt.plugin.java.*
import com.beust.kobalt.plugin.packaging.*
import com.beust.kobalt.plugin.publish.*

val jcommander = project {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.55"

    dependenciesTest {
        compile("org.testng:testng:6.9.10")
    }

    assemble {
        mavenJars {
        }
    }

    bintray {
        publish = true
    }
}
