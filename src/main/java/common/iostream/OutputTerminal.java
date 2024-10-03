package common.iostream;

public class OutputTerminal implements Output {

    public void output(Object outData, boolean inline) {
        if (inline) {
            System.out.print(outData);
        } else {
            System.out.println(outData);
        }
    }
}
