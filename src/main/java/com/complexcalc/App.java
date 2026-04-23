package com.complexcalc;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println(new Complex(6, 7));
        System.out.println(new Complex(-1, 0).toStringPolar());
        System.out.println(Complex.log(-10));
    }
}
