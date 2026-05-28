package com.complexcalc.evaluator;

import java.util.function.BiFunction;

public class Dual {

    private final double[] cs;

    public Dual(double[] coefficients) {
        cs = coefficients.clone();
    }

    public double[] getCs() {
        return cs.clone();
    }

    /**
     * returns the coefficient at the index
     * @return coefficient double
     */
    public double getC(int index) {
        return cs[index];
    }

    /**
     * returns the sum of both duals
     * @param z dual addend
     * @return dual sum
     */
    public Dual add(Dual z) {
        int sLen = Math.min(cAmount(), z.cAmount());
        int lLen = Math.max(cAmount(), z.cAmount());
        double[] resultCs = new double[lLen];
        for (int i = 0; i < sLen; i++) {
            resultCs[i] = cs[i] + z.getC(i);
        }
        return new Dual(resultCs);
    }

    /**
     * returns the sum of a real added to the dual
     * @param x real addend
     * @return dual sum
     */
    public Dual add(double x) {
        double[] result = cs.clone();
        result[0] += x;
        return new Dual(result);
    }

    /**
     * multiplies two dual numbers <p>
     * throws an exception if they are not of the same grade
     * @param z dual factor
     * @return dual product
     */
    public Dual mult(Dual z) {
        if (cs.length != z.cAmount()) throw new IllegalArgumentException(
            "trying to multiply two duals of different grade"
        );
        double[] result = new double[cs.length];
        for (int i = 0; i < cs.length; i++) {
            for (int j = 0; j < (cs.length - i); j++) {
                result[i + j] += cs[i] * z.getC(j);
            }
        }
        return new Dual(result);
    }

    public Dual mult(double x) {
        double[] result = new double[cs.length];
        for (int i = 0; i < cs.length; i++) {
            result[i] = cs[i] * x;
        }
        return new Dual(result);
    }

    private Dual function(double term, BiFunction<Double, Integer, Double> derivMethod) {
        double coefficient;
        Dual result = new Dual(new double[cs.length]);

        double currentFactorial = 1;
        double currentDeriv = term;

        double[] nilpotentArr = cs.clone();
        nilpotentArr[0] = 0;
        Dual nilpotentPart = new Dual(nilpotentArr);
        double[] oneArr = new double[cs.length];
        oneArr[0] = 1;
        Dual powerOfNilpotent = new Dual(oneArr);

        for (int i = 0; i < cAmount(); i++) {
            coefficient = (currentDeriv / currentFactorial);
            currentDeriv = derivMethod.apply(currentDeriv, i);

            result = result.add(powerOfNilpotent.mult(coefficient));

            if (i < cAmount() - 1) {
                powerOfNilpotent = powerOfNilpotent.mult(nilpotentPart);
                currentFactorial *= (i + 1);
            }
        }
        return result;
    }

    private double sqrtDeriv(double term, int number) {
        return (term * (0.5 - number)) / cs[0];
    }

    public void sqrtTest() {
        System.out.println(function(Math.sqrt(cs[0]), this::sqrtDeriv));
    }

    /**
     * returns the amount of coefficients
     * @return length of coefficient array
     */
    public int cAmount() {
        return cs.length;
    }

    public int order() {
        return cs.length - 1;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < cs.length; i++) {
            result += i == 0 ? truncateWhole(cs[i]) : truncateWhole(Math.abs(cs[i]));
            result += i == 0 ? "" : "ep";
            result += i < 2 ? " " : ("^" + i + " ");
            if (i < cs.length - 1) {
                if (cs[i + 1] < 0) result += "- ";
                else result += "+ ";
            }
        }
        return result;
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
