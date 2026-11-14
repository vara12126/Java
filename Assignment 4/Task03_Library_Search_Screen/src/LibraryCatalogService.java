package task03_library;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing library books catalog, searching, pagination, and reservations.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class LibraryCatalogService {

    private final List<BookRecord> masterCatalog = new ArrayList<>();

    public LibraryCatalogService() {
        seedBooks();
    }

    private void seedBooks() {
        masterCatalog.add(new BookRecord("CS-101", "Core Java Volume I – Fundamentals", "Cay S. Horstmann", "Computer Science", 2024, true));
        masterCatalog.add(new BookRecord("CS-102", "Effective Java (3rd Edition)", "Joshua Bloch", "Computer Science", 2018, true));
        masterCatalog.add(new BookRecord("CS-103", "Java: The Complete Reference (12th Ed)", "Herbert Schildt", "Computer Science", 2022, false));
        masterCatalog.add(new BookRecord("CS-104", "Design Patterns: Elements of Reusable OO", "Erich Gamma et al.", "Computer Science", 1994, true));
        masterCatalog.add(new BookRecord("CS-105", "Operating System Concepts (10th Ed)", "Abraham Silberschatz", "Computer Science", 2021, true));
        masterCatalog.add(new BookRecord("CS-106", "Introduction to Algorithms (4th Ed)", "Thomas H. Cormen", "Computer Science", 2022, true));
        masterCatalog.add(new BookRecord("EC-201", "Digital Signal Processing", "John G. Proakis", "Electronics", 2014, true));
        masterCatalog.add(new BookRecord("EC-202", "Microelectronic Circuits (8th Ed)", "Adel S. Sedra", "Electronics", 2020, true));
        masterCatalog.add(new BookRecord("ME-301", "Fluid Mechanics Fundamentals", "Yunus A. Cengel", "Mechanical", 2019, true));
        masterCatalog.add(new BookRecord("ME-302", "Engineering Thermodynamics", "P. K. Nag", "Mechanical", 2018, false));
        masterCatalog.add(new BookRecord("MA-401", "Advanced Engineering Mathematics", "Erwin Kreyszig", "Mathematics", 2020, true));
        masterCatalog.add(new BookRecord("MA-402", "Linear Algebra and Its Applications", "Gilbert Strang", "Mathematics", 2016, true));
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.add("Computer Science");
        categories.add("Electronics");
        categories.add("Mechanical");
        categories.add("Mathematics");
        return categories;
    }

    public List<BookRecord> searchBooks(String query, String category) {
        return masterCatalog.stream().filter(b -> {
            boolean matchCat = category.equals("All Categories") || b.getCategory().equalsIgnoreCase(category);
            boolean matchQuery = query == null || query.trim().isEmpty() ||
                    b.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    b.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    b.getAccessionNumber().toLowerCase().contains(query.toLowerCase());
            return matchCat && matchQuery;
        }).collect(Collectors.toList());
    }

    public boolean reserveBook(String accessionNo) {
        for (BookRecord b : masterCatalog) {
            if (b.getAccessionNumber().equalsIgnoreCase(accessionNo)) {
                if (b.isAvailable()) {
                    b.setAvailable(false);
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
