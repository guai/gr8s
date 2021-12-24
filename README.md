# gr8s

Gradle plugin which allows using typed DSL for generating kubernetes/openshift YAML files.  
Based on [kuberig](https://github.com/kuberig-io/kuberig)

## Usage

```
import io.github.guai.gr8s.GenerateYaml

plugins {
	id("io.github.guai.gr8s")
}

val generateYaml : GenerateYaml by tasks

generateYaml.apply {
	yamlFile("foo.yaml")

	doLast {
		dsl.v1.configMap("foo.yaml") {
			metadata {
				name("foo")
			}
			data("foo.txt", java.util.UUID.randomUUID().toString())
		}
	}
}
```

See more detailed example [here](https://github.com/guai/gr8s/blob/main/build.gradle.kts)