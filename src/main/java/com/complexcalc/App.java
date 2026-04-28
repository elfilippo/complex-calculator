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
        //System.out.println(new Parser("log(1+1,656^103)").evaluate());
        System.out.println(Complex.pow(new Complex(2, 0), -3, 0));
    }
}
