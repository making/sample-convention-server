package com.example.convention;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.convention.model.PodConventionContext;
import com.example.convention.model.PodConventionContextStatus;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

import org.springframework.stereotype.Component;

@Component
public class ConventionHandler {
	private static final String TARGET_ENV_NAME = "CONVENTION_SERVER";

	public PodConventionContextStatus handle(PodConventionContext ctx) {
		final V1PodTemplateSpec podTemplate = ctx.spec().template();
		if (podTemplate.getSpec() != null) {
			final List<V1Container> containers = podTemplate.getSpec().getContainers();
			boolean applied = false;
			for (V1Container container : containers) {
				final List<V1EnvVar> env = container.getEnv() == null ? new ArrayList<>() : container.getEnv();
				if (env.stream().noneMatch(envVar -> Objects.equals(envVar.getName(), TARGET_ENV_NAME))) {
					applied = true;
					env.add(new V1EnvVar().name(TARGET_ENV_NAME).value("HELLO FROM CONVENTION"));
				}
				container.setEnv(env);
			}
			if (applied) {
				return new PodConventionContextStatus(podTemplate, List.of("add-env-var"));
			}
		}
		return new PodConventionContextStatus(podTemplate, List.of());
	}
}
