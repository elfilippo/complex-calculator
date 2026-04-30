package com.complexcalc;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //System.out.println(Character.isAlphabetic('-'));
        //System.out.println(Parser.tokenize("hypot(2,3)"));
        //System.out.println(Parser.tokenize("(4*0.234^3-(sqrt833+x)/12-sqrt0.123^3)*2"));
        //System.out.println(Parser.tokenize("ln5"));
        //System.out.println(Parser.tokenize("sqrtxn"));
        //System.out.println(Parser.tokenize("(4*0.234^3-(exp833+x)/-0.123^3)*2hypot(2^(1/2),sqrt2)"));
        System.out.println(new Parser("(4*0.234^3-(exp83+x)/-0.123^3)*2hypot(2^(1/2),sqrt2)").evaluate('x', 10));
        //System.out.println(Complex.pow(new Complex(2, 0), -3, 0));
    }
}
