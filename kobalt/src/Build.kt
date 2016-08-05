import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.publish.bintray
import com.beust.kobalt.project

val jcommander = project {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.56"

    dependenciesTest {
        compile("org.testng:testng:6.9.11")
    }

    assemble {
        mavenJars {
        }
    }

    bintray {
        publish = true
    }
}
