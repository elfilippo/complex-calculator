package com.complexcalc.evaluator;

/**
 * complex number class that is faster by only calculating polar coordinates when needed
 */
public class FastComplex {

    public final double a, b;

    public FastComplex(double a, double b) {
        this.a = a;
        this.b = b;
    }

    /**
     * returns a new complex from the polar form input re^(iθ)
     * @param magnitude r
     * @param argument θ
     */
    public static FastComplex polar(double magnitude, double argument) {
        double a = magnitude * Math.cos(argument);
        double b = magnitude * Math.sin(argument);
        return new FastComplex(a, b);
    }

    /**
     * returns a full Complex with polar coordinates
     */
    public Complex full() {
        return new Complex(a, b);
    }

    public double mag() {
        return Math.hypot(a, b);
    }

    public double arg() {
        return Math.atan2(b, a);
    }

    /**
     * returns the sum of two complex numbers
     * @param z summand
     * @param w summand
     */
    public static FastComplex add(FastComplex z, FastComplex w) {
        return new FastComplex(z.a + w.a, z.b + w.b);
    }

    /**
     * returns the sum of a real number and a complex number
     * @param z complex summand
     * @param x real summand
     * @return complex sum
     */
    public static FastComplex add(FastComplex z, double x) {
        return new FastComplex(z.a + x, z.b);
    }

    /**
     * returns the difference between two complex numbers
     * @param z complex minuend
     * @param w complex subtrahend
     * @return complex difference
     */
    public static FastComplex sub(FastComplex z, FastComplex w) {
        return new FastComplex(z.a - w.a, z.b - w.b);
    }

    /**
     * returns the difference between a complex and a real number
     * @param z complex minuend
     * @param x real subtrahend
     * @return complex difference
     */
    public static FastComplex sub(FastComplex z, double x) {
        return new FastComplex(z.a - x, z.b);
    }

    /**
     * returns the difference between a real and a complex number
     * @param x real minuend
     * @param z complex subtrahend
     * @return complex difference
     */
    public static FastComplex sub(double x, FastComplex z) {
        return new FastComplex(x - z.a, -z.b);
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
    public static int quadrant(FastComplex z) {
        if (z.a >= 0 && z.b >= 0) return 1;
        if (z.a < 0 && z.b >= 0) return 2;
        if (z.a <= 0 && z.b <= 0) return 3;
        return 4;
    }

    /**
     * returns the product of two complex numbers
     * @param z complex factor
     * @param w complex factor
     * @return complex product
     */
    public static FastComplex mult(FastComplex z, FastComplex w) {
        return new FastComplex(z.a * w.a - z.b * w.b, z.a * w.b + z.b * w.a);
    }

    /**
     * returns the product of a complex factor with a real scalar x
     * @param z complex factor
     * @param x real scalar
     * @return complex product
     */
    public static FastComplex mult(FastComplex z, double x) {
        return new FastComplex(x * z.a, x * z.b);
    }

    /**
     * returns the quotient of z / w
     * @param z complex dividend
     * @param w complex divisor
     * @return complex quotient
     */
    public static FastComplex div(FastComplex z, FastComplex w) {
        double real = (z.a * w.a + z.b * w.b) / (w.a * w.a + w.b * w.b);
        double imaginary = (z.b * w.a - z.a * w.b) / (w.a * w.a + w.b * w.b);
        return new FastComplex(real, imaginary);
    }

    /**
     * returns the quotient of z / x
     * @param z complex dividend
     * @param x real divisor
     * @return complex quotient
     */
    public static FastComplex div(FastComplex z, double x) {
        return new FastComplex(z.a / x, z.b / x);
    }

    /**
     * returns the quotient of x / z
     * @param x real dividend
     * @param z complex divisor
     * @return complex quotient
     */
    public static FastComplex div(double x, FastComplex z) {
        return new FastComplex(x / z.a, x / z.b);
    }

    /**
     * returns the principal complex log to the base e
     * @param z complex anti-logaritm
     */
    public static FastComplex log(FastComplex z) {
        return new FastComplex(Math.log(z.mag()), z.arg());
    }

    /**
     * returns all values of the complex logarithm in a range including the max term <p>
     * the principal log is term (value) 0
     * @param z complex anti-logarithm
     * @param min lowest term
     * @param max highest term
     * @return array of complex logarithms
     */
    public static FastComplex[] logRange(FastComplex z, int min, int max) {
        FastComplex[] result = new FastComplex[max - min + 1];
        double magnitude = Math.log(z.mag());
        for (int i = min; i <= max; i++) {
            result[i - min] = new FastComplex(magnitude, z.arg() + (i * 2 * Math.PI));
        }
        return result;
    }

    /**
     * returns a specific value of the complex logarithm at a term number k <p>
     * the principal log is term (value) 0
     * @param z complex anti-logarithm
     * @param k term number
     */
    public static FastComplex log(FastComplex z, int k) {
        return new FastComplex(Math.log(z.mag()), z.arg() + 2 * k * Math.PI);
    }

    /**
     * returns the complex principal logarithm of x
     * @param x real anti-logarithm
     * @return complex logarithm
     */
    public static FastComplex log(double x) {
        if (x == 0) throw new IllegalArgumentException();
        return new FastComplex(Math.log(Math.abs(x)), x > 0 ? 0 : Math.PI);
    }

    /**
     * returns the principal result of complex z raised to real x <p>
     * since complex exponentiation is multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex base
     * @param x real exponent
     */
    public static FastComplex pow(FastComplex z, double x) {
        return polar(Math.pow(z.mag(), x), z.arg() * x);
    }

    /**
     * returns the principal result of complex z raised to complex w <p>
     * since complex exponentiation is multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex base
     * @param w complex exponent
     */
    public static FastComplex pow(FastComplex z, FastComplex w) {
        return (polar(Math.exp(w.a * Math.log(z.mag()) - w.b * z.arg()), w.b * Math.log(z.mag()) + w.a * z.arg()));
    }

    /**
     * returns term k of a complex raised to a complex <p>
     * since complex exponentiation is multi-valued for non-integers, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex base
     * @param w complex exponent
     * @param k term number
     */
    public static FastComplex pow(FastComplex z, FastComplex w, int k) {
        if (w.isReal() && w.a % 1 == 0) return polar(Math.pow(z.mag(), w.a), z.arg() * w.a);
        return (
            polar(
                Math.exp(w.a * Math.log(z.mag()) - w.b * (z.arg() + 2 * Math.PI * k)),
                w.b * Math.log(z.mag()) + w.a * (z.arg() + 2 * Math.PI * k)
            )
        );
    }

    /**
     * returns term k of a complex raised to a real <p>
     * since complex exponentiation is multi-valued for non-integers, k is the distance of
     * the term from the principal result (the principal result being k = 0)
     * @param z complex base
     * @param x real exponent
     * @param k term
     */
    public static FastComplex pow(FastComplex z, double x, int k) {
        if (x % 1 == 0) return polar(Math.pow(z.mag(), x), z.arg() * x);
        return polar(Math.pow(z.mag(), x), (z.arg() + (Math.PI * 2 * k)) * x);
    }

    /**
     * returns the principal square root of a complex
     * since complex roots are multi-valued for non-integers, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex
     */
    public static FastComplex sqrt(FastComplex z) {
        return pow(z, 0.5);
    }

    /**
     * returns the principal n-th root of a complex
     * since complex roots are multi-valued, this returns
     * the principal result based on normalized θ (-π < θ <= π)
     * @param z complex radicand
     * @param n real degree
     */
    public static FastComplex nRoot(FastComplex z, double n) {
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
    public static FastComplex nRoot(FastComplex z, double n, int k) {
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
    public static FastComplex nRoot(FastComplex z, FastComplex w, int k) {
        return pow(z, div(1, w), k);
    }

    /**
     * returns the conjugate of the number (inverse of b)
     * @param z complex
     * @return conjugate
     */
    public static FastComplex conjugate(FastComplex z) {
        return new FastComplex(z.a, -z.b);
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
        return full().toStringPolar();
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
