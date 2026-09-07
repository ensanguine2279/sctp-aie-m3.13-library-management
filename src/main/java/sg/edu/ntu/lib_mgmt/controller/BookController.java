package sg.edu.ntu.lib_mgmt.controller;

import sg.edu.ntu.lib_mgmt.model.Book;
import sg.edu.ntu.lib_mgmt.exception.BookNotFoundException;
import sg.edu.ntu.lib_mgmt.model.BookStatistics;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/books")
public class BookController {
    private Logger logger = Logger.getLogger(BookController.class.getName());

    private ArrayList<Book> books = new ArrayList<>();

    public BookController() {
        books.add(new Book("Clean Code", "Robert C. Martin", "978-0132350884", 2008));
        books.add(new Book("Effective Java", "Joshua Bloch", "978-0134685991", 2018));
        books.add(new Book("Design Patterns",
                "Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides", "978-0201633610", 1994));
        books.add(new Book("Head First Java", "Kathy Sierra and Bert Bates", "978-0596009205", 2005));
        books.add(new Book("Refactoring", "Martin Fowler", "978-0134757599", 2018));
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Book>> getBooks() {
        return ResponseEntity.ok(books);
    }

    @GetMapping("/statistics")
    public ResponseEntity<BookStatistics> getBookStatistics() {
        int totalBooks = books.size();

        if (totalBooks == 0) {
            return ResponseEntity.noContent().build();
        }

        String oldestBook = books.stream()
                .min((b1, b2) -> Integer.compare(b1.getPublicationYear(), b2.getPublicationYear()))
                .map(Book::getTitle).orElse(null);
        String newestBook = books.stream()
                .max((b1, b2) -> Integer.compare(b1.getPublicationYear(), b2.getPublicationYear()))
                .map(Book::getTitle).orElse(null);
        BookStatistics statistics = new BookStatistics(totalBooks, oldestBook, newestBook);
        return ResponseEntity.ok(statistics);
    }

    private int getBookIndex(String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id)).findFirst()
                .map(book -> books.indexOf(book))
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable String id) {
        try {
            int index = getBookIndex(id);
            return ResponseEntity.ok(books.get(index));
        } catch (BookNotFoundException e) {
            logger.warning("Book not found: " + e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<Object> createBook(@RequestBody Book book) {
        this.books.add(book);
        logger.info("Book created: " + book.getTitle());
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable String id, @RequestBody Book book) {
        try {
            int index = getBookIndex(id);
            books.set(index, book);
            logger.info("Book updated: " + book.getTitle());
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (BookNotFoundException ex) {
            logger.warning("Book not found: " + ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable String id) {
        try {
            int index = getBookIndex(id);
            Book removedBook = books.remove(index);

            // Spring will strip the body of a 204 No Content response even if we include
            // it.
            logger.info("Book deleted: " + removedBook.getTitle());
            return new ResponseEntity<>(removedBook, HttpStatus.NO_CONTENT);
        } catch (BookNotFoundException ex) {
            logger.warning("Book not found: " + ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
