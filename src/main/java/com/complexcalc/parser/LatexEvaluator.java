package com.complexcalc.parser;

import static com.complexcalc.parser.registry.Functions.*;

import com.complexcalc.parser.registry.Functions.ValueToken;
import com.complexcalc.parser.registry.Token;
import java.util.List;

public class LatexEvaluator {

    private List<ValueToken> tokens;
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

        for (Token token : multipleArgOperations.values()) {
            if (check(token)) {
                consume();
                expect(Token.LBRACE);
                double arg1 = depth1();
                double arg2 = 0;
                boolean hasArg2 = false;
                if (check(Token.LBRACE)) {
                    consume();
                    arg2 = depth1();
                    expect(Token.RBRACE);
                    hasArg2 = true;
                }

                return switch (token) {
                    case LOG -> {
                        if (!hasArg2) yield Math.log(arg1);
                        yield Math.log(arg2) / Math.log(arg1);
                    }
                    case ATAN2 -> {
                        if (!hasArg2) throw new IllegalArgumentException("missing second argument for atan2");
                        yield Math.atan2(arg1, arg2);
                    }
                    case HYPOT -> {
                        if (!hasArg2) throw new IllegalArgumentException("missing second argument for hypot");
                        yield Math.hypot(arg1, arg2);
                    }
                    case ROOT -> {
                        if (!hasArg2) yield Math.sqrt(arg1);
                        yield Math.pow(arg2, 1 / arg1);
                    }
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type());
                };
            }
        }

        for (Token token : wordOperations.values()) {
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
                        if (complexOperations.containsValue(token)) throw new IllegalArgumentException(
                            "encountered complex operation evaluating for real numbers"
                        );
                        else throw new IllegalArgumentException("unexpected word function: got " + peek().type());
                    }
                };
            }
        }
        throw new IllegalStateException("unexpected token: " + peek().type());
    }

    private ValueToken peek() {
        return tokens.get(pos);
    }

    private ValueToken consume() {
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
