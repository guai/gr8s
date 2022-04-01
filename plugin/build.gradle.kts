import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.guai"
version = "2.0"
description = "Gradle plugin which allows using typed DSL for generating kubernetes/openshift definition files"


plugins {
    kotlin("jvm") version "1.6.10"
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.18.0"
    `maven-publish`
    id("kuberig-dsl-generator")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

buildscript {
    dependencies {
        classpath(platform("org.jetbrains.kotlin:kotlin-bom:1.6.10"))
    }
}

gradlePlugin {
    plugins.register("gr8s") {
        id = "io.github.guai.gr8s"
        implementationClass = "io.github.guai.gr8s.Gr8sPlugin"
    }
}

pluginBundle {
    website = "https://github.com/guai/gr8s"
    vcsUrl = "https://github.com/guai/gr8s.git"

    description = project.description

    (plugins["gr8s"]).apply {

        displayName = "kubernetes/openshift typed DSL plugin"
        tags = listOf("kubernetes", "k8s", "openshift")
        version = project.version.toString()
    }
}

sourceSets {
    main {
        java.srcDir("kuberig-dsl-base/src/main/kotlin")
        java.srcDir("kuberig-core/src/main/kotlin")
        java.srcDir("kuberig-annotations/src/main/kotlin")
        java.srcDir("moshi-kotlin/src/main/kotlin")
        resources.srcDir("openapi-specs")
        java.srcDir(buildDir.resolve("generated-src/main/kotlin"))
    }
}

val generateDsl = tasks.withType<io.kuberig.dsl.generator.KuberigDslGeneratorTask>().first()

generateDsl.apply {
    swaggerFile.set(projectDir.resolve("openapi-specs/openshift/swagger-4.6.0.json"))
}

tasks.withType<KotlinCompile> {
    dependsOn(generateDsl)
}

dependencies {
    api(kotlin("reflect"))
    api("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.okio:okio:3.0.0")

    implementation("io.swagger:swagger-parser:1.0.58")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks["javadoc"].enabled = false