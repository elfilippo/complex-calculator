package com.complexcalc;

import java.lang.Math;

public class Complex {

    /**
     * the real part
     */
    public final double a;

    /**
     * the imaginary part
     */
    public final double b;

    /**
     * the magnitude
     */
    public final double r;

    /**
     * the normalized argument
     */
    public final double θ;

    /**
     * the argument given at construction time <p>
     * relevant for multi-valued functions like the complex logarithm <p>
     * default (normalized) if constructed in rectangular form <p>
     * only to be used when created manually by polar form <p>
     * NOT PRESERVED IN MOST OPERATIONS
     */
    public final double givenθ;

    /**
     * creates a complex number in the form a + bi
     * @param real the real part (a)
     * @param imaginary the imaginary part (b)
     */
    public Complex(double real, double imaginary) {
        this(real, imaginary, Math.hypot(real, imaginary), Math.atan2(imaginary, real));
    }

    /**
     * class-intern constructor for all parameters of complex number.
     * private to prevent value mismatches by user error.
     * @param real
     * @param imaginary
     * @param magnitude
     * @param argument
     */
    private Complex(double real, double imaginary, double magnitude, double argument) {
        a = real;
        b = imaginary;
        r = magnitude;
        givenθ = argument;
        θ = Math.atan2(imaginary, real);
    }

    /**
     * returns a new Complex from a polar form input re^iθ
     * @param magnitude r
     * @param argument θ
     * @return Complex
     */
    public static Complex polar(double magnitude, double argument) {
        double a = magnitude * Math.cos(argument);
        double b = magnitude * Math.sin(argument);
        return new Complex(a, b, magnitude, argument);
    }

    /**
     * returns the sum of two complex numbers
     */
    public static Complex add(Complex z, Complex w) {
        return new Complex(z.a + w.a, z.b + w.b);
    }

    /**
     * adds a real number to a complex number
     * @param z a complex number
     * @param x a real double
     * @return complex result
     */
    public static Complex add(Complex z, double x) {
        return new Complex(z.a + x, z.b);
    }

    /**
     * returns the difference between two complex numbers
     * @param z the minuend complex
     * @param w the subtrahend complex
     * @return the difference
     */
    public static Complex subtract(Complex z, Complex w) {
        return new Complex(z.a - w.a, z.b - w.b);
    }

    /**
     * returns the difference between a complex and a real number
     * @param z the minuend complex
     * @param x the subtrahend real
     * @return the difference
     */
    public static Complex subtract(Complex z, double x) {
        return new Complex(z.a - x, z.b);
    }

    /**
     * returns the difference between a real and a complex number
     * @param x the minuend real
     * @param z the subtrahend complex
     * @return the difference
     */
    public static Complex subtract(double x, Complex z) {
        return new Complex(x - z.a, -z.b);
    }

    /**
     * returns true if there is no imaginary part
     */
    public boolean isReal() {
        return b == 0;
    }

    /**
     * multiplies two complex numbers with respect to the given arguments
     * @param z complex factor
     * @param w complex factor
     * @return product
     */
    public static Complex multiply(Complex z, Complex w) {
        return polar(z.r * w.r, z.givenθ + w.givenθ);
    }

    /**
     * multiplies a complex number with a scalar x
     * @param z complex factor
     * @param x real scalar
     * @return
     */
    public static Complex multiply(Complex z, double x) {
        return new Complex(x * z.a, x * z.b);
    }

    /**
     * divides the complex number z by w
     * @param z complex dividend
     * @param w complex divisor
     * @return complex quotient
     */
    public static Complex divide(Complex z, Complex w) {
        double real = (z.a * w.a + z.b * w.b) / (w.a * w.a + w.b * w.b);
        double imaginary = (z.b * w.a - z.a * w.b) / (w.a * w.a + w.b * w.b);
        return new Complex(real, imaginary);
    }

    /**
     * divides the complex number z by the real number x
     * @param z complex dividend
     * @param x real divisor
     * @return complex quotient
     */
    public static Complex divide(Complex z, double x) {
        return new Complex(z.a / x, z.b / x);
    }

    /**
     * divides the real number x by the complex number z
     * @param x real dividend
     * @param z complex divisor
     * @return complex quotient
     */
    public static Complex divide(double x, Complex z) {
        return new Complex(x / z.a, x / z.b);
    }

    /**
     * returns the complex logarithm based on givenθ <p>
     * since the complex log is multi-valued, it can return different numbers for the same z based on the argument
     * @param z complex number
     */
    public static Complex givenLog(Complex z) {
        return new Complex(Math.log(z.r), z.givenθ);
    }

    /**
     * returns the principal complex log
     * @param z
     * @return
     */
    public static Complex log(Complex z) {
        return new Complex(Math.log(z.r), z.θ);
    }

    /**
     * returns all values of the complex logarithm in a range including the max term <p>
     * the principal log is term (value) 0
     * @param z complex number
     * @param min lowest term
     * @param max highest term
     * @return an array of complex values
     */
    public static Complex[] logRange(Complex z, int min, int max) {
        Complex[] result = new Complex[max - min + 1];
        double magnitude = Math.log(z.r);
        for (int i = min; i <= max; i++) {
            result[i - min] = new Complex(magnitude, z.θ + (i * 2 * Math.PI));
        }
        return result;
    }

    /**
     * returns a specific value of the complex logarithm at a term number <p>
     * the principal log is term (value) 0
     * @param z
     * @param term
     * @return
     */
    public static Complex logSpecific(Complex z, int term) {
        return new Complex(Math.log(z.r), z.θ + 2 * term * Math.PI);
    }

    /**
     *returns the complex principal logarithm of x
     * @param x real number
     * @return complex result
     */
    public static Complex log(double x) {
        if (x == 0) throw new IllegalArgumentException();
        return new Complex(Math.log(Math.abs(x)), x > 0 ? 0 : Math.PI);
    }

    /**
     * returns the conjugate of the number (inverse of b)
     * @param z complex number
     * @return complex conjugate
     */
    public static Complex conjugate(Complex z) {
        return new Complex(z.a, -z.b);
    }

    /**
     * returns a string in square form <p>
     * truncates decimals for whole numbers and omits empty values <p>
     */
    @Override
    public String toString() {
        if (b == 0) return truncateWhole(a);
        if (a == 0) return (b < 0 ? "-" : "") + truncateWhole(Math.abs(b)) + "i";

        if (b < 0) return truncateWhole(a) + " - " + truncateWhole(Math.abs(b)) + "i";
        return truncateWhole(a) + " + " + truncateWhole(Math.abs(b)) + "i";
    }

    /**
     * returns a string in normalized polar form <p>
     * truncates decimals for whole numbers and omits empty values <p>
     */
    public String toStringPolar() {
        if (r == 0) return "0";
        if (θ == 0) return truncateWhole(r);
        return ((r == 1 ? "" : truncateWhole(r)) + "e^(" + truncateWhole(θ) + "i)");
    }

    /**
     * returns a string in polar form for the argument given at construction <p>
     * truncates decimals for whole numbers and omits empty values <p>
     */
    public String toStringPolarGiven() {
        if (r == 0) return "0";
        if (givenθ == 0) return truncateWhole(r);
        return ((r == 1 ? "" : truncateWhole(r)) + "e^(" + truncateWhole(givenθ) + "i)");
    }

    /**
     * truncates decimals from whole numbers and returns as string
     * @param x double
     * @return String
     */
    private String truncateWhole(double x) {
        return x % 1 == 0 ? String.valueOf((long) x) : String.valueOf(x);
    }
}
