package io.sadeq.outbox.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    private Long id;
    private String name;
    private String isbn;

    public String toJson() {
        return String.format("""
                {"id":%d,"name":%s,"isbn":%s}
                """, id, stringify(name), stringify(isbn));
    }

    private String stringify(String str) {
        return (str == null) ? null : String.format("\"%s\"", str);
    }
}
