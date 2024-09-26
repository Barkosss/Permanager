package Permanager;

import java.util.ArrayList;

// Чтение входных данных с терминала
public class InputTerminal {
    public static ArrayList<String> arguments = new ArrayList<String>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        while(true) {
            arguments.add(input.nextLint());
        }
    }
}