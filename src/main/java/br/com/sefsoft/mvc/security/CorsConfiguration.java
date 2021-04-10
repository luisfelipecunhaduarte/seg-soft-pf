package br.com.sefsoft.mvc.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfiguration {
	@Value("#{'${allowed.origins}'.split(',')}")
	private List<String> rawOrigins;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(getOrigin())
						.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH",
								"OPTIONS", "HEAD", "TRACE", "CONNECT")
						.allowCredentials(true);
			}
		};
	}

	public String[] getOrigin() {
		int size = rawOrigins.size();
		String[] originArray = new String[size];
		return rawOrigins.toArray(originArray);
	}
}