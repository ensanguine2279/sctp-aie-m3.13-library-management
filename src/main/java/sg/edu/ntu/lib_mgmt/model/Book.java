package sg.edu.ntu.lib_mgmt.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({ "id", "title", "author", "isbn", "publicationYear" })
@Data
@NoArgsConstructor
public class Book {
    private final String id = UUID.randomUUID().toString();
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;

    public Book(String title, String author, String isbn, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }
}
