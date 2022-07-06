package com.example.convention.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.kubernetes.client.custom.Quantity;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class QuantitySerializer extends JsonSerializer<Quantity> {

	@Override
	public void serialize(Quantity quantity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(quantity.toSuffixedString());
	}
}