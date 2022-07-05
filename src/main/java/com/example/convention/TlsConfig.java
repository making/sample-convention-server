package com.example.convention;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.coyote.http11.AbstractHttp11JsseProtocol;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class TlsConfig {

	@ConstructorBinding
	@ConfigurationProperties(prefix = "tls")
	public record TlsProps(boolean enabled, Resource certificate,
						   Resource certificatePrivateKey) {}

	@Bean
	public TomcatConnectorCustomizer customizer(TlsProps props) {
		return connector -> {
			if (props.enabled()) {
				AbstractHttp11JsseProtocol<?> protocol = (AbstractHttp11JsseProtocol) connector.getProtocolHandler();
				protocol.setSSLEnabled(true);
				try {
					protocol.setSSLCertificateFile(props.certificate().getURL().getPath());
					protocol.setSSLCertificateKeyFile(props.certificatePrivateKey().getURL().getPath());
				}
				catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		};
	}
}
