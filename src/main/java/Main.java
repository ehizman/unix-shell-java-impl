import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        Scanner scanner = new Scanner(System.in);
        String[] builtins = builtins();

        while (true){
            System.out.print("$ ");
            String input = scanner.nextLine();
            int indexOfFirstSpace = input.indexOf(" ");
            String command, parameter = "";

            if (indexOfFirstSpace == -1) {
                command = input;
            } else {
                command = input.substring(0, indexOfFirstSpace);
                parameter = input.substring(indexOfFirstSpace).trim();
            }

            switch (command) {
                case "exit": {
                    if (parameter.equals("0")){
                        System.exit(0);
                    } else {
                        out.println(input + ": command not found");
                    }
                    break;
                }

                case "type": {
                    if (parameter.equalsIgnoreCase(builtins[0])||
                        parameter.equalsIgnoreCase(builtins[1])||
                        parameter.equalsIgnoreCase(builtins[2])) {
                        out.println(parameter + " is a shell builtin");
                    } else {
                        String pathName = getPath(parameter);
                        if (pathName != null) {
                            out.println(parameter + " is " + pathName);;
                        } else {
                            out.println(parameter + ": not found");
                        }
                    }
                    break;
                }

                case "echo": {
                    out.println(parameter);
                    break;
                }
                default: out.println(input + ": command not found");
            }
        }
    }

    public static String getPath(String parameterStr) {
        for (String dir : System.getenv("PATH").split(":")) {
            Path fullPath = Path.of(dir, parameterStr);
            if (Files.isRegularFile(fullPath)){
                return fullPath.toString();
            }
        }
        return null;
    }
    private static String[] builtins() {
        return new String[]{"exit", "type", "echo"};
    }
}
