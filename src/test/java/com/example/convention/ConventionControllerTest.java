package com.example.convention;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConventionController.class)
@Import(ConventionHandler.class)
class ConventionControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void conventionAppliedEnvNotExists() throws Exception {
		final byte[] content = StreamUtils.copyToByteArray(new ClassPathResource("request.json").getInputStream());
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
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env[0].value").value("HELLO FROM CONVENTION"))
				.andExpect(jsonPath("$.status.appliedConventions").isArray())
				.andExpect(jsonPath("$.status.appliedConventions.length()").value(1))
				.andExpect(jsonPath("$.status.appliedConventions[0]").value("add-env-var"));
	}

	@Test
	void conventionAppliedEnvExists() throws Exception {
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
				                "name": "PORT",
				                "value": "8080"
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
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env.length()").value(2))
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env[1].name").value("CONVENTION_SERVER"))
				.andExpect(jsonPath("$.status.template.spec.containers.[0].env[1].value").value("HELLO FROM CONVENTION"))
				.andExpect(jsonPath("$.status.appliedConventions").isArray())
				.andExpect(jsonPath("$.status.appliedConventions.length()").value(1))
				.andExpect(jsonPath("$.status.appliedConventions[0]").value("add-env-var"));
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