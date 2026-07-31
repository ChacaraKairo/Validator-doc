package br.com.chacarakairo.validatordoc.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClamAvProperties.class)
public class ClamAvConfiguration {
}
