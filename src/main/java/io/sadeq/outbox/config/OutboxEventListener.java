package io.sadeq.outbox.config;

import io.sadeq.outbox.entities.Book;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serial;

@Slf4j
public class OutboxEventListener implements
        PostInsertEventListener, PostUpdateEventListener, PreDeleteEventListener {

    @Serial
    private static final long serialVersionUID = 2180674581693436007L;

    public static final OutboxEventListener INSTANCE = new OutboxEventListener();

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        log.info("===========> Executing onPostInsert().........");
        dispatchToOutboxTable(event.getSession(), event.getEntity(), "C");
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        log.info("===========> Executing onPostUpdate().........");
        dispatchToOutboxTable(event.getSession(), event.getEntity(), "U");
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        log.info("===========> Executing onPreDelete().........");
        dispatchToOutboxTable(event.getSession(), event.getEntity(), "D");
        return false;
    }

    private void dispatchToOutboxTable(EventSource session, Object entity, String dataOp) {
        if (entity instanceof Book book) {
            insertRecordIntoOutbox(session, book, dataOp);
        }
    }

    @SneakyThrows
    private void insertRecordIntoOutbox(EventSource session, Book book, String dataOp) {
        session.createNativeQuery(
                        "INSERT INTO book_outbox (data, data_op) VALUES (cast(:data as jsonb), :data_op)"
                )
                .setParameter("data", book.toJson())
                .setParameter("data_op", dataOp)
                // See https://vladmihalcea.com/hibernate-event-listeners
                // If not set (i.e., default AUTO is used),
                // a "duplicate key value violates unique constraint" exception is thrown.
                .setHibernateFlushMode(FlushMode.MANUAL)
                .executeUpdate();
    }
}
