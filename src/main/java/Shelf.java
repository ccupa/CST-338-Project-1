import Utilities.Code;

import java.util.HashMap;
import java.util.Objects;

public class Shelf {
    public static final int SHELF_NUMBER_ = 0;
    public static final int SUBJECT_ = 1;

    private int shelfNumber;
    private String subject;
    private HashMap<Book, Integer> books = new HashMap<>();

    public Shelf() {
    }

    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.subject = subject;
    }

    public int getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public HashMap<Book, Integer> getBooks() {
        return books;
    }

    public void setBooks(HashMap<Book, Integer> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelf shelf = (Shelf) o;
        return shelfNumber == shelf.shelfNumber && Objects.equals(subject, shelf.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelfNumber, subject);
    }

    public String toString() {
        return shelfNumber + ":" + subject;
    }

    public int getBookCount(Book book){
        if(books.containsKey(book)) {
            return books.get(book);
        } else {
            return -1;
        }
    }

    public Code addBook(Book book) {
        if (books.containsKey(book)) {
            int count = books.get(book);
            books.put(book, count + 1);
            System.out.println(book.toString() + " added to shelf " + this.toString());
            return Code.SUCCESS;
        } else {
            if (book.getSubject().equals(this.subject)) {
                books.put(book, 1);
                System.out.println(book.toString()+ " added to shelf " + this.toString());
                return Code.SUCCESS;
            } else {
                return Code.SHELF_SUBJECT_MISMATCH_ERROR;
            }
        }
    }

        public Code removeBook (Book book){
            if (!books.containsKey(book)) {
                System.out.println(book.getTitle() + " is not on shelf " + this.subject);
                return Code.BOOK_NOT_IN_INVENTORY_ERROR;
            } else if (books.containsKey(book) && books.get(book) == 0) {
                System.out.println("No copies of " + book.getTitle() + " remain on the shelf " + this.subject);
                return Code.BOOK_NOT_IN_INVENTORY_ERROR;
            } else if (books.containsKey(book) && books.get(book) > 0) {
                books.put(book, books.get(book) - 1);
                System.out.println(book.getTitle() + " successfully removed from shelf " + this.subject);
                return Code.SUCCESS;
            } else {
                return Code.BOOK_NOT_IN_INVENTORY_ERROR;
            }
        }

        public String listBooks () {
            int totalBooks = 0;
            for (int count : books.values()) {
                totalBooks += count;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(totalBooks).append(" books on shelf: ").append(shelfNumber).
                    append(" : ").append(subject).append("\n");

            for (Book book : books.keySet()) {
                int count = books.get(book);
                sb.append(book.getTitle()).append(" by ").append(book.getAuthor())
                        .append(" ISBN:").append(book.getIsbn()).append(" ")
                        .append(count).append("\n");
            }
            return sb.toString();
        }
    }
