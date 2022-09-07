package com.example.convention;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConventionController.class, properties = { "spring.jackson.serialization.indent-output=true" })
@Import(ConventionHandler.class)
class ConventionControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void conventionApplied() throws Exception {
		final byte[] content = StreamUtils.copyToByteArray(new ClassPathResource("request.json").getInputStream());
		this.mockMvc.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").isNotEmpty())
				.andExpect(jsonPath("$.status.template").isNotEmpty())
				.andExpect(jsonPath("$.status.template.metadata").isNotEmpty())
				.andExpect(jsonPath("$.status.template.metadata.annotations").isNotEmpty())
				.andExpect(jsonPath("$.status.template.metadata.annotations['inspect-image.buildpacks.io/buildpacks']").value("""
						- id: paketo-buildpacks/ca-certificates
						  version: 3.0.1
						- id: paketo-buildpacks/bellsoft-liberica
						  version: 9.0.1
						- id: paketo-buildpacks/syft
						  version: 1.2.0
						- id: paketo-buildpacks/maven
						  version: 6.0.1
						- id: paketo-buildpacks/executable-jar
						  version: 6.0.1
						- id: paketo-buildpacks/apache-tomcat
						  version: 7.0.2
						- id: paketo-buildpacks/dist-zip
						  version: 5.0.1
						- id: paketo-buildpacks/spring-boot
						  version: 5.2.0
						""".trim()))
				.andExpect(jsonPath("$.status.template.metadata.annotations['inspect-image.buildpacks.io/base-image']").value("""
						reference: 0d382d05205978c348b29b35331bbbe0200b93a4b55f33934cf6131dbaa86337
						top_layer: sha256:d7467baf869b85ae4a6df9a7f06f008bf1e41c4d5f4916c399e602a94d0b7cc4
						""".trim()))
				.andExpect(jsonPath("$.status.template.metadata.annotations['inspect-image.buildpacks.io/run-image']").value("index.docker.io/paketobuildpacks/run:tiny-cnb"))
				.andExpect(jsonPath("$.status.template.metadata.annotations['inspect-image.opencontainers.org/source']").value("tanzu/7a7641687498e837a34a3e07964bab589285084d"))
				.andExpect(jsonPath("$.status.appliedConventions").isArray())
				.andExpect(jsonPath("$.status.appliedConventions.length()").value(4))
				.andExpect(jsonPath("$.status.appliedConventions[0]").value("buildpacks"))
				.andExpect(jsonPath("$.status.appliedConventions[1]").value("base-image"))
				.andExpect(jsonPath("$.status.appliedConventions[2]").value("run-image"))
				.andExpect(jsonPath("$.status.appliedConventions[3]").value("source"));
	}

	@Test
	void conventionNotApplied() throws Exception {
		final String content = """
				{
				  "apiVersion": "conventions.apps.tanzu.vmware.com/v1alpha1",
				  "kind": "PodIntent",
				  "metadata": {
				    "name": "spring-music",
				    "namespace": "demo"
				  },
				  "spec": {
				    "serviceAccountName": "default",
				    "template": {
				      "spec": {
				        "containers": [
				          {
				            "image": "ghcr.io/making/spring-music-demo@sha256:65403885732b4973cf9ed4dbacfc2e085096a115b5f8d55f87a7d58cd6f9fcb3",
				            "name": "workload",
				            "env": [
				              {
				                "name": "CONVENTION_SERVER",
				                "value": "demo"
				              }
				            ]
				          }
				        ],
				        "serviceAccountName": "default"
				      }
				    }
				  }
								}""";
		this.mockMvc.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").isNotEmpty())
				.andExpect(jsonPath("$.status.template").isNotEmpty())
				.andExpect(jsonPath("$.status.template.spec").isNotEmpty())
				.andExpect(jsonPath("$.status.template.spec.containers").isArray())
				.andExpect(jsonPath("$.status.template.spec.containers.length()").value(1))
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env").isArray())
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env.length()").value(1))
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env[0].name").value("CONVENTION_SERVER"))
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env[0].value").value("demo"))
				.andExpect(jsonPath("$.status.appliedConventions").isArray())
				.andExpect(jsonPath("$.status.appliedConventions.length()").value(0));
	}
}