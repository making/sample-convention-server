package com.example.convention;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
		final Set<String> appliedConventions = new HashSet<>();
		if (podTemplate.getSpec() != null) {
			final List<V1Container> containers = podTemplate.getSpec().getContainers();
			for (V1Container container : containers) {
				final List<V1EnvVar> env = container.getEnv() == null ? new ArrayList<>() : container.getEnv();
				if (env.stream().noneMatch(envVar -> Objects.equals(envVar.getName(), TARGET_ENV_NAME))) {
					appliedConventions.add("add-env-var");
					env.add(new V1EnvVar().name(TARGET_ENV_NAME).value("HELLO FROM CONVENTION"));
				}
				container.setEnv(env);
			}
		}
		return new PodConventionContextStatus(podTemplate, appliedConventions.stream().toList());
	}
}
