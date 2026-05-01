package com.complexcalc;

import com.complexcalc.evaluator.ComplexEvaluator;
import com.complexcalc.evaluator.Evaluator;
import com.complexcalc.evaluator.FastComplex;
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
        System.out.println(FastComplex.cos(new FastComplex(0, 4)));
        System.exit(0);
        //System.out.println(new Parser("τ").eval());

        String[] tests = {
            "2+3",
            "2+3*4",
            "(2+3)*4",
            "-5+2",
            "--5",
            "-(3+2)",
            "i",
            "2i",
            "i*i",
            "i^2",
            "i^3",
            "i^4",
            "1/i",
            "(i+1)(i-1)",
            "(2+3i)+(4-5i)",
            "(2+3i)-(4-5i)",
            "(2+3i)(4-5i)",
            "(3+4i)/(1-2i)",
            "(5-2i)/(3+i)",
            "(1+i)^2",
            "(1+i)^3",
            "(1+i)^4",
            "(2-3i)^2",
            "(2-3i)^3",
            "(1+i)^0.5",
            "(-1)^0.5",
            "(-8)^(1.0/3)",
            "(1+i)^i",
            "i^i",
            "(2+3i)^(1+i)",
            "(1-i)^(2+i)",
            "2^3^2",
            "-2^2",
            "(-2)^2",
            "1+2*3^2",
            "(1+2i)^2+3(2-i)",
            "0^0",
            "1/0",
            "((2-i)^(3+2i))/(1+i)^2",
        };

        String[] problems = { "(-1)^0.5", "(-8)^(1/3)", "2^3^2" };

        for (String expression : problems) {
            System.out.println(new ComplexEvaluator(expression).eval());
        }

        var expression1 = new ComplexEvaluator("((2-i)^(3+2i))/(1+i)^2^(1/3)");
        var expression2 = new Evaluator("((2-i)^(3+2i))/(1+i)^2^(1/3)");

        long startingTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            expression1.eval();
        }
        System.out.println((System.nanoTime() - startingTime) / 1000000 + "ms");

        startingTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            expression2.eval('i', 5);
        }
        System.out.println((System.nanoTime() - startingTime) / 1000000 + "ms");
    }
}
