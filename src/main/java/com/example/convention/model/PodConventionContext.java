package com.example.convention.model;

import io.kubernetes.client.openapi.models.V1ObjectMeta;

/**
 * A wrapper for the PodConventionContextSpec and the PodConventionContextStatus which is the structure used for both requests
 * and responses from the convention server.
 *
 * @param apiVersion
 * @param kind
 * @param metadata
 * @param spec
 * @param status
 */
public record PodConventionContext(String apiVersion, String kind, V1ObjectMeta metadata,
								   PodConventionContextSpec spec,
								   PodConventionContextStatus status) {
}
