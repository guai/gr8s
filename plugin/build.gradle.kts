import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.guai"
version = "1.2"
description = "Gradle plugin which allows using typed DSL for generating kubernetes/openshift YAML files"


plugins {
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
    implementation("io.swagger:swagger-parser:1.0.58")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

dependencies {

    listOf(
            "com.fasterxml.jackson.core:jackson-core",
            "com.fasterxml.jackson.core:jackson-databind",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml",
            "com.fasterxml.jackson.module:jackson-module-kotlin",
    ).forEach {
        implementation(it) { version { strictly("2.11.4") } }
    }

    implementation("com.konghq:unirest-java:3.13.7")

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    implementation("com.jayway.jsonpath:json-path:2.7.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}
