package com.example.convention;

import com.example.convention.model.PodConventionContext;
import com.example.convention.model.PodConventionContextStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConventionController {
	private final Logger log = LoggerFactory.getLogger(ConventionController.class);

	private final ConventionHandler conventionHandler;

	public ConventionController(ConventionHandler conventionHandler) {
		this.conventionHandler = conventionHandler;
	}

	@PostMapping(path = "/")
	public PodConventionContext convention(@RequestBody PodConventionContext ctx) {
		log.info("PodConventionContext={}", ctx);
		final PodConventionContextStatus status = this.conventionHandler.handle(ctx);
		log.info("PodConventionContextStatus={}", status);
		return new PodConventionContext(ctx.apiVersion(), ctx.kind(), ctx.metadata(), ctx.spec(), status);
	}
}
