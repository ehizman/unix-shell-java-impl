package main.java;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
    public static List<String> parse(String input) {
        List<String> arguments = new ArrayList<>();
        String command = parseCommand(input);
        // call parseQuote methods from Utils
        // add result of parseQuote method to list
        arguments.add(Utils.parseQuotes(command));
        // take substring of input from command to the end of input string as parameters
        String parameters = input.substring(command.length());
        // split by space character
       arguments.add(parameters);
        return arguments;
    }

    private static String parseCommand(String input) {
        if (input.startsWith("'") || input.startsWith("\"")) {
            // find the enclosing quote character
            char enclosingQuote = input.charAt(0) == '\'' ? '\'' : '"';
            for (int i = 1; i < input.length(); i++) {
                char character = input.charAt(i);
                if (character == enclosingQuote &&
                        i-2 > 0 &&
                            (input.charAt(i-1) != '\\' || (input.charAt(i-2) == '\\' && input.charAt(i-1)=='\\'))){
                    return input.substring(0, i+1);
                }
            }
            return input;
            // take the substring from the enclosing quote characters to the beginning of the string
        } else {
            return input.split(" ")[0];
        }
    }
}
