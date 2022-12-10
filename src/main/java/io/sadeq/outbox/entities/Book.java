package io.sadeq.outbox.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    private Long id;
    private String name;
    private String isbn;
    private LocalDateTime created = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime lastModified = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn
    @JsonManagedReference
    protected List<Author> authors = new ArrayList<>();

    public Book addAuthor(Author author) {
        author.book = this;
        authors.add(author);
        return this;
    }

    @SuppressWarnings("unused")
    public Book removeAuthor(Author author) {
        author.book = null;
        authors.remove(author);
        return this;
    }
}
