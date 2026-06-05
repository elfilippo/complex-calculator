package com.complexcalc.evaluator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaintextLexer {

    public static enum PlainToken {
        ADD,
        MINUS,
        UMINUS,
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

    static final Map<String, PlainToken> wordFunctions = new LinkedHashMap<>();

    static {
        //INFO: order by longest first for correct parsing
        wordFunctions.put("arcsinh", PlainToken.ASINH);
        wordFunctions.put("arccosh", PlainToken.ACOSH);
        wordFunctions.put("arctanh", PlainToken.ATANH);
        wordFunctions.put("arccoth", PlainToken.ACOTH);
        wordFunctions.put("arcsech", PlainToken.ASECH);
        wordFunctions.put("arccsch", PlainToken.ACSCH);

        wordFunctions.put("arcsin", PlainToken.ASIN);
        wordFunctions.put("arccos", PlainToken.ACOS);
        wordFunctions.put("arctan", PlainToken.ATAN);
        wordFunctions.put("arccot", PlainToken.ACOT);
        wordFunctions.put("arcsec", PlainToken.ASEC);
        wordFunctions.put("arccsc", PlainToken.ACSC);

        wordFunctions.put("log10", PlainToken.LOG10);

        wordFunctions.put("floor", PlainToken.FLOOR);
        wordFunctions.put("round", PlainToken.ROUND);

        wordFunctions.put("asinh", PlainToken.ASINH);
        wordFunctions.put("acosh", PlainToken.ACOSH);
        wordFunctions.put("atanh", PlainToken.ATANH);
        wordFunctions.put("acoth", PlainToken.ACOTH);
        wordFunctions.put("asech", PlainToken.ASECH);
        wordFunctions.put("acsch", PlainToken.ACSCH);

        wordFunctions.put("sinh", PlainToken.SINH);
        wordFunctions.put("cosh", PlainToken.COSH);
        wordFunctions.put("tanh", PlainToken.TANH);
        wordFunctions.put("coth", PlainToken.COTH);
        wordFunctions.put("sech", PlainToken.SECH);
        wordFunctions.put("csch", PlainToken.CSCH);

        wordFunctions.put("asin", PlainToken.ASIN);
        wordFunctions.put("acos", PlainToken.ACOS);
        wordFunctions.put("atan", PlainToken.ATAN);
        wordFunctions.put("acot", PlainToken.ACOT);
        wordFunctions.put("asec", PlainToken.ASEC);
        wordFunctions.put("acsc", PlainToken.ACSC);

        wordFunctions.put("sqrt", PlainToken.SQRT);
        wordFunctions.put("ceil", PlainToken.CEIL);
        wordFunctions.put("conj", PlainToken.CONJ);

        wordFunctions.put("abs", PlainToken.ABS);
        wordFunctions.put("exp", PlainToken.EXP);

        wordFunctions.put("sin", PlainToken.SIN);
        wordFunctions.put("cos", PlainToken.COS);
        wordFunctions.put("tan", PlainToken.TAN);
        wordFunctions.put("cot", PlainToken.COT);
        wordFunctions.put("sec", PlainToken.SEC);
        wordFunctions.put("csc", PlainToken.CSC);

        wordFunctions.put("log", PlainToken.LOG);
        wordFunctions.put("ln", PlainToken.LOG);
    }

    static Map<String, PlainToken> multipleArguments = new LinkedHashMap<>(
        Map.of("atan2", PlainToken.ATAN2, "hypot", PlainToken.HYPOT, "log", PlainToken.LOG, "root", PlainToken.ROOT)
    );

    static Map<String, PlainToken> complexOperations = new LinkedHashMap<>(Map.of("conj", PlainToken.CONJ));

    record Token(PlainToken type, double value) {}

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
                tokens.add(new Token(PlainToken.NUM, Double.parseDouble(s.substring(digitStart, i))));
                digitStart = -1;
            }

            switch (c) {
                case '+' -> tokens.add(new Token(PlainToken.ADD, 1));
                case '-' -> {
                    PlainToken lastToken;
                    if (i == 0) tokens.add(new Token(PlainToken.UMINUS, 3));
                    else {
                        lastToken = tokens.getLast().type;
                        if (
                            lastToken != PlainToken.NUM && lastToken != PlainToken.VAR && lastToken != PlainToken.RPAR
                        ) tokens.add(new Token(PlainToken.UMINUS, 3));
                        else tokens.add(new Token(PlainToken.MINUS, 1));
                    }
                }
                case '*' -> tokens.add(new Token(PlainToken.MULT, 2));
                case '/' -> tokens.add(new Token(PlainToken.DIV, 2));
                case '^' -> tokens.add(new Token(PlainToken.POW, 4));
                case '(' -> tokens.add(new Token(PlainToken.LPAR, 5));
                case ')' -> tokens.add(new Token(PlainToken.RPAR, 5));
                case ',' -> tokens.add(new Token(PlainToken.COMMA, 0));
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
                        if (!wordFound) tokens.add(new Token(PlainToken.VAR, c));
                    } else throw new IllegalArgumentException();
                }
            }
        }
        if (digitStart != -1) tokens.add(new Token(PlainToken.NUM, Double.parseDouble(s.substring(digitStart))));

        List<PlainToken> allowedToEnd = new ArrayList<>(
            List.of(
                PlainToken.ADD,
                PlainToken.DIV,
                PlainToken.POW,
                PlainToken.MULT,
                PlainToken.MINUS,
                PlainToken.UMINUS,
                PlainToken.COMMA
            )
        );

        for (int i = 0; i < tokens.size() - 1; i++) {
            if (!allowedToEnd.contains(tokens.get(i).type) && !allowedToEnd.contains(tokens.get(i + 1).type)) {
                if (
                    wordFunctions.containsValue(tokens.get(i).type) ||
                    multipleArguments.containsValue(tokens.get(i).type) ||
                    PlainToken.LPAR == tokens.get(i).type ||
                    PlainToken.RPAR == tokens.get(i + 1).type
                ) continue;
                tokens.add(i + 1, new Token(PlainToken.MULT, 2));
                i++;
            }
        }
        return tokens;
    }
}
