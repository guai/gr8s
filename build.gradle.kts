import io.k8s.apimachinery.pkg.apis.meta.v1.objectMeta
import io.github.guai.gr8s.GenerateYaml
import io.github.guai.gr8s.Gr8sPluginExtension
import nl.martijndwars.markdown.CompileMarkdownToHtmlTask
import kotlin.random.Random.Default.nextBytes

group = "com.example"
version = "9000"

plugins {
	id("io.github.guai.gr8s")
	id("nl.martijndwars.markdown") version "0.2.1"
}


the<Gr8sPluginExtension>().apply {
	yamlDir = buildDir.resolve("yaml") // you can configure output dir. 'build/yaml' is the default
}

val generateYaml: GenerateYaml by tasks // this task was created by the plugin

val generateRouteYaml by tasks.creating(GenerateYaml::class) { // you can also make more tasks
	doLast {
		dsl {
			// use kuberig's DSL here
		}
	}
}

// call generateYaml to generate them all
require(generateYaml.taskDependencies.getDependencies(generateYaml).contains(generateRouteYaml))


val compileMarkdownToHtml: CompileMarkdownToHtmlTask by tasks

compileMarkdownToHtml.apply {
	inputFile.set(file("README.md"))
	outputFile.set(file(buildDir.resolve("readme.html")))
}


val fileToReuse = generateYaml.yamlFile("nginx-static-example.yaml")

val generateReadmeConfigMapYaml by tasks.creating(GenerateYaml::class) {
	dependsOn(compileMarkdownToHtml)

	doLast {
		// you can append to existing file
		dsl(fileToReuse) {
			// but then you still have to pass an empty string. that's how kuberig creates DSL
			v1.configMap("") {
				metadata {
					name("html-configmap")
				}
				data("index.html", compileMarkdownToHtml.outputFile.asFile.get().readText())
			}
		}

	}
}

val dockerRegistry = findProperty("dockerRegistry")
val deploymentHost = findProperty("deploymentHost") ?: "example.com"

val meta = objectMeta {
	name("nginx-static-example")
}

generateYaml.apply {
	doLast {
		// same file used in two tasks
		dsl(fileToReuse) {

			apps.v1.deployment("") {
				metadata(meta)
				spec {
					selector {
						matchLabels {
							matchLabel("app", "nginx")
						}
					}
					replicas(1)
					template {
						metadata {
							labels {
								label("app", "nginx")
							}
						}
						spec {
							containers {
								container {
									name("nginx-static-example")
									image(sequenceOf(dockerRegistry, "nginxinc/nginx-unprivileged").filterNotNull().joinToString("/"))
									ports {
										port {
											containerPort(8080)
										}
									}
									volumeMounts {
										volumeMount {
											mountPath("/usr/share/nginx/html")
											name("html-configmap-volume")
										}
									}
								}
							}
							volumes {
								volume {
									name("html-configmap-volume")
									configMap {
										name("html-configmap")
//										defaultMode(Integer.parseInt("744", 8))
									}
								}
							}
						}
					}
				}
			}

			v1.service("") {
				metadata(meta)
				spec {
					type("ClusterIP")
					ports {
						port {
							name("http")
							port(8080)
							protocol("TCP")
							targetPort(8080)
						}
					}
					selector("app", "nginx")
				}
			}
		}

	}
}

generateRouteYaml.apply {
	// or you can first declare a file
	yamlFile("route.yaml")

	doLast {
		// and then use it here by name
		dsl {
			route.openshift.io.v1.route("route.yaml") {
				metadata(meta)
				spec {
					host("nginx-static-example.${deploymentHost}")
					port {
						targetPort("http")
					}
					to {
						kind("Service")
						name("nginx-static-example")
						weight(100)
					}
				}
			}
		}

		// or perhaps you want to print the yaml to console
		dsl(writerSupplier = { System.out.writer() }) {
			v1.secret("") {
				data("random.txt", nextBytes(20))
			}
		}

	}
}
