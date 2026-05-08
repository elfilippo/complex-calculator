package com.complexcalc.evaluator;

public class Vector3d {

    public final double i, j, k;

    public Vector3d(double x, double y, double z) {
        this.i = x;
        this.j = y;
        this.k = z;
    }

    public Vector3d add(Vector3d v) {
        return new Vector3d(i + v.i, j + v.j, k + v.k);
    }

    public Vector3d scale(double x) {
        return new Vector3d(i * x, j * x, k * x);
    }

    public double dot(Vector3d v) {
        return i * v.i + j * v.j + k * v.k;
    }

    public double selfDot() {
        return i * i + j * j + k * k;
    }

    public Vector3d cross(Vector3d v) {
        return new Vector3d(j * v.k - k * v.j, k * v.i - i * v.k, i * v.j - j * v.i);
    }

    public double mag() {
        return Math.sqrt(i * i + j * j + k * k);
    }

    public Vector3d div(double x) {
        return new Vector3d(i / x, j / x, k / x);
    }

    public Vector3d normalize() {
        return div(mag());
    }

    public Vector3d negate() {
        return new Vector3d(-k + 0.0, -j + 0.0, -i + 0.0);
    }
}
