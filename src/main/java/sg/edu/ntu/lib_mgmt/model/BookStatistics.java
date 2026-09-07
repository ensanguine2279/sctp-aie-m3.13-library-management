package sg.edu.ntu.lib_mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookStatistics {
    private int totalBooks;
    private String oldestBook;
    private String newestBook;
}