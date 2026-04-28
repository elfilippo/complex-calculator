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
     * returns a new complex number from the cartesian form input a + bi
     * @param real the real part (a)
     * @param imaginary the imaginary part (b)
     */
    public Complex(double real, double imaginary) {
        this(real, imaginary, Math.hypot(real, imaginary), Math.atan2(imaginary, real));
    }

    /**
     * class-intern constructor for all parameters of complex number.
     * private to prevent value mismatches by user error.
     * @param real a
     * @param imaginary b
     * @param magnitude r
     * @param argument θ
     */
    private Complex(double real, double imaginary, double magnitude, double argument) {
        a = real;
        b = imaginary;
        r = magnitude;
        givenθ = argument;
        θ = Math.atan2(imaginary, real);
    }

    /**
     * returns a new complex from the polar form input re^(iθ)
     * @param magnitude r
     * @param argument θ
     */
    public static Complex polar(double magnitude, double argument) {
        double a = magnitude * Math.cos(argument);
        double b = magnitude * Math.sin(argument);
        return new Complex(a, b, magnitude, argument);
    }

    /**
     * returns the sum of two complex numbers
     * @param z summand
     * @param w summand
     */
    public static Complex add(Complex z, Complex w) {
        return new Complex(z.a + w.a, z.b + w.b);
    }

    /**
     * returns the sum of a real number and a complex number
     * @param z complex summand
     * @param x real summand
     * @return complex sum
     */
    public static Complex add(Complex z, double x) {
        return new Complex(z.a + x, z.b);
    }

    /**
     * returns the difference between two complex numbers
     * @param z complex minuend
     * @param w complex subtrahend
     * @return complex difference
     */
    public static Complex sub(Complex z, Complex w) {
        return new Complex(z.a - w.a, z.b - w.b);
    }

    /**
     * returns the difference between a complex and a real number
     * @param z complex minuend
     * @param x real subtrahend
     * @return complex difference
     */
    public static Complex sub(Complex z, double x) {
        return new Complex(z.a - x, z.b);
    }

    /**
     * returns the difference between a real and a complex number
     * @param x real minuend
     * @param z complex subtrahend
     * @return complex difference
     */
    public static Complex sub(double x, Complex z) {
        return new Complex(x - z.a, -z.b);
    }

    /**
     * returns true if the number is fully real (no imaginary part)
     */
    public boolean isReal() {
        return b == 0;
    }

    /**
     * returns the int number of the quadrant of the complex number <p>
     * considers numbers on the axes as part of the lowest numbered quadrant they're in <p>
     * quadrants are numbered counterclockwise starting from the positive real and positive imaginary
     */
    public static int quadrant(Complex z) {
        if (z.a >= 0 && z.b >= 0) return 1;
        if (z.a < 0 && z.b >= 0) return 2;
        if (z.a <= 0 && z.b <= 0) return 3;
        return 4;
    }

    /**
     * returns the product of two complex numbers with the arguments given at construction
     * @param z complex factor
     * @param w complex factor
     * @return complex product
     */
    public static Complex mult(Complex z, Complex w) {
        return polar(z.r * w.r, z.givenθ + w.givenθ);
    }

    /**
     * returns the product of a complex factor with a real scalar x
     * @param z complex factor
     * @param x real scalar
     * @return complex product
     */
    public static Complex mult(Complex z, double x) {
        return new Complex(x * z.a, x * z.b);
    }

    /**
     * returns the quotient of z / w
     * @param z complex dividend
     * @param w complex divisor
     * @return complex quotient
     */
    public static Complex div(Complex z, Complex w) {
        double real = (z.a * w.a + z.b * w.b) / (w.a * w.a + w.b * w.b);
        double imaginary = (z.b * w.a - z.a * w.b) / (w.a * w.a + w.b * w.b);
        return new Complex(real, imaginary);
    }

    /**
     * returns the quotient of z / x
     * @param z complex dividend
     * @param x real divisor
     * @return complex quotient
     */
    public static Complex div(Complex z, double x) {
        return new Complex(z.a / x, z.b / x);
    }

    /**
     * returns the quotient of x / z
     * @param x real dividend
     * @param z complex divisor
     * @return complex quotient
     */
    public static Complex div(double x, Complex z) {
        return new Complex(x / z.a, x / z.b);
    }

    /**
     * returns the complex logarithm based on givenθ to the base e <p>
     * since the complex log is multi-valued, it can return different numbers for the same z
     * based on the argument given at construction
     * @param z complex anti-logarithm
     */
    public static Complex logGiven(Complex z) {
        return new Complex(Math.log(z.r), z.givenθ);
    }

    /**
     * returns the principal complex log to the base e
     * @param z complex anti-logaritm
     */
    public static Complex log(Complex z) {
        return new Complex(Math.log(z.r), z.θ);
    }

    /**
     * returns all values of the complex logarithm in a range including the max term <p>
     * the principal log is term (value) 0
     * @param z complex anti-logarithm
     * @param min lowest term
     * @param max highest term
     * @return array of complex logarithms
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
     * returns a specific value of the complex logarithm at a term number k <p>
     * the principal log is term (value) 0
     * @param z complex anti-logarithm
     * @param k term number
     */
    public static Complex log(Complex z, int k) {
        return new Complex(Math.log(z.r), z.θ + 2 * k * Math.PI);
    }

    /**
     * returns the complex principal logarithm of x
     * @param x real anti-logarithm
     * @return complex logarithm
     */
    public static Complex log(double x) {
        if (x == 0) throw new IllegalArgumentException();
        return new Complex(Math.log(Math.abs(x)), x > 0 ? 0 : Math.PI);
    }

    /**
     * returns the principal result of complex z raised to real x <p>
     * since complex exponentiation is multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex base
     * @param x real exponent
     */
    public static Complex pow(Complex z, double x) {
        return polar(Math.pow(z.r, x), z.θ * x);
    }

    /**
     * returns the principal result of complex z raised to complex w <p>
     * since complex exponentiation is multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex base
     * @param w complex exponent
     */
    public static Complex pow(Complex z, Complex w) {
        return (polar(Math.exp(w.a * Math.log(z.r) - w.b * z.θ), w.b * Math.log(z.r) + w.a * z.θ));
    }

    /**
     * returns term k of a complex raised to a complex <p>
     * since complex exponentiation is multi-valued for non-integers, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex base
     * @param w complex exponent
     * @param k term number
     */
    public static Complex pow(Complex z, Complex w, int k) {
        if (w.isReal() && w.a % 1 == 0) return polar(Math.pow(z.r, w.a), z.θ * w.a);
        return (
            polar(
                Math.exp(w.a * Math.log(z.r) - w.b * (z.θ + 2 * Math.PI * k)),
                w.b * Math.log(z.r) + w.a * (z.θ + 2 * Math.PI * k)
            )
        );
    }

    /**
     * returns the result of complex z raised to real x based on given θ <p>
     * since complex exponentiation is multi-valued for non-integers, this can give different
     * results for the same complex number based on the argument given at construction
     * @param z the base
     * @param x the exponent
     */
    public static Complex powGiven(Complex z, double x) {
        if (x % 1 == 0) return polar(Math.pow(z.r, x), z.θ * x);
        return polar(Math.pow(z.r, x), z.givenθ * x);
    }

    /**
     * returns term k of a complex raised to a real <p>
     * since complex exponentiation is multi-valued for non-integers, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex base
     * @param x real exponent
     * @param k term
     */
    public static Complex pow(Complex z, double x, int k) {
        if (x % 1 == 0) return polar(Math.pow(z.r, x), z.θ * x);
        return polar(Math.pow(z.r, x), (z.θ + (Math.PI * 2 * k)) * x);
    }

    /**
     * returns the principal square root of a complex
     * since complex roots are multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex
     */
    public static Complex sqrt(Complex z) {
        return pow(z, 0.5);
    }

    /**
     * returns the principal n-th root of a complex
     * since complex roots are multi-valued, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex radicand
     * @param n real degree
     */
    public static Complex nRoot(Complex z, double n) {
        if (n == 0) throw new IllegalArgumentException("trying to take 0th root");
        return pow(z, 1 / n);
    }

    /**
     * returns term k of the n-th complex root <p>
     * since complex roots are multi-valued, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex radicand
     * @param n real degree
     */
    public static Complex nRoot(Complex z, double n, int k) {
        if (n == 0) throw new IllegalArgumentException("trying to take 0th root");
        return pow(z, 1 / n, k);
    }

    /**
     * returns term k of the n-th complex root <p>
     * since complex roots are multi-valued, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex radicand
     * @param n complex degree
     */
    public static Complex nRoot(Complex z, Complex w, int k) {
        return pow(z, div(1, w), k);
    }

    /**
     * returns the conjugate of the number (inverse of b)
     * @param z complex
     * @return conjugate
     */
    public static Complex conjugate(Complex z) {
        return new Complex(z.a, -z.b);
    }

    /**
     * returns a string in cartesian (square) form <p>
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
