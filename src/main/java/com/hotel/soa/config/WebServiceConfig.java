package com.hotel.soa.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "customers")
    public DefaultWsdl11Definition customersWsdl11Definition(XsdSchema customerSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CustomerPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://hotel.com/soap/customer");
        wsdl11Definition.setSchema(customerSchema);
        return wsdl11Definition;
    }

    @Bean(name = "payments")
    public DefaultWsdl11Definition paymentsWsdl11Definition(XsdSchema paymentSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PaymentPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://hotel.com/soap/payment");
        wsdl11Definition.setSchema(paymentSchema);
        return wsdl11Definition;
    }

    @Bean(name = "customerSchema")
    public XsdSchema customerSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/customer.xsd"));
    }

    @Bean(name = "paymentSchema")
    public XsdSchema paymentSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/payment.xsd"));
    }
}
