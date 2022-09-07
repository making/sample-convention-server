package com.example.convention.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ImageConfigTest {
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void config() throws Exception {
		final byte[] contents = StreamUtils.copyToByteArray(new ClassPathResource("image-config.json").getInputStream());
		final ImageConfig imageConfig = objectMapper.readValue(contents, ImageConfig.class);
		final JsonNode config = imageConfig.config();
		final JsonNode labels = config.get("config").get("Labels");
		//assertThat(labels).hasSize(16);
		assertThat(labels.has("org.opencontainers.image.title")).isTrue();
		assertThat(labels.get("org.opencontainers.image.title").asText()).isEqualTo("petclinic");
		assertThat(labels.has("io.buildpacks.lifecycle.metadata")).isTrue();
		final JsonNode metadata = objectMapper.readValue(labels.get("io.buildpacks.lifecycle.metadata").asText(), JsonNode.class);
		assertThat(metadata.has("buildpacks")).isTrue();
		assertThat(metadata.get("buildpacks")).hasSize(8);
		assertThat(metadata.get("buildpacks").get(0).get("key").asText()).isEqualTo("paketo-buildpacks/ca-certificates");
		assertThat(metadata.get("buildpacks").get(0).get("version").asText()).isEqualTo("3.0.1");
		assertThat(metadata.get("buildpacks").get(1).get("key").asText()).isEqualTo("paketo-buildpacks/bellsoft-liberica");
		assertThat(metadata.get("buildpacks").get(1).get("version").asText()).isEqualTo("9.0.1");
		assertThat(metadata.get("buildpacks").get(2).get("key").asText()).isEqualTo("paketo-buildpacks/syft");
		assertThat(metadata.get("buildpacks").get(2).get("version").asText()).isEqualTo("1.2.0");
		assertThat(metadata.get("buildpacks").get(3).get("key").asText()).isEqualTo("paketo-buildpacks/maven");
		assertThat(metadata.get("buildpacks").get(3).get("version").asText()).isEqualTo("6.0.1");
		assertThat(metadata.get("buildpacks").get(4).get("key").asText()).isEqualTo("paketo-buildpacks/executable-jar");
		assertThat(metadata.get("buildpacks").get(4).get("version").asText()).isEqualTo("6.0.1");
		assertThat(metadata.get("buildpacks").get(5).get("key").asText()).isEqualTo("paketo-buildpacks/apache-tomcat");
		assertThat(metadata.get("buildpacks").get(5).get("version").asText()).isEqualTo("7.0.2");
		assertThat(metadata.get("buildpacks").get(6).get("key").asText()).isEqualTo("paketo-buildpacks/dist-zip");
		assertThat(metadata.get("buildpacks").get(6).get("version").asText()).isEqualTo("5.0.1");
		assertThat(metadata.get("buildpacks").get(7).get("key").asText()).isEqualTo("paketo-buildpacks/spring-boot");
		assertThat(metadata.get("buildpacks").get(7).get("version").asText()).isEqualTo("5.2.0");
	}
}