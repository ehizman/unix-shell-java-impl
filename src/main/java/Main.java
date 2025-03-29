import javax.swing.*;
import java.util.Scanner;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage

        System.out.print("$ ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        while (!input.startsWith("exit")){

            if (input.startsWith("type")) {
                handleTypeCommand(input);
            } else if (input.startsWith("echo")) {
                String command = input.substring(5);
                handleEchoCommand(command);
            } else{
                out.println(input + ": command not found");
            }
            System.out.print("$ ");
            input = scanner.nextLine();
        }
    }

    private static void handleTypeCommand(String input) {
        String command = input.substring(5);

        if (command.equalsIgnoreCase("type") || command.equalsIgnoreCase("echo") || command.equalsIgnoreCase("exit")){
            out.println(command + " is a shell builtin");
        } else {
            out.println(input+ ": command not found");
        }
    }

    private static void handleEchoCommand(String input) {
        String command = input.substring(5);
        out.println(command);
    }
}
