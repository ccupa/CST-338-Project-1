import Utilities.Code;

import java.io.File;
import java.io.FileNotFoundException;
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

    public int listReaders() {
        return 0;
    }

    private Code initReader(int readerCount, Scanner scan) {
        return null;
    }

    public int listShelves() {
        return  0;
    }

    private Code initShelves(int shelfCount, Scanner scan) {
        return null;
    }

    public int listBooks() {
        return 0;
    }

    private Code initBooks(int bookCount, Scanner scan) {
        return null;
    }

    public static int convertInt(String recordCountString, Code code) {
        return 0;
    }
}
