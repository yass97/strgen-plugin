plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}

group = "io.github"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.jdom)
    implementation(libs.agp)
    testImplementation(libs.kotlin.test.junit)
    testRuntimeOnly(libs.junit.platform)
}

gradlePlugin {
    val generateStrGenPlugin by plugins.creating {
        id = "io.github.yass97.strgen"
        implementationClass = "io.github.StrGenPlugin"
    }
}

publishing {
    publications {
        create<MavenPublication>("generateStrGen") {
            pom {
                distributionManagement {
                    relocation {
                        groupId = "io.github"
                        artifactId = "strgen"
                        version = "1.0.0"
                        message = "generate StrGen"
                    }
                }
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
