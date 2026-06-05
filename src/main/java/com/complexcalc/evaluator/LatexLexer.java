package com.complexcalc.evaluator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LatexLexer {

    public static enum LatexToken {
        ADD,
        MINUS,
        UMINUS,
        MULT,
        DIV,
        FRAC,
        POW,
        SUBS,
        VAR,
        LPAR,
        RPAR,
        LBRACE,
        RBRACE,
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
        ABS,
        ROOT,
        EXP,
        CONJ,
    }

    static final Map<String, LatexToken> wordFunctions = new LinkedHashMap<>();

    static {
        String[][] entries = {
            //INFO: order by longest first for correct parsing
            { "floor-pair", "FLOOR" },
            { "ceil-pair", "CEIL" },
            { "arcsinh", "ASINH" },
            { "arccosh", "ACOSH" },
            { "arctanh", "ATANH" },
            { "arccoth", "ACOTH" },
            { "arcsech", "ASECH" },
            { "arccsch", "ACSCH" },
            { "arcsin", "ASIN" },
            { "arccos", "ACOS" },
            { "arctan", "ATAN" },
            { "arccot", "ACOT" },
            { "arcsec", "ASEC" },
            { "arccsc", "ACSC" },
            { "asinh", "ASINH" },
            { "acosh", "ACOSH" },
            { "atanh", "ATANH" },
            { "acoth", "ACOTH" },
            { "asech", "ASECH" },
            { "acsch", "ACSCH" },
            { "sinh", "SINH" },
            { "cosh", "COSH" },
            { "tanh", "TANH" },
            { "coth", "COTH" },
            { "sech", "SECH" },
            { "csch", "CSCH" },
            { "asin", "ASIN" },
            { "acos", "ACOS" },
            { "atan", "ATAN" },
            { "acot", "ACOT" },
            { "asec", "ASEC" },
            { "acsc", "ACSC" },
            { "sqrt", "SQRT" },
            { "frac", "FRAC" },
            { "abs", "ABS" },
            { "exp", "EXP" },
            { "sin", "SIN" },
            { "cos", "COS" },
            { "tan", "TAN" },
            { "cot", "COT" },
            { "sec", "SEC" },
            { "csc", "CSC" },
            { "log", "LOG" },
            { "ln", "LOG" },
        };

        for (var e : entries) {
            wordFunctions.put(e[0], LatexToken.valueOf(e[1]));
        }
    }

    static Map<String, LatexToken> braceArguments = new LinkedHashMap<>(
        Map.of("atan2", LatexToken.ATAN2, "hypot", LatexToken.HYPOT, "root", LatexToken.ROOT)
    );

    static Map<String, LatexToken> complexOperations = new LinkedHashMap<>(Map.of("conj", LatexToken.CONJ));

    record Token(LatexToken type, double value) {}

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
                tokens.add(new Token(LatexToken.NUM, Double.parseDouble(s.substring(digitStart, i))));
                digitStart = -1;
            }

            switch (c) {
                case '+' -> tokens.add(new Token(LatexToken.ADD, 1));
                case '-' -> {
                    LatexToken lastToken;
                    if (i == 0) tokens.add(new Token(LatexToken.UMINUS, 3));
                    else {
                        lastToken = tokens.getLast().type;
                        if (
                            lastToken != LatexToken.NUM && lastToken != LatexToken.VAR && lastToken != LatexToken.RPAR
                        ) tokens.add(new Token(LatexToken.UMINUS, 3));
                        else tokens.add(new Token(LatexToken.MINUS, 1));
                    }
                }
                case '*' -> tokens.add(new Token(LatexToken.MULT, 2));
                case '/' -> tokens.add(new Token(LatexToken.DIV, 2));
                case '^' -> tokens.add(new Token(LatexToken.POW, 4));
                case '(' -> tokens.add(new Token(LatexToken.LPAR, 5));
                case ')' -> tokens.add(new Token(LatexToken.RPAR, 5));
                case '{' -> tokens.add(new Token(LatexToken.LBRACE, 5));
                case '}' -> tokens.add(new Token(LatexToken.RBRACE, 5));
                case '_' -> tokens.add(new Token(LatexToken.SUBS, 5));
                case '\\' -> {
                    for (String function : wordFunctions.keySet()) {
                        if (s.substring(i + 1).startsWith(function)) {
                            tokens.add(new Token(wordFunctions.get(function), 3));
                            i += function.length();
                            break;
                        }
                    }
                    for (String function : braceArguments.keySet()) {
                        if (s.substring(i).startsWith(function)) {
                            tokens.add(new Token(braceArguments.get(function), 3));
                            i += function.length() - 1;
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
                        if (!wordFound) tokens.add(new Token(LatexToken.VAR, c));
                    } else throw new IllegalArgumentException();
                }
            }
        }
        if (digitStart != -1) tokens.add(new Token(LatexToken.NUM, Double.parseDouble(s.substring(digitStart))));

        List<LatexToken> allowedToEnd = new ArrayList<>(
            List.of(
                LatexToken.ADD,
                LatexToken.DIV,
                LatexToken.POW,
                LatexToken.MULT,
                LatexToken.MINUS,
                LatexToken.UMINUS
            )
        );

        //DOES: insert mult tokens for implied multiplication
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (!allowedToEnd.contains(tokens.get(i).type) && !allowedToEnd.contains(tokens.get(i + 1).type)) {
                if (
                    wordFunctions.containsValue(tokens.get(i).type) ||
                    braceArguments.containsValue(tokens.get(i).type) ||
                    LatexToken.LPAR == tokens.get(i).type ||
                    LatexToken.RPAR == tokens.get(i + 1).type ||
                    LatexToken.LBRACE == tokens.get(i).type ||
                    LatexToken.LBRACE == tokens.get(i + 1).type ||
                    LatexToken.RBRACE == tokens.get(i + 1).type ||
                    (LatexToken.RBRACE == tokens.get(i).type && LatexToken.LBRACE == tokens.get(i + 1).type)
                ) continue;
                tokens.add(i + 1, new Token(LatexToken.MULT, 2));
                i++;
            }
        }
        return tokens;
    }
}
