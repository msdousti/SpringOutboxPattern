package io.sadeq.outbox.repo;

import io.sadeq.outbox.config.HibernateConfig;
import io.sadeq.outbox.config.OutboxEventListener;
import io.sadeq.outbox.config.OutboxEventListenerIntegrator;
import io.sadeq.outbox.entities.Author;
import io.sadeq.outbox.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({OutboxEventListener.class, OutboxEventListenerIntegrator.class, HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
@AutoConfigureJson
@Rollback(false)
class BookOutboxTest {

    private final BookRepo bookRepo;
    private final EntityManager em;

    private Book bookForInsert = new Book().name("Book for insert");
    private Book bookForUpdate = new Book().name("Book for update");
    private Book bookForDelete = new Book().name("Book for delete");

    @Autowired
    BookOutboxTest(BookRepo bookRepo, EntityManager em) {
        this.bookRepo = bookRepo;
        this.em = em;
    }

    @BeforeEach
    void beforeEach() {
        em.createNativeQuery("TRUNCATE table book CASCADE").executeUpdate();
        bookForUpdate = bookRepo.save(bookForUpdate);
        bookForDelete = bookRepo.save(bookForDelete);
        em.createNativeQuery("TRUNCATE table book_outbox").executeUpdate();
    }

    @Test
    void testAllOperations() {
        bookForInsert = bookRepo.save(bookForInsert);
        assertThat(getCountInOutbox("C")).isEqualTo(1L);

        bookRepo.save(bookForUpdate.isbn("123456789"));
        assertThat(getCountInOutbox("U")).isEqualTo(1L);

        // we only monitor updates to "isbn"
        bookRepo.save(bookForUpdate.name("Book for update!"));
        assertThat(getCountInOutbox("U")).isEqualTo(1L);

        Author johnDoe = new Author().name("John Doe");
        bookRepo.save(bookForUpdate.addAuthor(johnDoe));
        assertThat(getCountInOutbox("U")).isEqualTo(2L);

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