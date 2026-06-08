package com.complexcalc.evaluator;

import com.complexcalc.evaluator.LatexLexer.valueToken;
import java.util.List;

public class LatexEvaluator {

    private List<valueToken> tokens;
    private int pos = 0;

    private char var1;
    private double var1val;
    private char var2;
    private double var2val;
    private char var3;
    private double var3val;
    private char var4;
    private double var4val;

    public LatexEvaluator(String expression) {
        tokens = LatexLexer.tokenize(expression);
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

        pos = 0;

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

        while (check(Token.ADD) || check(Token.MINUS)) {
            Token op = consume().type();
            double right = depth2();
            result = op == Token.ADD ? result + right : result - right;
        }
        return result;
    }

    private double depth2() {
        double result = depth3();

        while (check(Token.MULT) || check(Token.DIV)) {
            Token op = consume().type();
            double right = depth3();
            result = op == Token.MULT ? result * right : result / right;
        }
        return result;
    }

    private double depth3() {
        if (check(Token.UMINUS)) {
            consume();
            return -depth3();
        }
        return depth4();
    }

    private double depth4() {
        double result = depth5();

        if (check(Token.POW)) {
            consume();
            return Math.pow(result, depth5());
        }
        return result;
    }

    private double depth5() {
        if (check(Token.LPAR)) {
            consume();
            double result = depth1();
            expect(Token.RPAR);
            return result;
        }

        if (check(Token.NUM)) {
            return consume().value();
        }

        if (check(Token.VAR)) {
            if (peek().value() == 'e') {
                consume();
                return Math.E;
            }
            if (peek().value() == 'π') {
                consume();
                return Math.PI;
            }
            if (peek().value() == 'τ') {
                consume();
                return Math.TAU;
            }
            if (peek().value() == var1) {
                consume();
                return var1val;
            }
            if (peek().value() == var2) {
                consume();
                return var2val;
            }
            if (peek().value() == var3) {
                consume();
                return var3val;
            }
            if (peek().value() == var4) {
                consume();
                return var4val;
            }
            throw new IllegalArgumentException("unknown variable: " + (char) peek().value());
        }

        /*
        for (Token token : LatexLexer.multipleArguments.values()) {
            if (check(token)) {
                consume();
                expect(Token.LPAR);
                List<Double> args = new ArrayList<>();
                args.add(depth1());
                while (check(Token.COMMA)) {
                    consume();
                    args.add(depth1());
                }
                expect(Token.RPAR);

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
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type());
                };
            }
        }
        */

        for (Token token : LatexLexer.wordFunctions.values()) {
            if (check(token)) {
                consume();
                boolean par = check(Token.LPAR);
                if (par) consume();
                double result = par ? depth1() : depth5();
                if (par) expect(Token.RPAR);
                return switch (token) {
                    case FLOOR -> Math.floor(result);
                    case CEIL -> Math.ceil(result);
                    case ROUND -> Math.round(result);
                    case SINH -> Math.sinh(result);
                    case COSH -> Math.cosh(result);
                    case TANH -> Math.tanh(result);
                    case ASIN -> Math.asin(result);
                    case ACOS -> Math.acos(result);
                    case ATAN -> Math.atan(result);
                    case ABS -> Math.abs(result);
                    case EXP -> Math.exp(result);
                    case SIN -> Math.sin(result);
                    case COS -> Math.cos(result);
                    case LOG -> Math.log(result);
                    default -> {
                        if (LatexLexer.complexOperations.containsValue(token)) throw new IllegalArgumentException(
                            "encountered complex operation evaluating for real numbers"
                        );
                        else throw new IllegalArgumentException("unexpected word function: got " + peek().type());
                    }
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

    private valueToken peek() {
        return tokens.get(pos);
    }

    private valueToken consume() {
        return tokens.get(pos++);
    }

    private boolean check(Token t) {
        return pos < tokens.size() && tokens.get(pos).type() == t;
    }

    private void expect(Token t) {
        if (!check(t)) throw new IllegalArgumentException("missing " + t);
        consume();
    }
}
