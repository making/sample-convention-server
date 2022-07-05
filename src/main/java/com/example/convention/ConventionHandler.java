package com.example.convention;

import java.util.List;

import com.example.convention.model.PodConventionContext;
import com.example.convention.model.PodConventionContextStatus;

import org.springframework.stereotype.Component;

@Component
public class ConventionHandler {
	public PodConventionContextStatus handle(PodConventionContext ctx) {
		return new PodConventionContextStatus(ctx.spec().template(), List.of("dumper"));
	}
}
