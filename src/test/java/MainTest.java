import Utilities.Code;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This file serves as a proxy for a legitimate Library test suite.
 * It is not a complete test suite, but it does test the basic functionality.
 * If all of these tests pass, you can be confident that your code is working at least 82% or so.
 */
public class MainTest {

  private final String CSUMB = "CSUMB";

  Library csumb;
@BeforeAll
static void overallSetup(){
  System.out.println("Version: 1.3.2");
}
  @BeforeEach
  void init(){
    csumb = new Library(CSUMB);
    String LIBRARY00 = "Library00.csv";
    assertEquals(Code.SUCCESS, csumb.init(LIBRARY00));
  }
  @Test
  void badCsvTest(){
    assertEquals(Code.FILE_NOT_FOUND_ERROR, csumb.init("Sir not appearing in this file"));
  }

  @Test
  void getNameTest(){
    assertEquals(CSUMB,csumb.getName());
  }

  @Test
  void numberBooksTest(){
    int numberBooks = csumb.listBooks();
    assertEquals(9,csumb.listBooks());
    System.out.println("total books: " + numberBooks);
  }

  @Test
  void numberReadersTest(){
    int numberReaders = csumb.listReaders();
    assertEquals(4,csumb.listReaders());
    System.out.println("total readers: " + numberReaders);
  }

  @Test
  void numberShelvesTest(){
    assertEquals(3,csumb.listShelves(true));
  }

  @Test
  void addBookTest(){
    Shelf scifi = csumb.getShelf("sci-fi");
    assertNotNull(scifi);
    assertNull(csumb.getShelf("Grimdark"));
    int shelfCount = csumb.listShelves();
    Shelf romance = csumb.getShelf(shelfCount+1);
    assertNull(romance);
    romance = csumb.getShelf("Romance");
    assertNull(romance);
    assertEquals(Code.SUCCESS,csumb.addShelf("Romance"));
    romance = csumb.getShelf("Romance");
    assertNotNull(romance);
    System.out.println(romance);
    String grimdark = "GrimDark";
    Book bestServedCold = new Book(
                          "12345",
                          "Best Served Cold",
                          grimdark,
                          235,
                          "Joe Abercrombie",
                          LocalDate.now()
                          );
    assertEquals(Code.SHELF_SUBJECT_MISMATCH_ERROR,scifi.addBook(bestServedCold));
    assertEquals(Code.SHELF_EXISTS_ERROR,csumb.addBook(bestServedCold));
    assertEquals(Code.SUCCESS,csumb.addShelf(grimdark));
    assertEquals(Code.SHELF_EXISTS_ERROR,csumb.addShelf(grimdark));

    assertEquals(4,csumb.listReaders(true));

    assertEquals(5, csumb.listShelves(true));


    Reader drew = csumb.getReaderByCard(1);

    assertNotNull(drew);

    assertEquals(Code.SUCCESS,csumb.checkOutBook(drew,bestServedCold));

    csumb.listReaders(true);

   assertEquals(Code.SUCCESS,csumb.returnBook(drew,csumb.getBookByISBN("12345")));

   assertEquals(Code.READER_DOESNT_HAVE_BOOK_ERROR,csumb.returnBook(drew,csumb.getBookByISBN("42-w-87")));

  }

}
