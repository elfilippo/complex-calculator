package com.complexcalc.evaluator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LatexLexer {

    static final Map<String, Token> wordFunctions = new LinkedHashMap<>();

    static {
        //IS: word functions
        //USAGE: string that should be parsed as the function first, then name of the Token
        String[][] entries = {
            //INFO: order by longest first for correct parsing
            { "floor-pair", "FLOOR" },
            { "ceil-pair", "CEIL" },
            { "arccosh", "ACOSH" },
            { "arccoth", "ACOTH" },
            { "arccsch", "ACSCH" },
            { "arcsech", "ASECH" },
            { "arcsinh", "ASINH" },
            { "arctanh", "ATANH" },
            { "acosh", "ACOSH" },
            { "acoth", "ACOTH" },
            { "acsch", "ACSCH" },
            { "arccos", "ACOS" },
            { "arccot", "ACOT" },
            { "arccsc", "ACSC" },
            { "arcsec", "ASEC" },
            { "arcsin", "ASIN" },
            { "arctan", "ATAN" },
            { "asech", "ASECH" },
            { "asinh", "ASINH" },
            { "atanh", "ATANH" },
            { "minus", "MINUS" },
            { "times", "MULT" },
            { "acos", "ACOS" },
            { "acot", "ACOT" },
            { "acsc", "ACSC" },
            { "asec", "ASEC" },
            { "asin", "ASIN" },
            { "atan", "ATAN" },
            { "cosh", "COSH" },
            { "coth", "COTH" },
            { "csch", "CSCH" },
            { "frac", "FRAC" },
            { "prod", "PROD" },
            { "sech", "SECH" },
            { "sinh", "SINH" },
            { "tanh", "TANH" },
            { "plus", "ADD" },
            { "abs", "ABS" },
            { "cos", "COS" },
            { "cot", "COT" },
            { "csc", "CSC" },
            { "div", "DIV" },
            { "exp", "EXP" },
            { "log", "LOG" },
            { "sec", "SEC" },
            { "sin", "SIN" },
            { "sum", "SUM" },
            { "tan", "TAN" },
            { "ln", "LOG" },
        };

        for (String[] e : entries) {
            wordFunctions.put(e[0], Token.valueOf(e[1]));
        }
    }

    //IS: word functions that take two arguments with no special behavior
    static Map<String, Token> braceArguments = new LinkedHashMap<>(
        Map.of("atan2", Token.ATAN2, "hypot", Token.HYPOT, "sqrt", Token.ROOT, "frac", Token.FRAC)
    );

    //IS: word functions that shouldn't parse to a token, but directly to a number
    static Map<String, Double> numbers = new LinkedHashMap<>(Map.of("pi", Math.PI, "tau", Math.TAU));

    //TODO: add with other custom function, expand
    static Map<String, Token> complexOperations = new LinkedHashMap<>(Map.of("conj", Token.CONJ));

    record valueToken(Token type, double value) {}

    public static List<valueToken> tokenize(String s) {
        List<valueToken> tokens = new ArrayList<>();

        int digitStart = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') continue;

            if (Character.isDigit(c) || c == '.') {
                if (digitStart == -1) digitStart = i;
                continue;
            }

            if (digitStart != -1) {
                tokens.add(new valueToken(Token.NUM, Double.parseDouble(s.substring(digitStart, i))));
                digitStart = -1;
            }
            switch (c) {
                case '+' -> tokens.add(new valueToken(Token.ADD, 1));
                case '-', '−' -> {
                    Token lastToken;
                    if (i == 0) tokens.add(new valueToken(Token.UMINUS, 3));
                    else {
                        lastToken = tokens.getLast().type;
                        if (
                            lastToken != Token.NUM &&
                            lastToken != Token.VAR &&
                            lastToken != Token.RPAR &&
                            lastToken != Token.RBRACE &&
                            lastToken != Token.FACT
                        ) tokens.add(new valueToken(Token.UMINUS, 3));
                        else tokens.add(new valueToken(Token.MINUS, 1));
                    }
                }
                case '*', '×' -> tokens.add(new valueToken(Token.MULT, 2));
                case '/', '÷' -> tokens.add(new valueToken(Token.DIV, 2));
                case '!' -> tokens.add(new valueToken(Token.FACT, 4));
                case '^' -> tokens.add(new valueToken(Token.POW, 4));
                case '(' -> tokens.add(new valueToken(Token.LPAR, 5));
                case ')' -> tokens.add(new valueToken(Token.RPAR, 5));
                case '{', '[' -> tokens.add(new valueToken(Token.LBRACE, 5));
                case '}', ']' -> tokens.add(new valueToken(Token.RBRACE, 5));
                case '_' -> tokens.add(new valueToken(Token.SUBS, 5));
                case '=' -> tokens.add(new valueToken(Token.EQUALS, 6));
                case '\\' -> {
                    for (String function : wordFunctions.keySet()) {
                        if (s.substring(i + 1).startsWith(function)) {
                            tokens.add(new valueToken(wordFunctions.get(function), 3));
                            i += function.length();
                            break;
                        }
                    }
                    for (String function : braceArguments.keySet()) {
                        if (s.substring(i + 1).startsWith(function)) {
                            tokens.add(new valueToken(braceArguments.get(function), 3));
                            i += function.length();
                            break;
                        }
                    }
                    for (String number : numbers.keySet()) {
                        if (s.substring(i + 1).startsWith(number)) {
                            tokens.add(new valueToken(Token.NUM, numbers.get(number)));
                            i += number.length();
                            break;
                        }
                    }
                }
                default -> {
                    if (Character.isAlphabetic(c)) {
                        boolean wordFound = false;
                        //TODO: add support for custom functions (atan2, etc)
                        /**
                        for (String function : wordFunctions.keySet()) {
                            if (s.substring(i).startsWith(function)) {
                                tokens.add(new Token(wordFunctions.get(function), 3));
                                i += function.length() - 1;
                                wordFound = true;
                                break;
                            }
                        }
                        for (String function : multipleArguments.keySet()) {
                            if (s.substring(i).startsWith(function)) {
                                tokens.add(new Token(multipleArguments.get(function), 3));
                                i += function.length() - 1;
                                wordFound = true;
                                break;
                            }
                        }
                        */
                        if (!wordFound) tokens.add(new valueToken(Token.VAR, c));
                    } else throw new IllegalArgumentException();
                }
            }
        }
        if (digitStart != -1) tokens.add(new valueToken(Token.NUM, Double.parseDouble(s.substring(digitStart))));

        //IS: list of tokens that are allowed between two expressions
        //INFO: implied multiplication doesn't add mult tokens after these
        List<Token> allowedToEnd = new ArrayList<>(
            List.of(Token.ADD, Token.DIV, Token.POW, Token.MULT, Token.MINUS, Token.EQUALS, Token.UMINUS)
        );

        //DOES: insert mult tokens for implied multiplication
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (!allowedToEnd.contains(tokens.get(i).type) && !allowedToEnd.contains(tokens.get(i + 1).type)) {
                if (
                    wordFunctions.containsValue(tokens.get(i).type) ||
                    braceArguments.containsValue(tokens.get(i).type) ||
                    Token.LPAR == tokens.get(i).type ||
                    Token.RPAR == tokens.get(i + 1).type ||
                    Token.LBRACE == tokens.get(i).type ||
                    Token.LBRACE == tokens.get(i + 1).type ||
                    Token.RBRACE == tokens.get(i + 1).type ||
                    (Token.RBRACE == tokens.get(i).type && Token.LBRACE == tokens.get(i + 1).type) ||
                    Token.FACT == tokens.get(i + 1).type
                ) continue;
                tokens.add(i + 1, new valueToken(Token.MULT, 2));
                i++;
            }
        }
        return tokens;
    }
}
