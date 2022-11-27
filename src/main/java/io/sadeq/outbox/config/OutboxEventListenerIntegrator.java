package io.sadeq.outbox.config;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import static org.hibernate.event.spi.EventType.*;

public class OutboxEventListenerIntegrator implements Integrator {

    public static final OutboxEventListenerIntegrator INSTANCE =
            new OutboxEventListenerIntegrator();

    @Override
    public void integrate(
            Metadata metadata,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(POST_INSERT, OutboxEventListener.INSTANCE);
        eventListenerRegistry.appendListeners(POST_UPDATE, OutboxEventListener.INSTANCE);
        eventListenerRegistry.appendListeners(PRE_DELETE, OutboxEventListener.INSTANCE);
    }

    @Override
    public void disintegrate(
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
    }
}
