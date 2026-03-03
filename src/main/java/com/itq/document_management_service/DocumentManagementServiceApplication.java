package com.itq.document_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DocumentManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentManagementServiceApplication.class, args);
	}

}
