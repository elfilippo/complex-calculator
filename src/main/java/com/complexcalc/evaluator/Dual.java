package com.complexcalc.evaluator;

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
        int sLen = Math.min(cAmt(), z.cAmt());
        int lLen = Math.max(cAmt(), z.cAmt());
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
        if (cs.length != z.cAmt()) throw new IllegalArgumentException(
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

    /**
     * returns the amount of coefficients
     * @return length of coefficient array
     */
    public int cAmt() {
        return cs.length;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < cs.length; i++) {
            result += i == 0 ? truncateWhole(cs[i]) : truncateWhole(Math.abs(cs[i]));
            result += i == 0 ? "" : "ep";
            result += i < 2 ? " " : ("^" + i + " ");
            if (i < cs.length - 1) {
                if (cs[i] < 0) result += "- ";
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
