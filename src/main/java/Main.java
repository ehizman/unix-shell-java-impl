import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage

         System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.matches("exit [0-9]")){
            return;
        }
        System.out.println(input + ": command not found");
        if (input.matches("echo (\\s+\\w+)*")) {
            String command = input.replace("echo ", "");
            out.println(command);
            return;
        }
        main(null);
    }
}
