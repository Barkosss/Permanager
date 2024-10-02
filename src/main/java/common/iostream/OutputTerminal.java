package common.iostream;

public class OutputTerminal implements Output {

    public <Type> void output(Type outData, boolean inline) {
        if (inline) {
            System.out.print(outData);
        } else {
            System.out.println(outData);
        }
    }
}
