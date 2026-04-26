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
        FLOOR,
        CEIL,
        ROUND,
        SQRT,
        COMMA,
    }

    //INFO: order by longest first for correct parsing
    private static Map<String, TokenType> wordFunctions = new LinkedHashMap<>(
        Map.of(
            "floor",
            TokenType.FLOOR,
            "round",
            TokenType.ROUND,
            "ceil",
            TokenType.CEIL,
            "sqrt",
            TokenType.SQRT,
            "tan",
            TokenType.TAN,
            "sin",
            TokenType.SIN,
            "cos",
            TokenType.COS,
            "ln",
            TokenType.LN
        )
    );

    private static Map<String, TokenType> multipleArguments = new LinkedHashMap<>(Map.of("log", TokenType.LOG));

    private record Token(TokenType type, double value) {}

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
                case '^' -> tokens.add(new Token(TokenType.EXP, 4));
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
        return tokens;
    }

    String s;
    List<Token> tokens;
    int pos = 0;

    public Parser(String expression) {
        s = expression;
        tokens = tokenize(expression);
    }

    public double evaluate() {
        double result = power1();

        while (check(TokenType.ADD) || check(TokenType.SUB)) {
            TokenType op = consume().type;
            double right = power1();
            result = op == TokenType.ADD ? result + right : result - right;
        }
        return result;
    }

    //DOES: evaluate expressions recursively based on binding power
    private double power1() {
        double result = power2();

        while (check(TokenType.MULT) || check(TokenType.DIV)) {
            TokenType op = consume().type;
            double right = power2();
            result = op == TokenType.MULT ? result * right : result / right;
        }
        return result;
    }

    private double power2() {
        if (check(TokenType.UN_SUB)) {
            consume();
            return -power2();
        }
        return power3();
    }

    private double power3() {
        double result = power4();

        if (check(TokenType.EXP)) {
            consume();
            return Math.pow(result, power4());
        }
        return result;
    }

    private double power4() {
        if (check(TokenType.LPAR)) {
            consume();
            double result = evaluate();
            expect(TokenType.RPAR);
            return result;
        }
        if (check(TokenType.NUM)) {
            return consume().value;
        }

        for (TokenType token : multipleArguments.values()) {
            if (check(token)) {
                consume();
                expect(TokenType.LPAR);
                List<Double> args = new ArrayList<>();
                args.add(evaluate());
                while (check(TokenType.COMMA)) {
                    consume();
                    args.add(evaluate());
                }
                expect(TokenType.RPAR);
                if (args.size() < 2) throw new IllegalArgumentException(
                    "too few arguments provided for multi-valued function"
                );
                return switch (token) {
                    case LOG -> Math.log(args.get(1)) / Math.log(args.get(0));
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type);
                };
            }
        }

        for (TokenType token : wordFunctions.values()) {
            if (check(token)) {
                consume();
                boolean par = check(TokenType.LPAR);
                if (par) consume();
                double result = par ? evaluate() : power4();
                if (par) expect(TokenType.RPAR);
                return switch (token) {
                    case SIN -> Math.sin(result);
                    case COS -> Math.cos(result);
                    case LN -> Math.log(result);
                    default -> throw new IllegalArgumentException("unexpected word function: got " + peek().type());
                };
            }
        }
        throw new IllegalStateException("unexpected token: " + peek().type());
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private boolean check(TokenType t) {
        return pos < tokens.size() && tokens.get(pos).type == t;
    }

    private void expect(TokenType t) {
        if (!check(t)) throw new IllegalArgumentException("missing " + t);
        consume();
    }
}
