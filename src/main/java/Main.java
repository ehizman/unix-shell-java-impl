import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        Scanner scanner = new Scanner(in);
        List<String> builtins = builtins();
        String cwd = getenv("PWD");

        while (true){
            out.print("$ ");
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
                        exit(0);
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
                    if (parameter.startsWith("'") || parameter.startsWith("\"")) {
                        parameter = parseQuotes(parameter);
                    }else if (parameter.matches("\\w*(\\s)+\\w*")) {
                        parameter = parameter.replaceAll("\\s+", " ");
                    }
                    out.println(parameter.trim());

                    break;
                }

                case "pwd" : {
                    out.println(cwd);
                    break;
                }

                case "cd" : {
                    if (parameter.equals("~")) {
                        cwd = getenv("HOME");
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
                case "cat": {
                    StringBuilder sb = new StringBuilder();
                    List<String> filePaths = new ArrayList<>();
                    // "/tmp/quz/f 93" "/tmp/quz/f   13" "/tmp/quz/f's15"
                    if (parameter.charAt(0)=='\''){
                        Arrays.stream(parameter.split("' '"))
                                .forEach(string -> {
                                    filePaths.add(string.replace("'", "").trim());
                                });
                    } else if (parameter.charAt(0) == '"'){
                        Arrays.stream(parameter.split("\" \""))
                                .forEach(string -> {
                                    if (string.contains("\"")) filePaths.add(string.replace("\"", "").trim());
                                    else filePaths.add(string);
                                });
                    }

                    filePaths.forEach(string -> {
                                File file = new File(string);
                                try {
                                    Scanner sc = new Scanner(file);
                                    while (sc.hasNext()) {
                                        String contents = sc.nextLine();
                                        sb.append(contents);
                                    }
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    out.println(sb);

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
        for (String dir : getenv("PATH").split(":")) {
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

    private static String parseQuotes(String parameter) { //  "script  test"  "hello""example"
        StringBuilder result = new StringBuilder();
        int sPtr = 0, fPtr = 1;
        char quoteChar = parameter.charAt(0) == '"' ? '"' : '\'';
        boolean foundQuote;
        while (fPtr < parameter.length()) {
            foundQuote = parameter.charAt(fPtr) == quoteChar; //false
            if (foundQuote && (fPtr - sPtr > 1)) {
                if (parameter.substring(sPtr+1, fPtr).isBlank()){
                    result.append(" ");
                } else {
                    result.append(parameter, sPtr+1, fPtr);
                }
                sPtr = fPtr;
            } else if(foundQuote) {
                sPtr = fPtr;
            }
            fPtr++;
        }
        return result.toString();
    }
}
