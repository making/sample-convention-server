package com.example.convention.model;

import java.util.List;

import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

/**
 * a wrapper of the PodTemplateSpec and list of ImageConfigs provided in the request body of the server.
 *
 * @param template a wrapper of the PodTemplateSpec and list of ImageConfigs provided in the request body of the server.
 * @param imageConfig an array of imageConfig objects with each image configuration object holding the name of the image, the BOM, and the OCI image
 * configuration with image metadata from the repository. Each of the image config array entries have a 1:1 mapping to
 * images referenced in the PodTemplateSpec.
 */
public record PodConventionContextSpec(V1PodTemplateSpec template,
									   List<ImageConfig> imageConfig) {
}
