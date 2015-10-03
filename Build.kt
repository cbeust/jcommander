import com.beust.kobalt.*
import com.beust.kobalt.plugin.java.*
import com.beust.kobalt.plugin.packaging.*
import com.beust.kobalt.plugin.publish.*

val jcommander = javaProject {
    name = "jcommander"
    group = "com.beust"
    artifactId = name
    version = "1.52"

    dependenciesTest {
        compile("org.testng:testng:6.9.5")
    }
}

val pack = assemble(jcommander) {
    mavenJars {
    }
}

val jcenter = jcenter(jcommander) {
    publish = false
}
