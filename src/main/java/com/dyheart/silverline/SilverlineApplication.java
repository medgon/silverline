package com.dyheart.silverline;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.apache.catalina.connector.Connector;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class SilverlineApplication {
	
	static Logger log = Logger.getRootLogger();
	
	@Value("${tomcat.ajp.port}")
	int ajpPort;

	@Value("${tomcat.ajp.tomcat-authentication}")
	String tomcatAuthentication;

	@Value("${tomcat.ajp.enabled}")
	boolean tomcatAjpEnabled;
	
	public static void main(String[] args) {
		ApplicationContext appCtx = SpringApplication.run(SilverlineApplication.class, args);
		String[] beanNames = appCtx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        log.debug(beanNames);
	}
	
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {

	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	    
	    if (tomcatAjpEnabled)
	    {
	        Connector ajpConnector = new Connector("AJP/1.3");
	        ajpConnector.setProtocol("AJP/1.3");
	        ajpConnector.setPort(ajpPort);
	        ajpConnector.setSecure(false);
	        ajpConnector.setAllowTrace(false);
	        ajpConnector.setScheme("http");
	        ajpConnector.setAttribute("tomcatAuthentication", tomcatAuthentication);

	        tomcat.addAdditionalTomcatConnectors(ajpConnector);
	        
	        log.info("AJP Connector enabled: "+ajpConnector);
	        
	    }

	    return tomcat;
	}
	
	@Bean
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	          executor.setCorePoolSize(7);
	          executor.setMaxPoolSize(42);
	          executor.setQueueCapacity(11);
	          executor.setThreadNamePrefix("Silverline-Executor-");
	    executor.initialize();
	    return executor;
	}
}
