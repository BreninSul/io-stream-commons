import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    val kotlinVersion = "2.1.0"
    id("java")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.4"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "io.github.breninsul"
version = "1.0.4"


val javaVersion = JavaVersion.VERSION_17


tasks.named<Jar>("javadocJar") {
    from(tasks.named("dokkaJavadoc"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
java {
    sourceCompatibility = javaVersion
    withJavadocJar()
    withSourcesJar()
}
repositories {
    mavenCentral()
}
tasks.compileJava {
    dependsOn.add(tasks.processResources)
}
tasks.compileKotlin {
    dependsOn.add(tasks.processResources)
}

dependencies {
    api("io.netty:netty-buffer:4.2.117.Final")
    api("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
}

tasks.withType<DokkaTask>().configureEach {
    val repositoryName = project.name
    dokkaSourceSets {
        named("main") {
            moduleName.set(project.name)
            includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/BreninSul/$repositoryName"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = javaVersion.majorVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

signing {
    useGpgCmd()
}

centralPortal {
    pom {
        packaging = "jar"
        name.set("BreninSul IO Stream Commons")
        val repositoryName = project.name
        url.set("https://github.com/BreninSul/$repositoryName")
        description.set(
            """
            """.trimIndent(),
        )
        licenses {
            license {
                name.set("MIT License")
                url.set("http://opensource.org/licenses/MIT")
            }
        }
        scm {
            connection.set("scm:https://github.com/BreninSul/$repositoryName.git")
            developerConnection.set("scm:git@github.com:BreninSul/$repositoryName.git")
            url.set("https://github.com/BreninSul/$repositoryName")
        }
        developers {
            developer {
                id.set("BreninSul")
                name.set("BreninSul")
                email.set("brenimnsul@gmail.com")
                url.set("breninsul.github.io")
            }
        }
    }
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}
