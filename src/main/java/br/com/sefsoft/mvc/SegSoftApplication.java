package br.com.sefsoft.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
		"br.com.almodeschool.mvcalmode"
})
public class SegSoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(SegSoftApplication.class, args);
	}

}
