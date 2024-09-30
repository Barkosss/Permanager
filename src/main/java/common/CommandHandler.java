package common;

import common.iostream.Input;
import common.iostream.InputTerminal;

public class CommandHandler {

    public static void main(String[] args) {
        Input input = new InputTerminal();
        String commandName;

        while(true) {
            commandName = input.getString().toLowerCase();
            if (commandName.equals("exit")) {
                break;
            }


        }
    }
}
