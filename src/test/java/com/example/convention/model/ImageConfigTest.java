package com.example.convention.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ImageConfigTest {
	@Autowired
	JacksonTester<ImageConfig> jacksonTester;

	@Test
	void config() throws Exception {
		final byte[] contents = StreamUtils.copyToByteArray(new ClassPathResource("image-config.json").getInputStream());
		final ObjectContent<ImageConfig> parsed = jacksonTester.parse(contents);
		final ImageConfig imageConfig = parsed.getObject();
		final JsonNode config = imageConfig.config();
		final JsonNode labels = config.get("config").get("Labels");
		assertThat(labels).hasSize(16);
		assertThat(labels.has("org.opencontainers.image.title")).isTrue();
		assertThat(labels.get("org.opencontainers.image.title").asText()).isEqualTo("petclinic");
	}
}