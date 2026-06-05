package com.complexcalc.evaluator;

import com.complexcalc.evaluator.LatexLexer.LatexToken;
import com.complexcalc.evaluator.LatexLexer.Token;
import java.util.ArrayList;
import java.util.List;

public class LatexComplexEvaluator {

    private List<Token> tokens;
    private int pos = 0;

    private char var1;
    private FastComplex var1val;
    private char var2;
    private FastComplex var2val;
    private char var3;
    private FastComplex var3val;
    private char var4;
    private FastComplex var4val;

    public LatexComplexEvaluator(String expression) {
        tokens = LatexLexer.tokenize(expression);
    }

    public FastComplex eval(
        char var1,
        FastComplex var1val,
        char var2,
        FastComplex var2val,
        char var3,
        FastComplex var3val,
        char var4,
        FastComplex var4val
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

    public FastComplex eval() {
        return eval((char) 0, null, (char) 0, null, (char) 0, null, (char) 0, null);
    }

    public FastComplex eval(char var1, FastComplex var1val) {
        return eval(var1, var1val, (char) 0, null, (char) 0, null, (char) 0, null);
    }

    public FastComplex eval(char var1, FastComplex var1val, char var2, FastComplex var2val) {
        return eval(var1, var1val, var2, var2val, (char) 0, null, (char) 0, null);
    }

    public FastComplex eval(
        char var1,
        FastComplex var1val,
        char var2,
        FastComplex var2val,
        char var3,
        FastComplex var3val
    ) {
        return eval(var1, var1val, var2, var2val, var3, var3val, (char) 0, null);
    }

    private FastComplex depth1() {
        //DOES: evaluate expressions recursively based on binding power
        FastComplex result = depth2();

        while (check(LatexToken.ADD) || check(LatexToken.MINUS)) {
            LatexToken op = consume().type();
            FastComplex right = depth2();
            result = op == LatexToken.ADD ? FastComplex.add(result, right) : FastComplex.sub(result, right);
        }
        return result;
    }

    private FastComplex depth2() {
        FastComplex result = depth3();

        while (check(LatexToken.MULT) || check(LatexToken.DIV)) {
            LatexToken op = consume().type();
            FastComplex right = depth3();
            result = op == LatexToken.MULT ? FastComplex.mult(result, right) : FastComplex.div(result, right);
        }
        return result;
    }

    private FastComplex depth3() {
        if (check(LatexToken.UMINUS)) {
            consume();
            return FastComplex.invert(depth3());
        }
        return depth4();
    }

    private FastComplex depth4() {
        FastComplex result = depth5();

        if (check(LatexToken.POW)) {
            consume();
            return FastComplex.pow(result, depth3());
        }
        return result;
    }

    private FastComplex depth5() {
        if (check(LatexToken.LPAR)) {
            consume();
            FastComplex result = depth1();
            expect(LatexToken.RPAR);
            return result;
        }

        if (check(LatexToken.LBRACE)) {
            consume();
            FastComplex result = depth1();
            expect(LatexToken.RBRACE);
            return result;
        }

        if (check(LatexToken.NUM)) {
            return new FastComplex(consume().value(), 0);
        }

        if (check(LatexToken.VAR)) {
            if (peek().value() == 'i') {
                consume();
                return new FastComplex(0, 1);
            }
            if (peek().value() == 'e') {
                consume();
                return new FastComplex(Math.E, 0);
            }
            if (peek().value() == 'π') {
                consume();
                return new FastComplex(Math.PI, 0);
            }
            if (peek().value() == 'τ') {
                consume();
                return new FastComplex(Math.TAU, 0);
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

        for (LatexToken token : LatexLexer.braceArguments.values()) {
            if (check(token)) {
                consume();
                expect(LatexToken.LBRACE);
                List<FastComplex> args = new ArrayList<>();
                args.add(depth1());
                while (check(LatexToken.LBRACE)) {
                    consume();
                    args.add(depth1());
                }
                expect(LatexToken.RBRACE);

                return switch (token) {
                    case HYPOT -> {
                        argException(args.size(), 2, 2);
                        yield FastComplex.sqrt(
                            FastComplex.add(FastComplex.sqr(args.get(0)), FastComplex.sqr(args.get(1)))
                        );
                    }
                    case ROOT -> {
                        argException(args.size(), 1, 2);
                        if (args.size() == 1) yield FastComplex.sqrt(args.get(0));
                        yield FastComplex.nRoot(args.get(0), args.get(1));
                    }
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type());
                };
            }
        }

        if (check(LatexToken.LOG)) {
            consume();
            FastComplex base = null;
            if (check(LatexToken.SUBS)) {
                expect(LatexToken.LBRACE);
                base = depth1();
                expect(LatexToken.RBRACE);
            }
            expect(LatexToken.LBRACE);
            FastComplex antiLog = depth1();
            expect(LatexToken.RBRACE);
            return base == null
                ? FastComplex.log(antiLog)
                : FastComplex.div(FastComplex.log(antiLog), FastComplex.log(base));
        }

        for (LatexToken token : LatexLexer.wordFunctions.values()) {
            if (check(token)) {
                consume();
                boolean par = check(LatexToken.LPAR);
                if (par) consume();
                FastComplex result = par ? depth1() : depth5();
                if (par) expect(LatexToken.RPAR);
                return switch (token) {
                    case LOG10 -> FastComplex.log10(result);
                    case FLOOR -> FastComplex.floor(result);
                    case CEIL -> FastComplex.ceil(result);
                    case ROUND -> FastComplex.round(result);
                    case SINH -> FastComplex.sinh(result);
                    case COSH -> FastComplex.cosh(result);
                    case TANH -> FastComplex.tanh(result);
                    case COTH -> FastComplex.coth(result);
                    case SECH -> FastComplex.sech(result);
                    case CSCH -> FastComplex.csch(result);
                    case ASIN -> FastComplex.asin(result);
                    case ACOS -> FastComplex.acos(result);
                    case ACOT -> FastComplex.acot(result);
                    case ASEC -> FastComplex.asec(result);
                    case ACSC -> FastComplex.acsc(result);
                    case ATAN -> FastComplex.atan(result);
                    case ASINH -> FastComplex.asinh(result);
                    case ACOSH -> FastComplex.acosh(result);
                    case ATANH -> FastComplex.atanh(result);
                    case ACOTH -> FastComplex.acoth(result);
                    case ASECH -> FastComplex.asech(result);
                    case ACSCH -> FastComplex.acsch(result);
                    case CONJ -> FastComplex.conjugate(result);
                    case SQRT -> FastComplex.sqrt(result);
                    case ABS -> new FastComplex(result.mag(), 0);
                    case EXP -> FastComplex.exp(result);
                    case SIN -> FastComplex.sin(result);
                    case COS -> FastComplex.cos(result);
                    case TAN -> FastComplex.tan(result);
                    case COT -> FastComplex.cot(result);
                    case SEC -> FastComplex.sec(result);
                    case CSC -> FastComplex.csc(result);
                    case LOG -> FastComplex.log(result);
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

    private boolean check(LatexToken t) {
        return pos < tokens.size() && tokens.get(pos).type() == t;
    }

    private void expect(LatexToken t) {
        if (!check(t)) throw new IllegalArgumentException("missing " + t);
        consume();
    }
}
