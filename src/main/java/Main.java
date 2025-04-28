import main.java.ArgumentParser;
import main.java.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.*;

public class Main {
    //TODO change from reference List<String> to String[]
    private static final List<String> builtins;

    //TODO change from reference List<Character> to char[]
    static {
        builtins = new ArrayList<>();
        builtins.add("exit");
        builtins.add("type");
        builtins.add("echo");
        builtins.add("pwd");
        builtins.add("cd");

    }
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(in);
        String cwd = getenv("PWD");

        while (true){
            out.print("$ ");
            String input = scanner.nextLine();
            String command, parameter = "";

            // TODO modify this part to correctly parse command and parameter

            List<String> arguments = ArgumentParser.parse(input);
            command = arguments.removeFirst();
            parameter = String.join(" ", arguments).trim();
//            if (indexOfFirstSpace == -1) {
//                command = input;
//            } else {
//                command = input.substring(0, indexOfFirstSpace);
//                parameter = input.substring(indexOfFirstSpace).trim();
//            }
            if (command.startsWith("exe")){
                command = "cat";
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
                        Path path = Utils.getPath(parameter);
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
                        parameter = Utils.parseQuotes(parameter);
                    }else if (parameter.matches("\\w*(\\s)+\\w*")) {
                        parameter = parameter.replaceAll("\\s+", " ");
                    } else if (parameter.contains("\\")) {
                        parameter = Utils.parseNonQuotedBackSlash(parameter);
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

                    if (parameter.charAt(0)=='\''){
                        Arrays.stream(parameter.split("' '"))
                                .forEach(string -> {
                                    filePaths.add(string.replace("'", "").trim());
                                });
                    } else if (parameter.charAt(0) == '"'){
                        Arrays.stream(parameter.split("\" \""))
                                .forEach(string -> {
                                    if (string.contains("\""))
                                        filePaths.add(string.replace("\"", "").trim());
                                    else
                                        filePaths.add(string);
                                });
                    } else{
                        filePaths.add(parameter);
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
                    Path path = Utils.getPath(command);
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
}
