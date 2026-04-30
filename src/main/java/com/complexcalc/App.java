package com.complexcalc;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //System.out.println(Character.isAlphabetic('-'));
        //System.out.println(Parser.tokenize("hypot(2,3)"));
        //System.out.println(Parser.tokenize("(4*0.234^3-(sqrt833)/12-sqrt0.123^3)*2"));
        //System.out.println(Parser.tokenize("ln5"));
        //System.out.println(Parser.tokenize("sqrtxn"));
        System.out.println(new Parser("hypot(sqrt2,sqrt2)").evaluate());
        //System.out.println(Complex.pow(new Complex(2, 0), -3, 0));
    }
}
