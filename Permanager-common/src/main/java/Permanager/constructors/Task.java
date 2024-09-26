package constructors;

import java.util.Date;

public class Task {

    public long id;

    public String title;

    public String description;

    public Date timeStart;

    public Date timeEnd;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public static boolean isValidDate(String date) {
        // ...
        return false;
    }

    public void help() {
        // ...
    }

    public void create() {
        // ...
    }

    public void edit() {
        // ...
    }

    public void remove() {
        // ...
    }

    public void list() {
        // ...
    }

    public void clear() {
        // ...
    }
};