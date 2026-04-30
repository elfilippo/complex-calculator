package com.complexcalc;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //System.out.println(Character.isAlphabetic('-'));
        //System.out.println(Parser.tokenize("-sinx^6-14y*-2.5i"));
        //System.out.println(Parser.tokenize("ln5"));
        //System.out.println(Parser.tokenize("sqrtxn"));
        System.out.println(new Parser("sqrtxn").evaluate('x', 4, 'n', 3));
        //System.out.println(Complex.pow(new Complex(2, 0), -3, 0));
    }
}
