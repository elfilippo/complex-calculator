package com.complexcalc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private static enum TokenType {
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
        COS,
        COSH,
        ACOS,
        TAN,
        TANH,
        ATAN,
        ATAN2,
        HYPOT,
        LN,
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
    }

    private static final Map<String, TokenType> wordFunctions = new LinkedHashMap<>();

    static {
        //INFO: order by longest first for correct parsing
        wordFunctions.put("log10", TokenType.LOG10);
        wordFunctions.put("floor", TokenType.FLOOR);
        wordFunctions.put("round", TokenType.ROUND);
        wordFunctions.put("sinh", TokenType.SINH);
        wordFunctions.put("cosh", TokenType.COSH);
        wordFunctions.put("tanh", TokenType.TANH);
        wordFunctions.put("asin", TokenType.ASIN);
        wordFunctions.put("acos", TokenType.ACOS);
        wordFunctions.put("atan", TokenType.ATAN);
        wordFunctions.put("sqrt", TokenType.SQRT);
        wordFunctions.put("ceil", TokenType.CEIL);
        wordFunctions.put("abs", TokenType.ABS);
        wordFunctions.put("exp", TokenType.EXP);
        wordFunctions.put("sin", TokenType.SIN);
        wordFunctions.put("cos", TokenType.COS);
        wordFunctions.put("tan", TokenType.TAN);
        wordFunctions.put("ln", TokenType.LN);
    }

    private char var1;
    private double var1val;
    private char var2;
    private double var2val;
    private char var3;
    private double var3val;
    private char var4;
    private double var4val;

    private static Map<String, TokenType> multipleArguments = new LinkedHashMap<>(
        Map.of("atan2", TokenType.ATAN2, "hypot", TokenType.HYPOT, "log", TokenType.LOG, "root", TokenType.ROOT)
    );

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

    private List<Token> tokens;
    private int pos = 0;

    public Parser(String expression) {
        tokens = tokenize(expression);
    }

    public double eval(
        char var1,
        double var1val,
        char var2,
        double var2val,
        char var3,
        double var3val,
        char var4,
        double var4val
    ) {
        this.var1 = var1;
        this.var1val = var1val;
        this.var2 = var2;
        this.var2val = var2val;
        this.var3 = var3;
        this.var3val = var3val;
        this.var4 = var4;
        this.var4val = var4val;

        return depth1();
    }

    public double eval() {
        return eval((char) 0, 0, (char) 0, 0, (char) 0, 0, (char) 0, 0);
    }

    public double eval(char var1, double var1val) {
        return eval(var1, var1val, (char) 0, 0, (char) 0, 0, (char) 0, 0);
    }

    public double eval(char var1, double var1val, char var2, double var2val) {
        return eval(var1, var1val, var2, var2val, (char) 0, 0, (char) 0, 0);
    }

    public double eval(char var1, double var1val, char var2, double var2val, char var3, double var3val) {
        return eval(var1, var1val, var2, var2val, var3, var3val, (char) 0, 0);
    }

    private double depth1() {
        //DOES: evaluate expressions recursively based on binding power
        double result = depth2();

        while (check(TokenType.ADD) || check(TokenType.SUB)) {
            TokenType op = consume().type;
            double right = depth2();
            result = op == TokenType.ADD ? result + right : result - right;
        }
        return result;
    }

    private double depth2() {
        double result = depth3();

        while (check(TokenType.MULT) || check(TokenType.DIV)) {
            TokenType op = consume().type;
            double right = depth3();
            result = op == TokenType.MULT ? result * right : result / right;
        }
        return result;
    }

    private double depth3() {
        if (check(TokenType.UN_SUB)) {
            consume();
            return -depth3();
        }
        return depth4();
    }

    private double depth4() {
        double result = depth5();

        if (check(TokenType.POW)) {
            consume();
            return Math.pow(result, depth5());
        }
        return result;
    }

    private double depth5() {
        if (check(TokenType.LPAR)) {
            consume();
            double result = depth1();
            expect(TokenType.RPAR);
            return result;
        }

        if (check(TokenType.NUM)) {
            return consume().value;
        }

        if (check(TokenType.VAR)) {
            if (peek().value == 'e') {
                consume();
                return Math.E;
            }
            if (peek().value == 'π') {
                consume();
                return Math.PI;
            }
            if (peek().value == 'τ') {
                consume();
                return Math.TAU;
            }
            if (peek().value == var1) {
                consume();
                return var1val;
            }
            if (peek().value == var2) {
                consume();
                return var2val;
            }
            if (peek().value == var3) {
                consume();
                return var3val;
            }
            if (peek().value == var4) {
                consume();
                return var4val;
            }
            throw new IllegalArgumentException("unknown variable: " + (char) peek().value);
        }

        for (TokenType token : multipleArguments.values()) {
            if (check(token)) {
                consume();
                expect(TokenType.LPAR);
                List<Double> args = new ArrayList<>();
                args.add(depth1());
                while (check(TokenType.COMMA)) {
                    consume();
                    args.add(depth1());
                }
                expect(TokenType.RPAR);

                return switch (token) {
                    case LOG -> {
                        if (args.size() == 1) yield Math.log(args.get(0));
                        argException(args.size(), 2, 2);
                        yield Math.log(args.get(1)) / Math.log(args.get(0));
                    }
                    case ATAN2 -> {
                        argException(args.size(), 2, 2);
                        yield Math.atan2(args.get(0), args.get(1));
                    }
                    case HYPOT -> {
                        argException(args.size(), 2, 2);
                        yield Math.hypot(args.get(0), args.get(1));
                    }
                    case ROOT -> {
                        argException(args.size(), 1, 2);
                        if (args.size() == 1) yield Math.sqrt(args.get(0));
                        yield Math.pow(args.get(1), 1 / args.get(0));
                    }
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type);
                };
            }
        }

        for (TokenType token : wordFunctions.values()) {
            if (check(token)) {
                consume();
                boolean par = check(TokenType.LPAR);
                if (par) consume();
                double result = par ? depth1() : depth5();
                if (par) expect(TokenType.RPAR);
                return switch (token) {
                    case LOG10 -> Math.log10(result);
                    case FLOOR -> Math.floor(result);
                    case ROUND -> Math.round(result);
                    case SINH -> Math.sinh(result);
                    case COSH -> Math.cosh(result);
                    case TANH -> Math.tanh(result);
                    case ASIN -> Math.asin(result);
                    case ACOS -> Math.acos(result);
                    case ATAN -> Math.atan(result);
                    case SQRT -> Math.sqrt(result);
                    case CEIL -> Math.ceil(result);
                    case ABS -> Math.abs(result);
                    case EXP -> Math.exp(result);
                    case SIN -> Math.sin(result);
                    case COS -> Math.cos(result);
                    case LN -> Math.log(result);
                    default -> throw new IllegalArgumentException("unexpected word function: got " + peek().type());
                };
            }
        }
        throw new IllegalStateException("unexpected token: " + peek().type());
    }

    /**
     * throws exceptions if there are too many or too few arguments
     * @param amount the argument amount
     * @param min the minimum value to not throw an exception
     * @param max the max value to not throw an exception
     */
    private static void argException(int amount, int min, int max) {
        if (amount < min) throw new IllegalArgumentException("too few arguments provided");
        if (amount > max) throw new IllegalArgumentException("too many arguments provided");
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
