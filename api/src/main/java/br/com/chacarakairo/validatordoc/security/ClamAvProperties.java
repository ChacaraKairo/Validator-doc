package br.com.chacarakairo.validatordoc.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.clamav")
public record ClamAvProperties(
    String host,
    int port,
    int timeoutMillis,
    boolean enabled
) {
}
