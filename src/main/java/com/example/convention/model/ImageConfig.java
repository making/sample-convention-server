package com.example.convention.model;

import java.util.List;
import java.util.Map;

/**
 * @param image a string reference to the image name and tag or associated digest.
 * @param boms an array of Bills of Materials (BOMs) describing the software components and their dependencies and may be zero or more per image.
 * @param config OCI image metadata
 */
public record ImageConfig(String image, List<BOM> boms, Map<String, Object> config) {
}
