import Utilities.Code;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Library {
    public static final int LENDING_LIMIT = 5;
    private HashMap<Book, Integer> books = new HashMap<>();
    private static int libraryCard;
    private String name;
    private List<Reader> readers = new ArrayList<>();
    private HashMap<String, Shelf> shelves = new HashMap<>();

    public Library(String name) {
        this.name = name;
    }

    public Code init(String filename) {
        Scanner scanner = null;

        try {
            File file = new File(filename);
            scanner = new Scanner(file);

            if (!scanner.hasNextLine()) {
                return Code.BOOK_COUNT_ERROR;
            }

            String line = scanner.nextLine();
            int bookCount = convertInt(line, Code.BOOK_COUNT_ERROR);

            if (bookCount < 0){
                for (Code c : Code.values()) {
                    if (c.getCode() == bookCount) {
                        return c;
                    }
                }
                return Code.UNKNOWN_ERROR;
            }

            Code code = initBooks(bookCount, scanner);
            if (code == Code.SUCCESS) {
                return code;
            }
            listBooks();

            if (!scanner.hasNextLine()) {
                return Code.SHELF_COUNT_ERROR;
            }
            line = scanner.nextLine();
            int shelfCount = convertInt(line, Code.SHELF_COUNT_ERROR);

            if (shelfCount < 0) {
                for (Code c : Code.values()) {
                    if (c.getCode() == shelfCount) {
                        return c;
                    }
                }
                return Code.UNKNOWN_ERROR;
            }
            code = initShelves(shelfCount, scanner);
            if (code != Code.SUCCESS) {
                return code;
            }
            listShelves();

            if (!scanner.hasNextLine()) {
                return Code.READER_COUNT_ERROR;
            }
            line = scanner.nextLine();
            int readerCount = convertInt(line, Code.READER_COUNT_ERROR);

            if (readerCount < 0) {
                for (Code c : Code.values()) {
                    if (c.getCode() == readerCount) {
                        return c;
                    }
                }
                return Code.UNKNOWN_ERROR;
            }

            code = initReader(readerCount, scanner);
            if (code != Code.SUCCESS) {
                return code;
            }
            listReaders();

            return Code.SUCCESS;

        } catch (FileNotFoundException e) {
            return Code.FILE_NOT_FOUND_ERROR;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private Code initBooks(int bookCount, Scanner scan) {
        if (bookCount < 1) {
            return Code.LIBRARY_ERROR;
        }

        for (int i = 0; i < bookCount; i++) {
            if (!scan.hasNextLine()) {
                return Code.BOOK_RECORD_COUNT_ERROR;
            }
            String line = scan.nextLine();
            String[] bits = line.split(",", -1);

            if (bits.length < 6) {
                return Code.BOOK_RECORD_COUNT_ERROR;
            }

            String isbn = bits[Book.ISBN_].trim();
            String title = bits[Book.TITLE].trim();
            String subject = bits[Book.SUBJECT_].trim();

            int pages = convertInt(bits[Book.PAGE_COUNT_].trim(), Code.PAGE_COUNT_ERROR);
            if (pages <= 0) {
                return Code.PAGE_COUNT_ERROR;
            }

            String author = bits[Book.AUTHOR_].trim();

            LocalDate dueDate = convertDate(bits[Book.DUE_DATE].trim(), Code.DATE_CONVERSION_ERROR);
            if (dueDate == null) {
                return Code.DATE_CONVERSION_ERROR;
            }
            Book newBook = new Book(isbn, title, subject, pages, author, dueDate);
            addBook(newBook);
        }
        return Code.SUCCESS;
    }

    public Code addBook(Book newBook) {
        Integer count = books.get(newBook);
        if (count == null) {
            books.put(newBook, 1);
            System.out.println(newBook.getTitle() + " added to the stacks.");
        } else {
            int newCount = count + 1;
            books.put(newBook, newCount);
            System.out.println(newCount + " copies of " + newBook.getTitle() + " in the stacks.");
        }

        Shelf shelf = getShelf(newBook.getSubject());
        if (shelf != null) {
            shelf.addBook(newBook);
            return Code.SUCCESS;
        } else {
            System.out.println("No shelf for " + newBook.getSubject() + " books");
            return Code.SHELF_EXISTS_ERROR;
        }
    }

    public Code returnBook(Reader reader, Book book) {
        if(!reader.getBooks().contains(book)) {
            System.out.println(reader.getName() + " doesn't have " + book.getTitle() + " checked out");
            return Code.READER_DOESNT_HAVE_BOOK_ERROR;
        }

        if(!books.containsKey(book)) {
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        System.out.println(reader.getName() + " is returning " + book.getTitle());

        Code code = reader.removeBook(book);

        if (code == Code.SUCCESS) {
            Code shelfCode = returnBook(book);
        } else {
            System.out.println("Could not return " + book.getTitle());
        }
        return code;
    }

    public Code returnBook(Book book) {
        Shelf shelf = getShelf(book.getSubject());

        if (shelf == null) {
            System.out.println("No shelf for " + book.getTitle());
            return Code.SHELF_EXISTS_ERROR;
        }

        Code code = shelf.addBook(book);
        return code;
    }

    public Shelf getShelf(Integer shelfNumber) {
        for (Shelf shelf : shelves.values()) {
            if (shelf.getShelfNumber() == shelfNumber) {
                return shelf;
            }
        }
        System.out.println("No shelf number " + shelfNumber + " found");
        return null;
    }

    public Shelf getShelf(String subject) {
        if (shelves.containsKey(subject)) {
            return shelves.get(subject);
        } else {
            System.out.println("No shelf for " + subject + " books");
            return null;
        }
    }

    public LocalDate convertDate(String date, Code errorCode) {
        return null;
    }

    public int listReaders() {
        for (Reader reader : readers) {
            System.out.println(reader);
        }
        return readers.size();
    }

    public int listReaders(boolean showBooks) {
        if (showBooks) {
            for (Reader reader : readers) {
                System.out.println(reader.getName() + "(" + reader.getCardNumber() + ") has the following books:");
                System.out.println(reader.getBooks());
            }
        } else {
            for (Reader reader : readers) {
                System.out.println(reader);
            }
        }
        return readers.size();
    }

    private Code initReader(int readerCount, Scanner scan) {
        if (readerCount <= 0) {
            return Code.READER_COUNT_ERROR;
        }

        for (int i = 0; i < readerCount; i++) {
            if (!scan.hasNextLine()) {
                return Code.READER_COUNT_ERROR;
            }

            String line = scan.nextLine();
            String[] parts = line.split(", ", -1);

            if (parts.length < Reader.NAME_) {
                return Code.READER_COUNT_ERROR;
            }

            int cardNumber = convertInt(parts[Reader.CARD_NUMBER_].trim(), Code.READER_CARD_NUMBER_ERROR);
            String name = parts[Reader.NAME_].trim();
            String phone = parts[Reader.PHONE_].trim();

            Reader reader = new Reader(cardNumber, name, phone);
            readers.add(reader);

            int bookCount = convertInt(parts[Reader.BOOK_COUNT_].trim(), Code.BOOK_COUNT_ERROR);

            int start = Reader.BOOK_START_;
            for (int b = 0; b < bookCount; b++) {
                int isbnIndex = start + (b*2);
                int date = isbnIndex + 1;

                if (isbnIndex >= parts.length) {
                    break;
                }

                String isbn = parts[isbnIndex].trim();
                Book book = getBookByISBN(isbn);

                if (book == null) {
                    System.out.println("ERROR");
                    continue;
                }

                String dateString = parts[date].trim();
                LocalDate dueDate = convertDate(dateString, Code.DATE_CONVERSION_ERROR);
                checkOutBook(reader, book);
            }
        }
        return Code.SUCCESS;
    }

    public Code checkOutBook(Reader reader, Book book) {
        if (!readers.contains(reader)) {
            System.out.println(reader.getName() + " doesn't have an account here");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }

        if (reader.getBooks().size() >= LENDING_LIMIT) {
            System.out.println(reader.getName() + " has reached the lending limit, (" + LENDING_LIMIT + ")");
            return Code.BOOK_LIMIT_REACHED_ERROR;
        }

        if (!books.containsKey(book)) {
            System.out.println("ERROR: could not find " + book.getTitle());
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        Shelf shelf = getShelf(book.getSubject());
        if (shelf == null) {
            System.out.println("no shelf for " + book.getSubject() + " books!");
            return Code.SHELF_EXISTS_ERROR;
        }

        int copies = books.get(book);
        if (copies < 1) {
            System.out.println("ERROR: no copes of " + book.getTitle() + " remain");
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        Code code = reader.addBook(book);
        if (code != Code.SUCCESS) {
            System.out.println("Couldn't checkout " + book.getTitle());
            return code;
        }
        Code removeCode = shelf.removeBook(book);
        if (removeCode == Code.SUCCESS) {
            System.out.println(book.getTitle() + " checked out successfully");
        }
        return removeCode;
    }

    public Book getBookByISBN(String isbn) {
        for (Book book : books.keySet()) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        System.out.println("ERROR: Could not find a book with isbn: " + isbn);
        return null;
    }

    public int listShelves() {
        return listShelves(false);
    }

    public int listShelves(boolean showBooks) {
        for (Shelf shelf : shelves.values()) {
            if (showBooks) {
                shelf.listBooks();
            } else {
                System.out.println(shelf);
            }
        }
        return shelves.size();
    }



    private Code initShelves(int shelfCount, Scanner scan) {
        if (shelfCount < 1) {
            return Code.SHELF_COUNT_ERROR;
        }

        int added = 0;

        for (int i = 0; i < shelfCount; i++) {
            if (!scan.hasNextLine()) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }

            String line = scan.nextLine();
            if (line.isEmpty()) {
                i--;
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 2) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }

            int shelfNumber = convertInt(parts[0].trim(), Code.SHELF_NUMBER_PARSE_ERROR);
            if (shelfNumber < 1) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }

            String shelfSubject = parts[1].trim();

            Code code = addShelf(shelfSubject);
            if (code == Code.SUCCESS) {
                added++;
            } else if (code != Code.SHELF_EXISTS_ERROR);
            return code;
        }

        if (added == shelfCount || shelves.size() >= shelfCount) {
            return Code.SUCCESS;
        } else {
            System.out.println("Number of shelves doesn't match expected");
            return Code.SHELF_NUMBER_PARSE_ERROR;
        }
    }


    public Code addShelf(String shelfSubject) {
        int shelfNumber = shelves.size() + 1;
        Shelf shelf = new Shelf(shelfNumber, shelfSubject);

        return addShelf(shelf);
    }

    public Code addShelf(Shelf shelf) {
        if (!shelves.containsKey(shelf.getSubject())) {
            System.out.println("ERROR: Shelf already exists " + shelf);
            return Code.SHELF_EXISTS_ERROR;
        }

        shelves.put(shelf.getSubject(), shelf);

        for (Book book : books.keySet()) {
            if (book.getSubject().equals(shelf.getSubject())) {
                shelf.addBook(book);
            }
        }
        return Code.SUCCESS;
    }

    public int listBooks() {
        int total = 0;

        for (Book book : books.keySet()) {
            int count = books.get(book);
            total += count;

            System.out.println(count + " copies of "
                    + book.getTitle() + " by "
                    + book.getAuthor() + " ISBN:" + book.getIsbn());
        }
        return total;
    }


    public static int convertInt(String recordCountString, Code code) {
        try {
            return Integer.parseInt(recordCountString);
        } catch (NumberFormatException e) {
            System.out.println("Value which caused the error: " + recordCountString);
            System.out.println("Error message: " + code.getMessage());

            if (code == Code.BOOK_COUNT_ERROR) {
                System.out.println("Error: Could not read number of books");
            } else if (code == Code.PAGE_COUNT_ERROR) {
                System.out.println("Error: could not parse page count");
            } else if (code == Code.DATE_CONVERSION_ERROR) {
                System.out.println("Error: Could not parse date component");
            } else {
                System.out.println("Error: Unknown conversion error");
            }
            return code.getCode();
        }
    }

    public Reader getReaderByCard(int cardNumber) {
        for (Reader reader : readers) {
            if (reader.getCardNumber() == cardNumber) {
                return reader;
            }
        }
        System.out.println("Could not find a reader with card #" + cardNumber);
        return null;
    }

    public Code addReader(Reader reader) {
        if(readers.contains(reader)) {
            System.out.println(reader.getName() + " already has an account!");
            return Code.READER_ALREADY_EXISTS_ERROR;
        }

        for (Reader r : readers) {
            if (r.getCardNumber() == reader.getCardNumber()) {
            System.out.println(r.getName() + " and " + reader.getName() + " have the same card number!");
            return Code.READER_CARD_NUMBER_ERROR;
            }
        }
        readers.add(reader);
        System.out.println(reader.getName() + " added to the library!");
        if (reader.getCardNumber() > libraryCard) {
            libraryCard = reader.getCardNumber();
        }
        return Code.SUCCESS;
    }

    public Code removeReader(Reader reader) {
        if (!readers.contains(reader)) {
            System.out.println(reader.getName() + " is not part of this Library");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }

        if (reader.getBooks().size() > 0) {
            System.out.println(reader.getName() + " must return all books!");
            return Code.READER_STILL_HAS_BOOKS_ERROR;
        }

        readers.remove(reader);

        System.out.println(reader.getName() + " removed from the library.");

        return Code.SUCCESS;
    }

    public static LocalDate covertDate(String date, Code errorCode) {
        String[] bits = date.split("-");

        if (bits.length != 3) {
            System.out.println("ERROR: date conversion, could not parse " + date);
            System.out.println("using default date (01-jan-1970)");
            return LocalDate.of(1970,1, 1);
        }

        try {
            int year = Integer.parseInt(bits[0]);
            int month = Integer.parseInt(bits[1]);
            int day = Integer.parseInt(bits[2]);

            if (year < 0 || month < 0 || day < 0) {
                System.out.println("Error converting date: Year " + year);
                System.out.println("Error converting date: Month " + month);
                System.out.println("Error converting date: Dat " + day);
                System.out.println("Using default date (01-jan-1970)");
                return LocalDate.of(1970,1, 1);
            }
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970,1, 1);
        }
    }
     public static int getLibraryCard() {
        return libraryCard + 1;
     }

    private Code errorCode(int codeNumber) {
        for (Code code : Code.values()) {
            if (code.getCode() == codeNumber) {
                return code;
            }
        }
        return Code.UNKNOWN_ERROR;
    }

    public String getName() {
        return name;
    }

}
