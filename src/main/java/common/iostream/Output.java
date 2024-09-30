package common.iostream;

public interface Output {
    void outString(String outString, boolean inline);

    void outInt(int outInt, boolean inline);

    void outDate();
}