package com.complexcalc.parser.registry;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Functions {

    private Functions() {}

    public static final Map<String, Token> wordFunctions = new LinkedHashMap<>();

    static {
        //IS: word functions
        //USAGE: string that should be parsed as the function first, then name of the Token
        String[][] entries = {
            //INFO: order by longest first for correct parsing
            { "floor-pair", "FLOOR" },
            { "ceil-pair", "CEIL" },
            { "arccosh", "ACOSH" },
            { "arccoth", "ACOTH" },
            { "arccsch", "ACSCH" },
            { "arcsech", "ASECH" },
            { "arcsinh", "ASINH" },
            { "arctanh", "ATANH" },
            { "acosh", "ACOSH" },
            { "acoth", "ACOTH" },
            { "acsch", "ACSCH" },
            { "arccos", "ACOS" },
            { "arccot", "ACOT" },
            { "arccsc", "ACSC" },
            { "arcsec", "ASEC" },
            { "arcsin", "ASIN" },
            { "arctan", "ATAN" },
            { "asech", "ASECH" },
            { "asinh", "ASINH" },
            { "atanh", "ATANH" },
            { "minus", "MINUS" },
            { "times", "MULT" },
            { "acos", "ACOS" },
            { "acot", "ACOT" },
            { "acsc", "ACSC" },
            { "asec", "ASEC" },
            { "asin", "ASIN" },
            { "atan", "ATAN" },
            { "cosh", "COSH" },
            { "coth", "COTH" },
            { "csch", "CSCH" },
            { "frac", "FRAC" },
            { "prod", "PROD" },
            { "sech", "SECH" },
            { "sinh", "SINH" },
            { "tanh", "TANH" },
            { "plus", "ADD" },
            { "abs", "ABS" },
            { "cos", "COS" },
            { "cot", "COT" },
            { "csc", "CSC" },
            { "div", "DIV" },
            { "exp", "EXP" },
            { "log", "LOG" },
            { "sec", "SEC" },
            { "sin", "SIN" },
            { "sum", "SUM" },
            { "tan", "TAN" },
            { "ln", "LOG" },
        };

        for (String[] e : entries) {
            wordFunctions.put(e[0], Token.valueOf(e[1]));
        }
    }

    //IS: word functions that take two arguments with no special behavior
    public static final Map<String, Token> braceArguments = new LinkedHashMap<>(
        Map.of("atan2", Token.ATAN2, "hypot", Token.HYPOT, "sqrt", Token.ROOT, "frac", Token.FRAC)
    );

    //IS: word functions that shouldn't parse to a token, but directly to a number
    public static final Map<String, Double> numbers = new LinkedHashMap<>(Map.of("pi", Math.PI, "tau", Math.TAU));

    //TODO: add with other custom function, expand
    public static final Map<String, Token> complexOperations = new LinkedHashMap<>(Map.of("conj", Token.CONJ));

    public record valueToken(Token type, double value) {}
}
