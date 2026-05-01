package com.complexcalc.evaluator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    public static enum TokenType {
        ADD,
        SUB,
        UN_SUB,
        MULT,
        DIV,
        POW,
        VAR,
        LPAR,
        RPAR,
        NUM,
        SIN,
        SINH,
        ASIN,
        ASINH,
        COS,
        COSH,
        ACOS,
        ACOSH,
        TAN,
        TANH,
        ATAN,
        ATANH,
        COT,
        COTH,
        ACOT,
        ACOTH,
        CSC,
        CSCH,
        ACSC,
        ACSCH,
        SEC,
        SECH,
        ASEC,
        ASECH,
        ATAN2,
        HYPOT,
        LOG,
        LOG10,
        FLOOR,
        CEIL,
        ROUND,
        SQRT,
        COMMA,
        ABS,
        ROOT,
        EXP,
        CONJ,
    }

    static final Map<String, TokenType> wordFunctions = new LinkedHashMap<>();

    static {
        //INFO: order by longest first for correct parsing
        wordFunctions.put("arcsinh", TokenType.ASINH);
        wordFunctions.put("arccosh", TokenType.ACOSH);
        wordFunctions.put("arctanh", TokenType.ATANH);
        wordFunctions.put("arccoth", TokenType.ACOTH);
        wordFunctions.put("arcsech", TokenType.ASECH);
        wordFunctions.put("arccsch", TokenType.ACSCH);

        wordFunctions.put("arcsin", TokenType.ASIN);
        wordFunctions.put("arccos", TokenType.ACOS);
        wordFunctions.put("arctan", TokenType.ATAN);
        wordFunctions.put("arccot", TokenType.ACOT);
        wordFunctions.put("arcsec", TokenType.ASEC);
        wordFunctions.put("arccsc", TokenType.ACSC);

        wordFunctions.put("log10", TokenType.LOG10);

        wordFunctions.put("floor", TokenType.FLOOR);
        wordFunctions.put("round", TokenType.ROUND);

        wordFunctions.put("asinh", TokenType.ASINH);
        wordFunctions.put("acosh", TokenType.ACOSH);
        wordFunctions.put("atanh", TokenType.ATANH);
        wordFunctions.put("acoth", TokenType.ACOTH);
        wordFunctions.put("asech", TokenType.ASECH);
        wordFunctions.put("acsch", TokenType.ACSCH);

        wordFunctions.put("sinh", TokenType.SINH);
        wordFunctions.put("cosh", TokenType.COSH);
        wordFunctions.put("tanh", TokenType.TANH);
        wordFunctions.put("coth", TokenType.COTH);
        wordFunctions.put("sech", TokenType.SECH);
        wordFunctions.put("csch", TokenType.CSCH);

        wordFunctions.put("asin", TokenType.ASIN);
        wordFunctions.put("acos", TokenType.ACOS);
        wordFunctions.put("atan", TokenType.ATAN);
        wordFunctions.put("acot", TokenType.ACOT);
        wordFunctions.put("asec", TokenType.ASEC);
        wordFunctions.put("acsc", TokenType.ACSC);

        wordFunctions.put("sqrt", TokenType.SQRT);
        wordFunctions.put("ceil", TokenType.CEIL);
        wordFunctions.put("conj", TokenType.CONJ);

        wordFunctions.put("abs", TokenType.ABS);
        wordFunctions.put("exp", TokenType.EXP);

        wordFunctions.put("sin", TokenType.SIN);
        wordFunctions.put("cos", TokenType.COS);
        wordFunctions.put("tan", TokenType.TAN);
        wordFunctions.put("cot", TokenType.COT);
        wordFunctions.put("sec", TokenType.SEC);
        wordFunctions.put("csc", TokenType.CSC);

        wordFunctions.put("log", TokenType.LOG);
        wordFunctions.put("ln", TokenType.LOG);
    }

    static Map<String, TokenType> multipleArguments = new LinkedHashMap<>(
        Map.of("atan2", TokenType.ATAN2, "hypot", TokenType.HYPOT, "log", TokenType.LOG, "root", TokenType.ROOT)
    );

    static Map<String, TokenType> complexOperations = new LinkedHashMap<>(Map.of("conj", TokenType.CONJ));

    record Token(TokenType type, double value) {}

    public static List<Token> tokenize(String s) {
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
                case '^' -> tokens.add(new Token(TokenType.POW, 4));
                case '(' -> tokens.add(new Token(TokenType.LPAR, 5));
                case ')' -> tokens.add(new Token(TokenType.RPAR, 5));
                case ',' -> tokens.add(new Token(TokenType.COMMA, 0));
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
                        for (String function : multipleArguments.keySet()) {
                            if (s.substring(i).startsWith(function)) {
                                tokens.add(new Token(multipleArguments.get(function), 3));
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

        List<TokenType> allowedToEnd = new ArrayList<>(
            List.of(
                TokenType.ADD,
                TokenType.DIV,
                TokenType.POW,
                TokenType.MULT,
                TokenType.SUB,
                TokenType.UN_SUB,
                TokenType.COMMA
            )
        );

        for (int i = 0; i < tokens.size() - 1; i++) {
            if (!allowedToEnd.contains(tokens.get(i).type) && !allowedToEnd.contains(tokens.get(i + 1).type)) {
                if (
                    wordFunctions.containsValue(tokens.get(i).type) ||
                    multipleArguments.containsValue(tokens.get(i).type) ||
                    TokenType.LPAR == tokens.get(i).type ||
                    TokenType.RPAR == tokens.get(i + 1).type
                ) continue;
                tokens.add(i + 1, new Token(TokenType.MULT, 2));
                i++;
            }
        }
        return tokens;
    }
}
