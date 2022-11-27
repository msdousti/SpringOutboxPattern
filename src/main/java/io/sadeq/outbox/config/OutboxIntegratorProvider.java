package io.sadeq.outbox.config;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;

import java.util.List;

@SuppressWarnings("unused")
public class OutboxIntegratorProvider implements IntegratorProvider {

    @Override
    public List<Integrator> getIntegrators() {
        return List.of(OutboxEventListenerIntegrator.INSTANCE);
    }
}
