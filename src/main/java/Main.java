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

            String[] str = input.split(" ");
            String command = str[0];
            StringBuilder parameter = new StringBuilder();

            if (str.length > 2) {
                for (int i = 1; i < str.length ; i++) {
                    if (i < str.length-1) {
                        parameter.append(str[i]).append(" ");
                    } else {
                        parameter.append(str[i]);
                    }
                }
            }
            String parameterStr = parameter.toString();

            switch (command) {
                case "exit": {
                    if (parameterStr.equals("0")){
                        System.exit(0);
                    } else {
                        out.println(input + ": command not found");
                    }
                    break;
                }

                case "type": {
                    if (parameterStr.equalsIgnoreCase(builtins[0])||
                        parameterStr.equalsIgnoreCase(builtins[1])||
                        parameterStr.equalsIgnoreCase(builtins[2])) {
                        out.println(command + " is a shell builtin");
                    } else {
                        out.println(command+ ": not found");
                        String pathName = getPath(parameterStr);
                        if (pathName != null) {
                            out.println(parameterStr + " is " + pathName);;
                        } else {
                            out.println(parameterStr + ": not found");
                        }
                    }
                    break;
                }

                case "echo": {
                    out.println(parameterStr);
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
