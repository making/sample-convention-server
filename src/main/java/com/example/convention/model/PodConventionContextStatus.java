package com.example.convention.model;

import java.util.List;

import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

/**
 * status type used to represent the current status of the context retrieved by the request.
 *
 * @param template
 * @param appliedConventions a list of string with names of conventions to be applied
 */
public record PodConventionContextStatus(V1PodTemplateSpec template,
										 List<String> appliedConventions) {
}
