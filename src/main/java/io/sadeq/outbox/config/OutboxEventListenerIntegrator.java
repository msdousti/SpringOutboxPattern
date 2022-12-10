package io.sadeq.outbox.config;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hibernate.event.spi.EventType.*;

@Component
public class OutboxEventListenerIntegrator implements Integrator {

    private final OutboxEventListener listener;

    @Autowired
    public OutboxEventListenerIntegrator(OutboxEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void integrate(
            Metadata metadata,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(POST_INSERT, listener);
        eventListenerRegistry.appendListeners(PRE_UPDATE, listener);
        eventListenerRegistry.appendListeners(PRE_DELETE, listener);
        eventListenerRegistry.appendListeners(POST_COLLECTION_UPDATE, listener);
    }

    @Override
    public void disintegrate(
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
    }
}
