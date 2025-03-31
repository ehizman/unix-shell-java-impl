import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        Scanner scanner = new Scanner(System.in);
        List<String> builtins = builtins();
        String cwd = System.getenv("PWD");

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
                    if (builtins.contains(parameter)) {
                        out.println(parameter + " is a shell builtin");
                    } else {
                        Path path = getPath(parameter);
                        if (path != null) {
                            out.println(parameter + " is " + path);;
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

                case "pwd" : {
                    out.println(cwd);
                    break;
                }

                case "cd" : {
                    if (parameter.equals("~")) {
                        cwd = System.getenv("HOME");
                    } else {
                        Path newPath = Path.of(cwd).resolve(parameter).normalize();
                        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                            cwd = newPath.toString();
                        } else {
                            out.println("cd: " + parameter + ": No such file or directory");
                        }
                    }

                    break;
                }

                default: {
                    Path path = getPath(command);
                    if (path != null){
                        Process process = Runtime.getRuntime().exec(new String[]{command, parameter});
                        process.getInputStream().transferTo(out);
                    } else {
                        out.println(input + ": command not found");
                    }
                }
            }
        }
    }

    public static Path getPath(String parameter) {
        for (String dir : System.getenv("PATH").split(":")) {
            Path fullPath = Path.of(dir, parameter);
            if (Files.isRegularFile(fullPath)){
                return fullPath;
            }
        }
        return null;
    }
    private static List<String> builtins() {
        List<String> builtins = new ArrayList<>();
        builtins.add("exit");
        builtins.add("type");
        builtins.add("echo");
        builtins.add("pwd");
        builtins.add("cd");
        return builtins;
    }
}
