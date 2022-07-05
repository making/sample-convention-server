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
	void convention() throws Exception {
		final byte[] content = StreamUtils.copyToByteArray(new ClassPathResource("request.json").getInputStream());
		this.mockMvc.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").isNotEmpty())
				.andExpect(jsonPath("$.status.template").isNotEmpty())
				.andExpect(jsonPath("$.status.template.spec").isNotEmpty())
				.andExpect(jsonPath("$.status.appliedConventions").isArray())
				.andExpect(jsonPath("$.status.appliedConventions.length()").value(1))
				.andExpect(jsonPath("$.status.appliedConventions[0]").value("dumper"));
	}

}