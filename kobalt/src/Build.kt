
import com.beust.kobalt.plugin.java.javaCompiler
import com.beust.kobalt.plugin.osgi.*
import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.publish.bintray
import com.beust.kobalt.project
import org.apache.maven.model.Developer
import org.apache.maven.model.License
import org.apache.maven.model.Model
import org.apache.maven.model.Scm

val jcommander = project {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.71"
    description = "A Java library to parse command line options"

    dependenciesTest {
        compile("org.testng:testng:6.10")
        exclude("com.beust:jcommander:1.48")
    }

    assemble {
        mavenJars {
        }
    }

    bintray {
        publish = true
        sign = true
    }

    javaCompiler {
        args("-target", "1.7", "-source", "1.7")
    }

    osgi {}

    pom = Model().apply {
        name = project.name
        description = "Command line parsing"
        url = "http://jcommander.org"
        licenses = listOf(License().apply {
            name = "Apache 2.0"
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        })
        scm = Scm().apply {
            url = "http://github.com/cbeust/jcommander"
            connection = "https://github.com/cbeust/jcommander.git"
            developerConnection = "git@github.com:cbeust/jcommander.git"
        }
        developers = listOf(Developer().apply {
            name = "Cedric Beust"
            email = "cedric@beust.com"
        })
    }

}
