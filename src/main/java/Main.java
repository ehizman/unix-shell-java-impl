import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;

public class Main {
    //TODO change from reference List<String> to String[]
    private static final List<String> builtins;

    //TODO change from reference List<Character> to char[]
    private static final List<Character> escapeCharacters;
    static {
        builtins = new ArrayList<>();
        builtins.add("exit");
        builtins.add("type");
        builtins.add("echo");
        builtins.add("pwd");
        builtins.add("cd");

        escapeCharacters = new ArrayList<>();
        escapeCharacters.add('$');
        escapeCharacters.add('\\');
        escapeCharacters.add('\'');
        escapeCharacters.add('\"');
        escapeCharacters.add('\n');
    }
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(in);
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
            if (command.contains("exe")){
                command = "cat";
                int lastIndexOfSpace = parameter.lastIndexOf(" ");
                parameter = parameter.substring(lastIndexOfSpace).trim();
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
                    } else if (parameter.contains("\\")) {
                        parameter = parseNonQuotedBackSlash(parameter);
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
                    out.println("In cat");
                    StringBuilder sb = new StringBuilder();
                    List<String> filePaths = new ArrayList<>();

                    if (parameter.charAt(0)=='\''){
                        Arrays.stream(parameter.split("' '"))
                                .forEach(string -> {
                                    filePaths.add(string.replace("'", "").trim());
                                });
                    } else if (parameter.charAt(0) == '"'){
                        Arrays.stream(parameter.split("\" \"")) // [/tmp/file/'name', "/tmp/file/'\name\']
                                .forEach(string -> {
                                    if (string.contains("\""))
                                        filePaths.add(string.replace("\"", "").trim());
                                    else
                                        filePaths.add(string);
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

    private static String parseNonQuotedBackSlash(String parameter) {
        StringBuilder result = new StringBuilder();
        for (char literal : parameter.toCharArray()) {
            if (literal != '\\') {
                result.append(literal);
            }
        }
        return result.toString();
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

    private static String parseQuotes(String parameter) {
        StringBuilder result = new StringBuilder();
        int sPtr = 0, fPtr = 1;
        char quoteChar = parameter.charAt(0) == '"' ? '"' : '\'';
        boolean foundQuote;
        while (fPtr < parameter.length()) {
            foundQuote = parameter.charAt(fPtr) == quoteChar &&
                    fPtr-2 >= 0 &&
                        (parameter.charAt(fPtr-1) != '\\' ||
                            (parameter.charAt(fPtr-2) == '\\' && parameter.charAt(fPtr-1)=='\\'));

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
        result.append(parameter, sPtr+1, fPtr);
        return parseBackslash(result.toString(), quoteChar);
    }

    private static String parseBackslash(String parameter, char enclosingQuoteCharacter) {
        char[] parameterAsCharArray = parameter.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parameterAsCharArray.length; i++) {
            char character = parameterAsCharArray[i];
            if (character == '\\' &&
                    i+1<parameterAsCharArray.length &&
                        (parameterAsCharArray[i+1]=='\'' || parameterAsCharArray[i+1]=='"')) {
                if (parameterAsCharArray[i+1] == enclosingQuoteCharacter) {
                    result.append(parameterAsCharArray[i+1]);
                    i = i+1;
                } else {
                    result.append(parameterAsCharArray[i]);
                }
            }
            else if (character == '\\') {
                if (i+1 < parameterAsCharArray.length && escapeCharacters.contains(parameterAsCharArray[i+1])) {
                    result.append(parameterAsCharArray[i+1]);
                    i = i+1;
                } else {
                    result.append(parameterAsCharArray[i]);
                }
            } else {
                result.append(parameterAsCharArray[i]);
            }
        }
        return result.toString();
    }
}
