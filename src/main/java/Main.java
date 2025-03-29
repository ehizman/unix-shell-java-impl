import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage

         System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.contains("exit")){
            exit(0);
            return;
        }
        System.out.println(input + ": command not found");
        main(null);
    }
}
