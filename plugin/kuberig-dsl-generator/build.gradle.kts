
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

sourceSets {
    main {
        java.srcDir("../kuberig-dsl-base/src/main/kotlin")
    }
    test {
        resources.srcDir("../openapi-specs")
    }
}

gradlePlugin {
    plugins {
        register("kuberig-dsl-generator") {
            id = "kuberig-dsl-generator"
            implementationClass = "io.kuberig.dsl.generator.KuberigDslGeneratorPlugin"
        }
    }
}

dependencies {
    implementation("io.swagger:swagger-parser:1.0.58")
    implementation("org.slf4j:slf4j-api:1.7.36")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}