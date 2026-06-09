package com.complexcalc.parser;

import static com.complexcalc.parser.registry.Functions.*;

import com.complexcalc.parser.registry.Token;
import java.util.Arrays;
import java.util.List;

/**
 * complex evaluator class for LaTeX input
 * supports strings or lists of LaTeX tokens as input
 */
public class LatexComplexEvaluator {

    //IS: list of tokens to be evaluated
    private List<valueToken> tokens;

    //IS: current position
    private int pos = 0;

    //IS: array of variable - value pairs
    //INFO: initialized to length 0 if no variables are provided
    private varValue[] variables;

    /**
     * constructor that takes a String and automatically tokenizes it
     * @param expression the String to be evaluated. must be in correct LaTeX.
     */
    public LatexComplexEvaluator(String expression) {
        tokens = LatexLexer.tokenize(expression);
    }

    /**
     * constructor that takes a list of valueTokens directly
     * @param tokens list of valueTokens (token-value pairs). must be correctly tokenized LaTeX.
     */
    public LatexComplexEvaluator(List<valueToken> tokens) {
        this.tokens = tokens;
    }

    /**
     * record of variable-value pairs
     * @param var the char of the variable
     * @param val the value of the variable
     */
    public record varValue(char var, FastComplex val) {}

    /**
     * evaluates the expression of the current object. takes an array of variable-value pairs
     * @param variables array of varValue records
     * @return FastComplex result
     */
    public FastComplex eval(varValue[] variables) {
        this.variables = variables;

        pos = 0;

        return depth1();
    }

    /**
     * overloaded constructor <p>
     * evaluates the expression of the current object. doesn't take any variables
     * @return FastComplex result
     */
    public FastComplex eval() {
        return eval(new varValue[0]);
    }

    /**
     * overloaded constructor for a complex variable value <p>
     * evaluates the expression of the current object. takes one variable
     * @param var1 the char of the variable
     * @param var1val the FastComplex value of the variable
     * @return FastComplex result
     */
    public FastComplex eval(char var1, FastComplex var1val) {
        return eval(new varValue[] { new varValue(var1, var1val) });
    }

    /**
     * overloaded constructor for complex variable values <p>
     * evaluates the expression of the current object. takes two variables
     * @param var1 the char of the first variable
     * @param var1val the FastComplex value of the first variable
     * @param var2 the char of the second variable
     * @param var2val the FastComplex value of the second variable
     * @return FastComplex result
     */
    public FastComplex eval(char var1, FastComplex var1val, char var2, FastComplex var2val) {
        return eval(new varValue[] { new varValue(var1, var1val), new varValue(var2, var2val) });
    }

    /**
     * overloaded constructor for complex variable values <p>
     * evaluates the expression of the current object. takes three variables
     * @param var1 the char of the first variable
     * @param var1val the FastComplex value of the first variable
     * @param var2 the char of the second variable
     * @param var2val the FastComplex value of the second variable
     * @param var3 the char of the third variable
     * @param var3val the FastComplex value of the third variable
     * @return FastComplex result
     */
    public FastComplex eval(
        char var1,
        FastComplex var1val,
        char var2,
        FastComplex var2val,
        char var3,
        FastComplex var3val
    ) {
        return eval(
            new varValue[] { new varValue(var1, var1val), new varValue(var2, var2val), new varValue(var3, var3val) }
        );
    }

    /**
     * overloaded constructor for complex variable values <p>
     * evaluates the expression of the current object. takes four variables
     * @param var1 the char of the first variable
     * @param var1val the FastComplex value of the first variable
     * @param var2 the char of the second variable
     * @param var2val the FastComplex value of the second variable
     * @param var3 the char of the third variable
     * @param var3val the FastComplex value of the third variable
     * @param var4 the char of the fourth variable
     * @param var4val the FastComplex value of the fourth variable
     * @return FastComplex result
     */
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
        return eval(
            new varValue[] {
                new varValue(var1, var1val),
                new varValue(var2, var2val),
                new varValue(var3, var3val),
                new varValue(var4, var4val),
            }
        );
    }

    /**
     * overloaded constructor for a real variable value <p>
     * evaluates the expression of the current object. takes one variable
     * @param var1 the char of the variable
     * @param var1val the real value of the variable
     * @return FastComplex result
     */
    public FastComplex eval(char var1, double var1val) {
        return eval(new varValue[] { new varValue(var1, new FastComplex(var1val, 0)) });
    }

    /**
     * overloaded constructor for real variable values <p>
     * evaluates the expression of the current object. takes two variables
     * @param var1 the char of the first variable
     * @param var1val the real value of the second variable
     * @param var2 the char of the second variable
     * @param var2val the real value of the second variable
     * @return FastComplex result
     */
    public FastComplex eval(char var1, double var1val, char var2, double var2val) {
        return eval(
            new varValue[] {
                new varValue(var1, new FastComplex(var1val, 0)),
                new varValue(var2, new FastComplex(var2val, 0)),
            }
        );
    }

    /**
     * overloaded constructor for real variable values <p>
     * evaluates the expression of the current object. takes three variables
     * @param var1 the char of the first variable
     * @param var1val the real value of the second variable
     * @param var2 the char of the second variable
     * @param var2val the real value of the second variable
     * @param var3 the char of the third variable
     * @param var3val the real value of the third variable
     * @return FastComplex result
     */
    public FastComplex eval(char var1, double var1val, char var2, double var2val, char var3, double var3val) {
        return eval(
            new varValue[] {
                new varValue(var1, new FastComplex(var1val, 0)),
                new varValue(var2, new FastComplex(var2val, 0)),
                new varValue(var3, new FastComplex(var3val, 0)),
            }
        );
    }

    /**
     * overloaded constructor for real variable values <p>
     * evaluates the expression of the current object. takes four variables
     * @param var1 the char of the first variable
     * @param var1val the real value of the second variable
     * @param var2 the char of the second variable
     * @param var2val the real value of the second variable
     * @param var3 the char of the third variable
     * @param var3val the real value of the third variable
     * @param var4 the char of the fourth variable
     * @param var4val the real value of the fourth variable
     * @return FastComplex result
     */
    public FastComplex eval(
        char var1,
        double var1val,
        char var2,
        double var2val,
        char var3,
        double var3val,
        char var4,
        double var4val
    ) {
        return eval(
            new varValue[] {
                new varValue(var1, new FastComplex(var1val, 0)),
                new varValue(var2, new FastComplex(var2val, 0)),
                new varValue(var3, new FastComplex(var3val, 0)),
                new varValue(var4, new FastComplex(var4val, 0)),
            }
        );
    }

    /**
     * first depth of parsing and evaluating algorithm. each depth calls the one below it recursively at the very
     * beginning and to get the right side of the operation if it needs one. this makes the expression cascade up
     * from the deepest method if it can't be evaluated there until it can, incorporating binding power
     * (or PEMDAS). depth 1 handles addition and subtraction (the operations with the lowest binding power) and
     * only returns when the whole expression is done due to being the most shallow depth
     * @return FastComplex result of whole expression
     */
    private FastComplex depth1() {
        //IS: the left-hand side of the expression, which inherently becomes the full result once all operations
        //IS: have been performed on it
        FastComplex result = depth2();

        while (check(Token.ADD) || check(Token.MINUS)) {
            Token op = consume().type();
            FastComplex right = depth2();
            result = op == Token.ADD ? FastComplex.add(result, right) : FastComplex.sub(result, right);
        }

        return result;
    }

    /**
     * second depth of parsing algorithm. handles operations with a binding power of two, like multiplication and
     * division
     * @return
     */
    private FastComplex depth2() {
        FastComplex result = depth3();

        while (check(Token.MULT) || check(Token.DIV)) {
            Token op = consume().type();
            FastComplex right = depth3();
            result = op == Token.MULT ? FastComplex.mult(result, right) : FastComplex.div(result, right);
        }

        return result;
    }

    /**
     * third depth of parsing algorithm. handles the unary minus and only calls the further depth after that,
     * since the unary minus doesn't require a left-hand side (the result variable), but only a right-hand side
     * @return
     */
    private FastComplex depth3() {
        if (check(Token.UMINUS)) {
            consume();
            return FastComplex.negate(depth3());
        }

        return depth4();
    }

    /**
     * fourth depth of parsing algorithm. handles exponentiation
     * @return
     */
    private FastComplex depth4() {
        FastComplex result = depth5();

        if (check(Token.POW)) {
            consume();
            return FastComplex.pow(result, depth3());
        }

        if (check(Token.FACT)) {
            consume();
            return FastComplex.factorial(result);
        }

        return result;
    }

    /**
     * last depth of parsing algorithm. handles numbers, variables, braces, parentheses, and word functions such
     * as ln or exp, since the latter require braces or parentheses, therefore enforcing correct binding power
     * automatically
     * @return
     */
    private FastComplex depth5() {
        //DOES: evaluate parentheses
        if (check(Token.LPAR)) {
            consume();
            FastComplex result = depth1();
            expect(Token.RPAR);
            return result;
        }

        //DOES: evaluate braces
        if (check(Token.LBRACE)) {
            consume();
            FastComplex result = depth1();
            expect(Token.RBRACE);
            return result;
        }

        //DOES: evaluate numbers (return the value of the number token)
        if (check(Token.NUM)) {
            return new FastComplex(consume().value(), 0);
        }

        //DOES: evaluate numbers like i and e and variables (return the value given to the evaluator for the
        //DOES: variable)
        if (check(Token.VAR)) {
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
            for (varValue variable : variables) {
                if (peek().value() == variable.var) {
                    consume();
                    return variable.val;
                }
            }
            throw new IllegalArgumentException("unknown variable: " + (char) peek().value());
        }

        //DOES: evaluate simple brace arguments like roots that don't have special behavior like superscripting
        //INFO: braces and brackets are treated interchangably, the order of arguments determines which is which
        for (Token token : multipleArgOperations.values()) {
            if (check(token)) {
                consume();
                expect(Token.LBRACE);
                FastComplex arg1 = depth1();
                expect(Token.RBRACE);

                //DOES: check for a second argument and leave it blank if there is none
                FastComplex arg2 = null;
                if (check(Token.LBRACE)) {
                    consume();
                    arg2 = depth1();
                    expect(Token.RBRACE);
                }

                return switch (token) {
                    case FRAC -> {
                        secondArgException(arg2, "FRAC");
                        yield FastComplex.div(arg1, arg2);
                    }
                    // case HYPOT -> {
                    //     argException(args.size(), 2, 2);
                    //     yield FastComplex.sqrt(
                    //         FastComplex.add(FastComplex.sqr(args.get(0)), FastComplex.sqr(args.get(1)))
                    //     );
                    // }
                    case ROOT -> {
                        if (arg2 == null) yield FastComplex.sqrt(arg1);
                        yield FastComplex.nRoot(arg2, arg1);
                    }
                    default -> throw new IllegalArgumentException("unexpected multi-arg function: " + peek().type());
                };
            }
        }

        //DOES: evaluate logarithms
        //INFO: logarithms need to be evaluated separately due to the base being in subscript
        //INFO: defaults to ln (base e) if no base is found
        if (check(Token.LOG)) {
            consume();
            FastComplex base = null;

            //DOES: check for base (base has to be before antilog in LaTeX)
            if (check(Token.SUBS)) {
                consume();
                expect(Token.LBRACE);
                base = depth1();
                expect(Token.RBRACE);
            }

            //DOES: get the antilog
            expect(Token.LBRACE);
            FastComplex antiLog = depth1();
            expect(Token.RBRACE);

            return base == null
                ? FastComplex.log(antiLog)
                : FastComplex.div(FastComplex.log(antiLog), FastComplex.log(base));
        }

        //DOES: evaluate sums and products
        if (check(Token.SUM) || check(Token.PROD)) {
            boolean isSum = check(Token.SUM);
            consume();
            double finalIndex = -1;
            double startingIndex = 0;
            char var = 0;

            FastComplex result = null;

            boolean hasStartingIndex = false;
            boolean hasFinalIndex = false;

            //DOES: check for upper and lower bounds of sum or product (can be in any order)
            for (int i = 0; i < 2; i++) {
                if (check(Token.SUBS) && !hasStartingIndex) {
                    consume();
                    expect(Token.LBRACE);

                    //DOES: get variable to increment in sum or product
                    if (!check(Token.VAR)) throw new IllegalArgumentException(
                        "expected variable in lower bound of " + (isSum ? "sum" : "product")
                    );
                    var = (char) consume().value();

                    //DOES: check if variable in sum is the same as a provided variable
                    for (varValue variables : variables) {
                        if (var == variables.var) throw new IllegalArgumentException(
                            "variable provided for " + (isSum ? "sum" : "product") + "was already given"
                        );
                    }

                    //DOES: calculate starting index
                    expect(Token.EQUALS);
                    FastComplex arg = depth1();
                    expect(Token.RBRACE);

                    //DOES: convert starting index to double (can't be complex)
                    if (!arg.isReal()) throw new IllegalArgumentException(
                        "starting index of " + (isSum ? "sum" : "product") + " is complex"
                    );
                    startingIndex = arg.a;
                    hasStartingIndex = true;
                } else if (check(Token.POW) && !hasFinalIndex) {
                    consume();
                    expect(Token.LBRACE);

                    //DOES: calculate ending index & convert it to double (can't be complex)
                    FastComplex arg = depth1();
                    if (!arg.isReal()) throw new IllegalArgumentException(
                        "final index of " + (isSum ? "sum" : "product") + " is complex"
                    );
                    finalIndex = arg.a;

                    hasFinalIndex = true;
                    expect(Token.RBRACE);
                } else throw new IllegalArgumentException(
                    "missing " +
                        (hasStartingIndex ? "final index" : "starting index") +
                        " of " +
                        (isSum ? "sum" : "product")
                );
            }

            //DOES: find out what tokens belong to argument of sum or product
            int bodyStart = pos;
            int depth = 0;
            int bodyEnd = bodyStart;
            while (bodyEnd < tokens.size()) {
                if (tokens.get(bodyEnd).type() == Token.LBRACE) depth++;
                else if (tokens.get(bodyEnd).type() == Token.RBRACE) depth--;
                if (depth == 0) break;
                bodyEnd++;
            }

            //DOES: create evaluator from list of tokens that belong to sum or product argument
            LatexComplexEvaluator expression = new LatexComplexEvaluator(tokens.subList(bodyStart + 1, bodyEnd));

            //DOES: calculate sum or product
            varValue[] innerVars = Arrays.copyOf(variables, variables.length + 1);
            if (isSum) {
                result = new FastComplex(0, 0);
                for (double i = startingIndex; i <= finalIndex; i++) {
                    innerVars[innerVars.length - 1] = new varValue(var, new FastComplex(i, 0));
                    result = FastComplex.add(result, expression.eval(innerVars));
                }
            } else {
                result = new FastComplex(1, 0);
                for (double i = startingIndex; i <= finalIndex; i++) {
                    innerVars[innerVars.length - 1] = new varValue(var, new FastComplex(i, 0));
                    result = FastComplex.mult(result, expression.eval(innerVars));
                }
            }

            pos = bodyEnd + 1;

            return result;
        }

        //DOES: evaluate single-argument word functions like trig or abs
        for (Token token : wordOperations.values()) {
            if (check(token)) {
                consume();
                boolean par = check(Token.LPAR);
                if (par) consume();
                FastComplex result = par ? depth1() : depth5();
                if (par) expect(Token.RPAR);
                return switch (token) {
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
                    case CONJ -> FastComplex.conj(result);
                    case ABS -> new FastComplex(result.mag(), 0);
                    case EXP -> FastComplex.exp(result);
                    case SIN -> FastComplex.sin(result);
                    case COS -> FastComplex.cos(result);
                    case TAN -> FastComplex.tan(result);
                    case COT -> FastComplex.cot(result);
                    case SEC -> FastComplex.sec(result);
                    case CSC -> FastComplex.csc(result);
                    default -> throw new IllegalArgumentException("unexpected word function: got " + peek().type());
                };
            }
        }
        //CASE: when nothing in depth5 could evaluate the token
        throw new IllegalStateException("unexpected token: " + peek().type());
    }

    /**
     * returns the valueToken at the current position
     */
    private valueToken peek() {
        return tokens.get(pos);
    }

    /**
     * returns the valueToken at the current position and increments position
     * @return
     */
    private valueToken consume() {
        return tokens.get(pos++);
    }

    /**
     * checks if there is a certain token at the current position or if there are any tokens left
     * @param token the Token to be checked
     * @return false when there are no tokens left or the current token is not the same as the provided one
     */
    private boolean check(Token token) {
        return pos < tokens.size() && tokens.get(pos).type() == token;
    }

    /**
     * consumes the current token and throws an exception if it wasn't the provided token
     * @param token the token to be expected
     */
    private void expect(Token token) {
        if (!check(token)) throw new IllegalArgumentException("missing " + token);
        consume();
    }

    /**
     * throws an exception if the provided argument is null
     * @param arg2 any object
     * @param name the name of the function that required a second argument
     */
    private void secondArgException(Object arg2, String name) {
        if (arg2 == null) throw new IllegalArgumentException("no second argument provided for " + name);
    }
}
