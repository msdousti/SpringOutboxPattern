package io.sadeq.outbox.config;

import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {

    final OutboxEventListenerIntegrator integrator;

    @Autowired
    public HibernateConfig(OutboxEventListenerIntegrator integrator) {
        this.integrator = integrator;
    }

    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> List.of(integrator));
    }
}
