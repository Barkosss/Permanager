package common.iostream;


public interface Output {
    <Type> void output(Type outString, boolean inline);

    /* Под вопросом. Оставил на всякий случай
    void outString(String outString, boolean inline);

    void outInt(int outInt, boolean inline);

    void outDate(Date outDate, boolean inline);
    */
}