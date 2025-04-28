package main.java;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getenv;

public class Utils {
    private static final List<Character> escapeCharacters;
    static {

        escapeCharacters = new ArrayList<>();
        escapeCharacters.add('$');
        escapeCharacters.add('\\');
        escapeCharacters.add('\'');
        escapeCharacters.add('\"');
        escapeCharacters.add('\n');
    }
    public static String parseNonQuotedBackSlash(String parameter) {
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

    public static String parseQuotes(String parameter) {
        if (parameter.charAt(0) != '"' && parameter.charAt(0) != '\''){
            parameter = "'"+parameter;
        }
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

    public static void main(String[] args) {
        System.out.println(parseQuotes("\"world'test'\\\\'example\""));
    }
}
