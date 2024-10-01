package common.iostream;


public class OutputTerminal implements Output {

    public <Type> void output(Type outData, boolean inline) {
        if (inline) {
            System.out.print(outData);
        } else {
            System.out.println(outData);
        }
    }

    /* Под вопросом. Оставил на всякий случай
    public void outString(String outString, boolean inline) {
        if (inline) {
            System.out.print(outString);
        } else {
            System.out.println(outString);
        }
    }

    public void outInt(int outInt, boolean inline) {
        if (inline) {
            System.out.print(outInt);
        } else {
            System.out.println(outInt);
        }
    }

    public void outDate(Date outDate, boolean inline) {
        if (inline) {
            System.out.print(outDate);
        } else {
            System.out.println(outDate);
        }
    }
    */
}
