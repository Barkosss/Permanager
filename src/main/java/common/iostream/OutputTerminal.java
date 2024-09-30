package common.iostream;

public class OutputTerminal implements Output {

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

    public void outDate() {

    }
}
