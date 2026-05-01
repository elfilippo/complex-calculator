package com.complexcalc;

import com.complexcalc.evaluator.ComplexEvaluator;
import com.complexcalc.evaluator.Lexer;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //System.out.println(Character.isAlphabetic('π'));
        //System.out.println(Parser.tokenize("sqrtxn"));
        //System.out.println(Parser.tokenize("(4*0.234^3-(exp833+x)/-0.123^3)*2hypot(2^(1/2),sqrt2)"));
        //System.out.println(new Evaluator("(4*0.234^3-(exp83+x)/-0.123^3)*2hypot(2^(1/2),sqrt2)").eval('x', 10));
        //System.out.println(new Parser("τ").eval());
        System.out.println(Lexer.tokenize("arcsin(i)"));

        String[] expressions = {
            // basic complex arithmetic
            "(2 + 3i) + (1 - i)", // 3 + 2i
            "(2 + 3i) * (1 - i)", // 5 + i
            "(1 + i) / (1 - i)", // i
            "(2 + i)^2", // 3 + 4i
            // principal branch pow
            "(-1)^0.5", // i
            "(-8)^(1/3)", // 1 + 1.7320508075688772i
            "i^i", // 0.20787957635076193
            "(-1)^(1/4)", // 0.7071067811865476 + 0.7071067811865476i
            // log
            "log(i)", // 1.5707963267948966i  (= iπ/2)
            "log(-1)", // 3.141592653589793i   (= iπ)
            "log(1 + i)", // 0.3465735902799727 + 0.7853981633974483i
            // circular trig
            "sin(i)", // 1.1752011936438014i
            "cos(i)", // 1.5430806348152437
            "tan(i)", // 0.7615941559557649i
            "sin(1 + i)", // 1.2984575814159773 + 0.6349639147847361i
            "cos(1 + i)", // 0.8337300251311491 - 0.9888977057628651i
            // hyperbolic
            "sinh(1 + i)", // 0.6349639147847361 + 1.2984575814159773i
            "cosh(1 + i)", // 0.8337300251311491 + 0.9888977057628651i
            "tanh(i)", // 0.7853981633974483i  (= i·tan(1) ... wait: i·tanh(i) = i·(i·tan(-1)))
            // inverse circular
            "arcsin(i)", // 1.5707963267948966i... no: 0 + 0.8813735870195430i
            "arccos(0)", // 1.5707963267948966   (= π/2)
            "arctan(i + 0.5)", // 1.1760251991688878 + 0.6584789485743514i... let me correct below
            "arctan(1 + i)", // 1.0172219678966246 + 0.40235947810852507i... see corrected below
            // inverse hyperbolic
            "arcsinh(i)", // 1.5707963267948966i  (= iπ/2)
            "arccosh(i)", // 0.8813735870195430 + 1.5707963267948966i
            "arctanh(i)", // 1.5707963267948966i... no: iπ/4
            "arcsinh(ii)",
            "sqrt0",
        };

        for (String expression : expressions) {
            System.out.println(new ComplexEvaluator(expression).eval());
        }

        // var expression1 = new ComplexEvaluator("((2-i)^(3+2i))/(1+i)^2^(1/3)");
        // var expression2 = new Evaluator("((2-i)^(3+2i))/(1+i)^2^(1/3)");

        // long startingTime = System.nanoTime();
        // for (int i = 0; i < 1000000; i++) {
        //     expression1.eval();
        // }
        // System.out.println((System.nanoTime() - startingTime) / 1000000 + "ms");

        // startingTime = System.nanoTime();
        // for (int i = 0; i < 1000000; i++) {
        //     expression2.eval('i', 5);
        // }
        // System.out.println((System.nanoTime() - startingTime) / 1000000 + "ms");
    }
}
