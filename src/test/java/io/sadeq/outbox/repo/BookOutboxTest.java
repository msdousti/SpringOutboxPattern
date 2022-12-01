package io.sadeq.outbox.repo;

import io.sadeq.outbox.config.HibernateConfig;
import io.sadeq.outbox.config.OutboxEventListener;
import io.sadeq.outbox.config.OutboxEventListenerIntegrator;
import io.sadeq.outbox.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({OutboxEventListener.class, OutboxEventListenerIntegrator.class, HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class BookOutboxTest {

    private final BookRepo bookRepo;
    private final EntityManager em;

    private final Book bookForInsert = new Book().id(1L).name("Book for insert");
    private final Book bookForUpdate = new Book().id(2L).name("Book for update");
    private final Book bookForDelete = new Book().id(3L).name("Book for delete");

    @Autowired
    BookOutboxTest(BookRepo bookRepo, EntityManager em) {
        this.bookRepo = bookRepo;
        this.em = em;
    }

    @BeforeEach
    void beforeEach() {
        em.createNativeQuery("TRUNCATE table book").executeUpdate();
        em.persist(bookForUpdate);
        em.persist(bookForDelete);
        em.createNativeQuery("TRUNCATE table book_outbox").executeUpdate();
    }

    @Test
    void testAllOperations() {
        bookRepo.save(bookForInsert);
        assertThat(getCountInOutbox("C")).isEqualTo(1L);

        // we only monitor updates to "isbn"
        bookRepo.save(bookForUpdate.name("Book for update!"));
        assertThat(getCountInOutbox("U")).isEqualTo(0L);

        bookRepo.save(bookForUpdate.isbn("123456789"));
        assertThat(getCountInOutbox("U")).isEqualTo(1L);

        bookRepo.delete(bookForDelete);
        assertThat(getCountInOutbox("D")).isEqualTo(1L);
    }

    private long getCountInOutbox(String dataOp) {
        return ((BigInteger) em.createNativeQuery(
                        "SELECT count(*) from book_outbox where data_op=:data_op"
                )
                .setParameter("data_op", dataOp)
                .getSingleResult()).longValue();
    }
}