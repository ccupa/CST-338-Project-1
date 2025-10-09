import Utilities.Code;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @version  1.3.1.
 */

class LibraryTest {

    Library csumb = null;

    String library00 = "Library00.csv";
    String library01 = "Library01.csv";
    String badBooks0 = "badBooks0.csv";
    String badBooks1 = "badBooks1.csv";
    String badShelves0 = "badShelves0.csv";
    String badShelves1 = "badShelves1.csv";
    String badReader0 = "badReader0.csv";
    String badReader1 = "badReader1.csv";

    @BeforeEach
    void setUp() {
        csumb = new Library("CSUMB");
    }

    @AfterEach
    void tearDown() {
        csumb = null;
    }

    @Test
    void init_test() {
        //Bad file
        assertEquals(Code.FILE_NOT_FOUND_ERROR, csumb.init("nope.csv"));
        assertEquals(Code.BOOK_COUNT_ERROR, csumb.init(badBooks0));
        assertEquals(Code.BOOK_RECORD_COUNT_ERROR, csumb.init(badBooks1) );
        assertEquals(Code.SHELF_COUNT_ERROR,csumb.init(badShelves0));
        assertEquals(Code.SHELF_NUMBER_PARSE_ERROR,csumb.init(badShelves1));
    }

    @Test
    void init_goodFile_test() {
    }

    @Test
    void addBook() {
    }

    @Test
    void returnBook() {
    }

    @Test
    void testReturnBook() {
    }

    @Test
    void listBooks() {
    }

    @Test
    void checkOutBook() {
    }

    @Test
    void getBookByISBN() {
    }

    @Test
    void listShelves() {
    }

    @Test
    void addShelf() {
    }

    @Test
    void testAddShelf() {
    }

    @Test
    void getShelf() {
    }

    @Test
    void testGetShelf() {
    }

    @Test
    void listReaders() {
    }

    @Test
    void testListReaders() {
    }

    @Test
    void getReaderByCard() {
    }

    @Test
    void addReader() {
    }

    @Test
    void removeReader() {
    }

    @Test
    void convertInt() {
    }

    @Test
    void convertDate() {
    }

    @Test
    void getLibraryCardNumber() {
    }
}