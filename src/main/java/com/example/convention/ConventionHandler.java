package com.example.convention;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.convention.model.ImageConfig;
import com.example.convention.model.PodConventionContext;
import com.example.convention.model.PodConventionContextStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

import org.springframework.stereotype.Component;

@Component
public class ConventionHandler {
	private final ObjectMapper objectMapper;

	public ConventionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public PodConventionContextStatus handle(PodConventionContext ctx) {
		final V1PodTemplateSpec podTemplate = ctx.spec().template();
		final Set<String> appliedConventions = new LinkedHashSet<>();
		final V1ObjectMeta metadata = podTemplate.getMetadata();
		if (metadata != null) {
			final Map<String, String> annotations = metadata.getAnnotations() == null ? new HashMap<>() : metadata.getAnnotations();
			metadata.setAnnotations(annotations);
			for (ImageConfig imageConfig : ctx.spec().imageConfig()) {
				final JsonNode config = imageConfig.config();
				if (config == null || !config.has("config") || !config.get("config").has("Labels")) {
					continue;
				}
				final JsonNode labels = config.get("config").get("Labels");
				if (labels.has("io.buildpacks.lifecycle.metadata")) {
					applyBuildpacksInfo(imageConfig, appliedConventions, annotations);
				}
				if (labels.has("org.opencontainers.image.source")) {
					applySourceInfo(imageConfig, appliedConventions, annotations);
				}
			}
		}
		return new PodConventionContextStatus(podTemplate, appliedConventions.stream().toList());
	}

	private void applySourceInfo(ImageConfig imageConfig, Set<String> appliedConventions, Map<String, String> annotations) {
		final String source = imageConfig.config().get("config").get("Labels").get("org.opencontainers.image.source").asText();
		annotations.put("inspect-image.opencontainers.org/source", source);
		appliedConventions.add("source");
	}

	private void applyBuildpacksInfo(ImageConfig imageConfig, Set<String> appliedConventions, Map<String, String> annotations) {
		try {
			final JsonNode lifecycleMetadata = this.objectMapper.readValue(imageConfig.config().get("config").get("Labels").get("io.buildpacks.lifecycle.metadata").asText(), JsonNode.class);
			if (lifecycleMetadata.has("buildpacks")) {
				final String buildpacks = StreamSupport.stream(lifecycleMetadata.get("buildpacks").spliterator(), false)
						.map(n -> """
								- id: %s
								  version: %s
								"""
								.trim()
								.formatted(n.get("key").asText(), n.get("version").asText()))
						.collect(Collectors.joining(System.lineSeparator()));
				annotations.put("inspect-image.buildpacks.io/buildpacks", buildpacks);
				appliedConventions.add("buildpacks");
			}
			if (lifecycleMetadata.has("runImage")) {
				final JsonNode runImage = lifecycleMetadata.get("runImage");
				String baseImage = """
						reference: %s
						top_layer: %s
						"""
						.trim()
						.formatted(runImage.get("reference").asText(), runImage.get("topLayer").asText());
				annotations.put("inspect-image.buildpacks.io/base-image", baseImage);
				appliedConventions.add("base-image");
			}
			if (lifecycleMetadata.has("stack")) {
				final JsonNode stack = lifecycleMetadata.get("stack");
				final String runImage = stack.get("runImage").get("image").asText();
				annotations.put("inspect-image.buildpacks.io/run-image", runImage);
				appliedConventions.add("run-image");
			}
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}
}
