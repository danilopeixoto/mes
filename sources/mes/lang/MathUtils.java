// Copyright (c) 2017, Danilo Peixoto. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package mes.lang;

import java.util.Random;

public abstract class MathUtils {
    private static final Random random = new Random();

    public static final double E = Math.E;
    public static final double PI = Math.PI;
    public static final double EPSILON = Math.ulp(1.0);
    public static final double INFINITY = Double.MAX_VALUE;
    public static final double UNDEFINED = Double.NaN;

    public static double abs(double x) {
        return Math.abs(x);
    }

    public static double frac(double x) {
        return x - floor(x);
    }

    public static double floor(double x) {
        return Math.floor(x);
    }

    public static double ceil(double x) {
        return Math.ceil(x);
    }

    public static double round(double x) {
        return Math.round(x);
    }

    public static double trunc(double x) {
        return x < 0 ? ceil(x) : floor(x);
    }

    public static double sign(double x) {
        return Math.signum(x);
    }

    public static double clamp(double x, double a, double b) {
        return max(a, min(b, x));
    }

    public static double remap(double x, double a0, double b0, double a1, double b1) {
        return a1 + (x - a0) * (b1 - a1) / (b0 - a0);
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    public static double sqrt(double x) {
        return Math.sqrt(x);
    }

    public static double cbrt(double x) {
        return Math.cbrt(x);
    }

    public static double exp(double x) {
        return Math.exp(x);
    }

    public static double log(double x) {
        return Math.log(x);
    }

    public static double log(double a, double b) {
        return log(a) / log(b);
    }

    public static double erf(double x) {
        double t = 1.0 / (1.0 + 0.5 * abs(x));
        double e = 1.0 - t * Math.exp(-x * x - 1.26551223
                + t * (1.00002368
                + t * (0.37409196
                + t * (0.09678418
                + t * (-0.18628806
                + t * (0.27886807
                + t * (-1.13520398
                + t * (1.48851587
                + t * (-0.82215223
                + t * (0.17087277))))))))));

        return x < 0 ? -e : e;
    }

    public static double hypot(double a, double b) {
        return Math.hypot(a, b);
    }

    public static double lerp(double x, double a, double b) {
        return a + x * (b - a);
    }

    public static double smoothstep(double x, double a, double b) {
        double t = clamp((x - a) / (b - a), 0, 1.0);
        return t * t * (3.0 - (t * 2.0));
    }

    public static double sin(double x) {
        return Math.sin(x);
    }

    public static double cos(double x) {
        return Math.cos(x);
    }

    public static double tan(double x) {
        return Math.tan(x);
    }

    public static double asin(double x) {
        return Math.asin(x);
    }

    public static double acos(double x) {
        return Math.acos(x);
    }

    public static double atan(double x) {
        return Math.atan(x);
    }

    public static double atan(double a, double b) {
        return Math.atan2(a, b);
    }

    public static double sinh(double x) {
        return Math.sinh(x);
    }

    public static double cosh(double x) {
        return Math.cosh(x);
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static double deg(double x) {
        return Math.toDegrees(x);
    }

    public static double rad(double x) {
        return Math.toRadians(x);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static double rand() {
        return random.nextDouble();
    }

    public static int seed() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static boolean isfinite(double x) {
        return Double.isFinite(x);
    }

    public static boolean isinf(double x) {
        return Double.isInfinite(x);
    }

    public static boolean isnan(double x) {
        return Double.isNaN(x);
    }

    public static int linear(double a, double b, MutableDouble x) {
        if (a != 0) {
            x.set(-b / a);

            return 1;
        }

        return b != 0 ? 0 : -1;
    }

    public static int quadratic(double a, double b, double c, MutableDouble[] x) {
        if (a == 0)
            return linear(b, c, x[0]);

        double d = b * b - 4.0 * a * c;

        if (d > 0) {
            double s = sqrt(d);
            double q = 2.0 * a;

            x[0].set((-b + s) / q);
            x[1].set((-b - s) / q);

            if (x[0].compareTo(x[1]) > 0)
                x[0].swap(x[1]);

            return 2;
        } else if (d == 0) {
            x[0].set(-b / (2.0 * a));

            return 1;
        }

        return 0;
    }

    public static int cubic(double a, double b, double c, double d, MutableDouble[] x) {
        if (a == 0)
            return quadratic(b, c, d, x);

        if (a != 1.0) {
            b /= a;
            c /= a;
            d /= a;
        }

        double oneThird = 0.33333333333333333333;

        double p = c - b * b * oneThird;
        double q = b * (b * b / 13.5 - c * oneThird) + d;

        double s0 = -b * oneThird;

        if (p == 0 && q == 0)
            x[0].set(s0);
        else {
            double sqrtD = p * p * p / 27.0 + 0.25 * q * q;

            Complex u = sqrtD < 0 ? new Complex(-0.5 * q, sqrt(abs(sqrtD))).pow(oneThird)
                    : new Complex(cbrt(sqrt(sqrtD) - 0.5 * q));
            Complex v = u.inverse().multiply(-oneThird * p);

            double s1 = u.getReal() + v.getReal();

            x[0].set(s0 + s1);

            if (sqrtD <= 0) {
                Complex w = u.subtract(v).multiply(new Complex(0, sqrt(3)));

                x[1].set(s0 + 0.5 * (w.getReal() - s1));

                if (x[0].compareTo(x[1]) > 0)
                    x[0].swap(x[1]);

                if (sqrtD == 0)
                    return 2;

                x[2].set(s0 - 0.5 * (w.getReal() + s1));

                if (x[1].compareTo(x[2]) > 0)
                    x[1].swap(x[2]);

                if (x[0].compareTo(x[1]) > 0)
                    x[0].swap(x[1]);

                return 3;
            }
        }

        return 1;
    }

    public static double newton(double a, double b, double c, double d, double x0,
            int maxi, double eps) {
        for (int i = 0; i < maxi; i++) {
            double y = x0 * (x0 * (x0 * a + b) + c) + d;
            double dy = x0 * (3.0 * x0 * a + 2.0 * b) + c;

            if (dy == 0)
                return x0;

            double x1 = x0 - y / dy;

            if (abs(x1 - x0) < eps)
                return x0;

            x0 = x1;
        }

        return x0;
    }
}