package io.sadeq.outbox.config;

import io.sadeq.outbox.entities.Book;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OutboxEventListener implements
        PostInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    @Serial
    private static final long serialVersionUID = 2180674581693436007L;

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        dispatchToOutboxTable(event.getSession(), event.getEntity(), "C");
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        var names2indices = array2map(event.getPersister().getPropertyNames());
        var indexOfIsbn = names2indices.get(Book.Fields.isbn);
        if (event.getOldState()[indexOfIsbn] != event.getState()[indexOfIsbn])
            dispatchToOutboxTable(event.getSession(), event.getEntity(), "U");
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
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

    private static Map<String, Integer> array2map(String[] array) {
        var m = new HashMap<String, Integer>();
        for (int i = 0; i < array.length; i++)
            m.put(array[i], i);
        return m;
    }
}
