package com.example.convention.model;

/**
 * @param name bom-name
 * @param raw base64 encoded bytes with the encoded content of the BOM.
 */
public record BOM(String name, String raw) {
}
