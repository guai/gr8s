group = "io.github.guai"
version = "1.4.1"
description = "Gradle plugin which allows using typed DSL for generating kubernetes/openshift YAML files"

repositories.addAll(rootProject.buildscript.repositories)

plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	id("com.gradle.plugin-publish") version "0.18.0"
	`maven-publish`
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

dependencies {
	implementation("io.kuberig:kuberig-core:0.0.47")
	api("io.kuberig:kuberig-dsl-base:0.1.6-RC5a")
	api("io.kuberig.dsl.kubernetes:kuberig-dsl-openshift-v4.6.0:0.1.4")

	api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0-rc3")
}
