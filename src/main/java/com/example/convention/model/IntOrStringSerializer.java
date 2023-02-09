package com.example.convention.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.kubernetes.client.custom.IntOrString;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class IntOrStringSerializer extends JsonSerializer<IntOrString> {

	@Override
	public void serialize(IntOrString intOrString, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		if (intOrString.isInteger()) {
			jsonGenerator.writeNumber(intOrString.getIntValue());
		}
		else {
			jsonGenerator.writeString(intOrString.getStrValue());
		}
	}
}