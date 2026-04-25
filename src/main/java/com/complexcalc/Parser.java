package com.complexcalc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private enum TokenType {
        ADD,
        SUB,
        UN_SUB,
        MULT,
        DIV,
        EXP,
        VAR,
        LPAR,
        RPAR,
        NUM,
        SIN,
        COS,
        TAN,
        LN,
        LOG,
    }

    //INFO: order by longest first for correct parsing
    private static Map<String, TokenType> wordFunctions = new LinkedHashMap<>(
        Map.of("log", TokenType.LOG, "sin", TokenType.SIN, "cos", TokenType.COS)
    );

    private record Token(TokenType type, double value) {}

    public static List<Token> parse(String s) {
        List<Token> tokens = new ArrayList<>();

        int digitStart = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') continue;

            if (Character.isDigit(c) || c == '.') {
                if (digitStart == -1) digitStart = i;
                continue;
            }

            if (digitStart != -1) {
                tokens.add(new Token(TokenType.NUM, Double.parseDouble(s.substring(digitStart, i))));
                digitStart = -1;
            }

            switch (c) {
                case '+' -> tokens.add(new Token(TokenType.ADD, 1));
                case '-' -> {
                    TokenType lastToken;
                    if (i == 0) tokens.add(new Token(TokenType.UN_SUB, 3));
                    else {
                        lastToken = tokens.getLast().type;
                        if (
                            lastToken != TokenType.NUM && lastToken != TokenType.VAR && lastToken != TokenType.RPAR
                        ) tokens.add(new Token(TokenType.UN_SUB, 3));
                        else tokens.add(new Token(TokenType.SUB, 1));
                    }
                }
                case '*' -> tokens.add(new Token(TokenType.MULT, 2));
                case '/' -> tokens.add(new Token(TokenType.DIV, 2));
                case '^' -> tokens.add(new Token(TokenType.EXP, 4));
                case '(' -> tokens.add(new Token(TokenType.LPAR, 5));
                case ')' -> tokens.add(new Token(TokenType.RPAR, 5));
                default -> {
                    if (Character.isAlphabetic(c)) {
                        boolean wordFound = false;
                        for (String function : wordFunctions.keySet()) {
                            if (s.substring(i).startsWith(function)) {
                                tokens.add(new Token(wordFunctions.get(function), 3));
                                i += function.length() - 1;
                                wordFound = true;
                                break;
                            }
                        }
                        if (!wordFound) tokens.add(new Token(TokenType.VAR, c));
                    } else throw new IllegalArgumentException();
                }
            }
        }
        if (digitStart != -1) tokens.add(new Token(TokenType.NUM, Double.parseDouble(s.substring(digitStart))));
        return tokens;
    }
}
