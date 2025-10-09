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
            String[] bits = line.split(", ", -1);

            if (bits.length <= Book.DUE_DATE) {
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

    public Shelf getShelf(String subject) {
        return shelves.get(subject);
    }

    public LocalDate convertDate(String date, Code errorCode) {
        return null;
    }

    public int listReaders() {
        return 0;
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
        return null;
    }

    public Book getBookByISBN(String isbn) {
        return null;
    }

    public int listShelves() {
        return  0;
    }



    private Code initShelves(int shelfCount, Scanner scan) {
        if (shelfCount < 1) {
            return Code.SHELF_NUMBER_PARSE_ERROR;
        }

        for (int i = 0; i < shelfCount; i++) {
            if (!scan.hasNextLine()) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }


            String line = scan.nextLine();
            String[] parts = line.split(", ", -1);

            if (parts.length < 2) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }

            int shelfNumber = convertInt(parts[0].trim(), Code.SHELF_NUMBER_PARSE_ERROR);
            if (shelfNumber < 1) {
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }

            String shelfSubject = parts[1].trim();
            addShelf(shelfSubject);
        }

        if (shelves.size() == shelfCount) {
            return Code.SUCCESS;
        } else {
            System.out.println("Number of shelves doesn't match expected");
            return Code.SHELF_NUMBER_PARSE_ERROR;
        }
    }


    public Code addShelf(String shelfSubject) {
        return null;
    }

    public int listBooks() {
        return 0;
    }


    public static int convertInt(String recordCountString, Code code) {
        return 0;
    }
}
