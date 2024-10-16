package common.iostream;

import common.models.Interaction;

public class OutputTerminal implements Output {

    // Вывести значение в терминал
    public void output(Interaction interaction, boolean inline) {
        String message = interaction.getMessage();
        // Если inline == true, не делаем перенос строки
        if (inline) {
            System.out.print(message);
        } else { // Если inline == false, делаем перенос строки
            System.out.println(message);
        }
    }
}
