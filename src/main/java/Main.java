import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage

        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.startsWith("exit")){
            return;
        }
        if (input.startsWith("echo")) {
            System.out.println(input.substring(5));
        } else {
            out.println(input + ": command not found");
        }
    }
}
