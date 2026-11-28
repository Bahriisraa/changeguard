package com.changeguard.approval;

import java.time.Clock;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE = "http://changeguard.example.com/approval/v1";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext ctx) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(ctx);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean
    public XsdSchema approvalSchema() {
        return new SimpleXsdSchema(new ClassPathResource("approval.xsd"));
    }

    // le nom du bean ("approval") devient le nom du WSDL : /ws/approval.wsdl
    @Bean(name = "approval")
    public DefaultWsdl11Definition approvalWsdl(XsdSchema approvalSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ApprovalPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace(NAMESPACE);
        wsdl.setSchema(approvalSchema);
        return wsdl;
    }
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}