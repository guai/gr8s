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

Some hints are available:
![idea screenshot](ctrl-q.png)

The DSL is generated from OpenShift's swagger spec, which you can get with `oc get --raw /openapi/v2 > swagger.json`
