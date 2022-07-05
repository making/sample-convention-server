package com.example.convention.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @param image a string reference to the image name and tag or associated digest.
 * @param boms an array of Bills of Materials (BOMs) describing the software components and their dependencies and may be zero or more per image.
 * @param config OCI image metadata
 */
public record ImageConfig(String image, List<BOM> boms, JsonNode config) {
}
