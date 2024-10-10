package common.iostream;

public class OutputTerminal implements Output {

    // Вывести значение в терминал
    public void output(Object outData, boolean inline) {
        // Если inline == true, не делаем перенос строки
        if (inline) {
            System.out.print(outData);
        } else { // Если inline == false, делаем перенос строки
            System.out.println(outData);
        }
    }
}
