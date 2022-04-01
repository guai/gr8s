import io.github.guai.gr8s.Gr8sTask
import io.github.guai.gr8s.Gr8sPluginExtension
import io.k8s.apimachinery.pkg.apis.meta.v1.objectMeta
import nl.martijndwars.markdown.CompileMarkdownToHtmlTask
import kotlin.random.Random.Default.nextBytes

group = "com.example"
version = "9000"

plugins {
	id("io.github.guai.gr8s")
	id("nl.martijndwars.markdown") version "0.2.1"
}


the<Gr8sPluginExtension>().apply {
	outputDir = buildDir.resolve("gr8s") // you can configure output dir. 'build/gr8s' is the default
}

val gr8s: Gr8sTask by tasks // this task was created by the plugin

val route by tasks.creating(Gr8sTask::class) { // you can make more tasks
	doLast {
		dsl {
			// use kuberig's DSL here
		}
	}
}

// call gr8s to generate them all
require(gr8s.taskDependencies.getDependencies(gr8s).contains(route))


val compileMarkdownToHtml: CompileMarkdownToHtmlTask by tasks

compileMarkdownToHtml.apply {
	inputFile.set(file("README.md"))
	outputFile.set(file(buildDir.resolve("readme.html")))
}


val configMap by tasks.creating(Gr8sTask::class) {
	dependsOn(compileMarkdownToHtml)

	doLast {
		dsl {
			// first usage of this file
			v1.configMap("nginx-static-example-index.json") {
				metadata {
					name("html")
				}
				data("index.html", compileMarkdownToHtml.outputFile.asFile.get().readText())
			}
			// second usage. content would be appended
			v1.secret("nginx-static-example-png.json") {
				metadata {
					name("image")
				}
				data("ctrl-q.png", file("ctrl-q.png").readBytes())
			}
		}
	}
}

val dockerRegistry = findProperty("dockerRegistry")
val deploymentHost = findProperty("deploymentHost") ?: "example.com"

val meta = objectMeta {
	name("nginx-static-example")
}

gr8s.apply {
	doLast {
		dsl {
			// same here. append
			apps.v1.deployment("nginx-static-example-deployment.json") {
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
											mountPath("/usr/share/nginx/html/index.html")
											name("html-volume")
											subPath("index.html")
										}
										volumeMount {
											mountPath("/usr/share/nginx/html/ctrl-q.png")
											name("image-volume")
											subPath("ctrl-q.png")
										}
									}
								}
							}
							volumes {
								volume {
									name("html-volume")
									configMap {
										name("html")
//										defaultMode(Integer.parseInt("744", 8))
									}
								}
								volume {
									name("image-volume")
									secret {
										secretName("image")
									}
								}
							}
						}
					}
				}
			}

			v1.service("nginx-static-example-service.json") {
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

route.apply {
	doLast {
		dsl {
			// this goes to its own file
			route.openshift.io.v1.route("route.json") {
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

		// or perhaps you want to print to console
		dsl(writerSupplier = { System.out.writer() }) {
			v1.secret("will be ignored") {
				data("random.txt", nextBytes(20))
			}
		}

	}
}

