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
                String command = input.substring(5);
                if (command.startsWith("exit") || command.startsWith("echo")) {
                    out.println(command +" is a shell builtin");
                } else {
                    out.println(command + ": not found");
                }
            }
            if (input.startsWith("echo")) {
                System.out.println(input.substring(5));
            }
            out.println(input + ": command not found");
            System.out.print("$ ");
            input = scanner.nextLine();
        }
    }
}
