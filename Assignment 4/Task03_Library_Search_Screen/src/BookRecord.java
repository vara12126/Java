package task03_library;

/**
 * Entity representing a library catalog book.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class BookRecord {

    private final String accessionNumber;
    private final String title;
    private final String author;
    private final String category;
    private final int year;
    private boolean isAvailable;

    public BookRecord(String accessionNumber, String title, String author, String category, int year, boolean isAvailable) {
        this.accessionNumber = accessionNumber;
        this.title = title;
        this.author = author;
        this.category = category;
        this.year = year;
        this.isAvailable = isAvailable;
    }

    public String getAccessionNumber() { return accessionNumber; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getYear() { return year; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
