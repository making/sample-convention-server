package com.example.convention;

import java.nio.charset.StandardCharsets;

import am.ik.certificate.CertificateImporter;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SampleConventionServerApplicationTests {
	@LocalServerPort
	int port;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Test
	void contextLoads() throws Exception {
		final String caCert = StreamUtils.copyToString(new ClassPathResource("certs/ca.crt").getInputStream(), StandardCharsets.UTF_8);
		new CertificateImporter().doImport(caCert);
		final RestTemplate restTemplate = restTemplateBuilder.build();
		final ResponseEntity<String> response = restTemplate.getForEntity("https://127-0-0-1.sslip.io:%d/actuator/info".formatted(port), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
