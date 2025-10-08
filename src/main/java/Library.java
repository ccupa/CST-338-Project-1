import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Library  {
    public static final int LENDING_LIMIT = 0;
    private HashMap<Book, Integer> books = new HashMap<>();
    private int libraryCard;
    private String name;
    private List<Reader> readers = new ArrayList<>();
    private HashMap<String, Shelf> shelves = new HashMap<>();

}
