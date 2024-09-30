package common.iostream;

import java.util.ArrayList;
import java.util.Date;

public interface Input {
    int getInt();
    String getString();
    Date getDate();
    ArrayList<String> getStrings(int countElements);
}
