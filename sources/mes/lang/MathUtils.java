// Copyright (c) 2017, Danilo Ferreira, João de Oliveira and Lucas Alves.
// All rights reserved.
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

/**
 * Mathematical constants and functions.
 * @author Danilo Ferreira, João de Oliveira and Lucas Alves
 * @version 1.0.0
 */
public abstract class MathUtils {
    private static final Random random = new Random();

    /** The Euler real constant: 2.7182818284590452354. */
    @ExportSymbol("The Euler constant.")
    public static final double E = Math.E;

    /** The pi real constant: 3.14159265358979323846. */
    @ExportSymbol("The pi constant.")
    public static final double PI = Math.PI;

    /** The epsilon real value: 2.2204460492503131e-016. */
    @ExportSymbol("The epsilon number.")
    public static final double EPSILON = Math.ulp(1.0);

    /** The largest real value: 1.7976931348623158e+308. */
    @ExportSymbol("The largest number.")
    public static final double LARGEST = Double.MAX_VALUE;

    /** The infinity constant: Infinity. */
    @ExportSymbol("The infinity constant.")
    public static final double INFINITY = Double.POSITIVE_INFINITY;

    /** The undefined constant: NaN. */
    @ExportSymbol("The undefined constant.")
    public static final double UNDEFINED = Double.NaN;

    /** The false real constant: 0. */
    @ExportSymbol("The false constant.")
    public static final double FALSE = 0;

    /** The true real constant: 1.0. */
    @ExportSymbol("The true constant.")
    public static final double TRUE = 1.0;

    /**
     * Returns the absolute value of a given number.
     * @param x Real value
     * @return The absolute value.
     */
    @ExportSymbol("Returns the absolute value of a number.")
    public static double abs(double x) {
        return Math.abs(x);
    }

    /**
     * Returns the fractional part of a given number.
     * @param x Real value
     * @return The fractional part value.
     */
    @ExportSymbol("Returns the fractional part of a number.")
    public static double frac(double x) {
        return x - floor(x);
    }

    /**
     * Returns the given value rounded to the next lowest integer.
     * @param x Real value
     * @return The rounded value.
     */
    @ExportSymbol("Returns a number rounded to the next lowest integer.")
    public static double floor(double x) {
        return Math.floor(x);
    }

    /**
     * Returns the smallest integer greater than or equal to the given real
     * value.
     * @param x Real value
     * @return The rounded value.
     */
    @ExportSymbol("Returns the smallest integer greater than or equal to a number.")
    public static double ceil(double x) {
        return Math.ceil(x);
    }

    /**
     * Returns the given value rounded to the nearest integer.
     * @param x Real value
     * @return The rounded value.
     */
    @ExportSymbol("Returns a number rounded to the nearest integer.")
    public static double round(double x) {
        return Math.round(x);
    }

    /**
     * Returns the integer part of a given number.
     * @param x Real value
     * @return The rounded value.
     */
    @ExportSymbol("Returns the integer part of a number.")
    public static double trunc(double x) {
        return x < 0 ? ceil(x) : floor(x);
    }

    /**
     * Returns the sign of the given value, indicating whether the number is
     * negative, positive or zero.
     * @param x Real value
     * @return A negative, positive or zero real value
     * (-1.0, 1.0 or 0 respectively).
     */
    @ExportSymbol("Returns the sign value of a number.")
    public static double sign(double x) {
        return Math.signum(x);
    }

     /**
     * Returns a value with the magnitude of <i>a</i> and the sign of <i>b</i>.
     * @param a Real value
     * @param b Real value
     * @return The magnitude of the <i>a</i> with the sign of <i>b</i>.
     */
    @ExportSymbol("Returns a number with the magnitude of \"a\" and the sign of \"b\".")
    public static double copysign(double a, double b) {
        return Math.copySign(a, b);
    }

     /**
     * Clamps the given number between <i>a</i> and <i>b</i> and returns value.
     * @param x Real value
     * @param a Minimum real value
     * @param b Maximum real value
     * @return A real value between <i>a</i> and <i>b</i>.
     */
    @ExportSymbol("Clamps a number between \"a\" and \"b\".")
    public static double clamp(double x, double a, double b) {
        return max(a, min(b, x));
    }

    /**
     * Remaps the given value from one range to another.
     * @param x Real value
     * @param a0 Minimum real value of the input range
     * @param b0 Maximum real value of the input range
     * @param a1 Minimum real value of the output range
     * @param b1 Maximum real value of the output range
     * @return A real value in the output range.
     */
    @ExportSymbol("Remaps a number from one range to another.")
    public static double remap(double x, double a0, double b0, double a1, double b1) {
        return a1 + (x - a0) * (b1 - a1) / (b0 - a0);
    }
    
    /**
     * Returns the exponentiation value to the base <i>a</i> and exponent <i>b</i>.
     * @param a Base value
     * @param b Exponent value
     * @return The exponentiation value.
     */
    @ExportSymbol("Returns the exponentiation value")
    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    /**
     * Returns the square root of the given real value.
     * @param x Real value
     * @return The square root value.
     */
    @ExportSymbol("Returns the square root of a number.")
    public static double sqrt(double x) {
        return Math.sqrt(x);
    }

    /**
     * Returns the cubic root of the given real value.
     * @param x Real value
     * @return The cubic root value.
     */
    @ExportSymbol("Returns the cubic root of a number.")
    public static double cbrt(double x) {
        return Math.cbrt(x);
    }

    /**
     * Returns the Euler constant to the power of the given value.
     * @param x Real value
     * @return The exponentiation value.
     * @see E
     */
    @ExportSymbol("Returns the Euler constant to the power of a number.")
    public static double exp(double x) {
        return Math.exp(x);
    }

    /**
     * Returns the natural logarithm of the given value.
     * @param x Real value
     * @return The natural logarithm value.
     * @see E
     */
    @ExportSymbol("Returns the natural logarithm of a number.")
    public static double log(double x) {
        return Math.log(x);
    }

    /**
     * Returns the logarithm value to the antilogarithm <i>a</i> and base <i>b</i>.
     * @param a Antilogarithm value
     * @param b Base value
     * @return The logarithm value.
     */
    @ExportSymbol("Returns the logarithm value.")
    public static double log(double a, double b) {
        return log(a) / log(b);
    }
    
    /**
     * Returns the remainder of two real values after division.
     * @param a Real value
     * @param b Real value
     * @return The remainder value.
     */
    @ExportSymbol("Returns the remainder of two numbers after division.")
    public static double mod(double a, double b) {
        return a % b;
    }

    /**
     * Returns the error function of a given value.
     * @param x Real value
     * @return The error function value.
     */
    @ExportSymbol("Returns the error function of a number.")
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

    /**
     * Returns the hypotenuse value.
     * @param a Side value
     * @param b Side value
     * @return THe hypotenuse value.
     */
    @ExportSymbol("Returns the hypotenuse value.")
    public static double hypot(double a, double b) {
        return Math.hypot(a, b);
    }

    /**
     * Returns the linear interpolation between two real values.
     * @param x Real value in the input range
     * @param a Minimum value of the input range
     * @param b Maximum value of the input range
     * @return The linear interpolation between <i>a</i> and <i>b</i>.
     * @see #smoothstep(double, double, double)
     */
    @ExportSymbol("Returns the linear interpolation between two numbers.")
    public static double lerp(double x, double a, double b) {
        return a + x * (b - a);
    }

    /**
     * Returns the smooth interpolation between two real values.
     * @param x Real value in the input range
     * @param a Minimum value of the input range
     * @param b Maximum value of the input range
     * @return The smooth interpolation between <i>a</i> and <i>b</i>.
     * @see #lerp(double, double, double)
     */
    @ExportSymbol("Returns the smooth interpolation between two numbers.")
    public static double smoothstep(double x, double a, double b) {
        double t = clamp((x - a) / (b - a), 0, 1.0);
        return t * t * (3.0 - (t * 2.0));
    }

    /**
     * Returns the sine of an angle in radians.
     * @param x Angle in radians
     * @return The sine value.
     */
    @ExportSymbol("Returns the sine of an angle in radians.")
    public static double sin(double x) {
        return Math.sin(x);
    }

    /**
     * Returns the cosine of an angle in radians.
     * @param x Angle in radians
     * @return The cosine value.
     */
    @ExportSymbol("Returns the cosine of an angle in radians.")
    public static double cos(double x) {
        return Math.cos(x);
    }

    /**
     * Returns the tangent of an angle in radians.
     * @param x Angle in radians
     * @return The tangent value.
     */
    @ExportSymbol("Returns the tangent of an angle in radians.")
    public static double tan(double x) {
        return Math.tan(x);
    }

    /**
     * Returns the arc sine of a real value.
     * @param x Real value
     * @return An angle in radians.
     */
    @ExportSymbol("Returns the arc sine of a number.")
    public static double asin(double x) {
        return Math.asin(x);
    }

    /**
     * Returns the arc cosine of a real value.
     * @param x Real value
     * @return An angle in radians.
     */
    @ExportSymbol("Returns the arc cosine of a number.")
    public static double acos(double x) {
        return Math.acos(x);
    }

    /**
     * Returns the arc tangent of a real value.
     * @param x Real value
     * @return An angle in radians.
     */
    @ExportSymbol("Returns the arc tangent of a number.")
    public static double atan(double x) {
        return Math.atan(x);
    }

    /**
     * Returns the arc tangent (angle in radians) of the rectangular
     * coordinates (x, y).
     * @param a X component
     * @param b Y component
     * @return An angle in radians.
     */
    @ExportSymbol("Returns the arc tangent from rectangular coordinates.")
    public static double atan(double a, double b) {
        return Math.atan2(b, a);
    }

    /**
     * Returns the hyperbolic sine of an angle in radians.
     * @param x Angle in radians
     * @return The hyperbolic sine value.
     */
    @ExportSymbol("Returns the hyperbolic sine of an angle in radians.")
    public static double sinh(double x) {
        return Math.sinh(x);
    }

    /**
     * Returns the hyperbolic cosine of an angle in radians.
     * @param x Angle in radians
     * @return The hyperbolic cosine value.
     */
    @ExportSymbol("Returns the hyperbolic cosine of an angle in radians.")
    public static double cosh(double x) {
        return Math.cosh(x);
    }

    /**
     * Returns the hyperbolic tangent of an angle in radians.
     * @param x Angle in radians
     * @return The hyperbolic tangent value.
     */
    @ExportSymbol("Returns the hyperbolic tangent of an angle in radians.")
    public static double tanh(double x) {
        return Math.tanh(x);
    }

    /**
     * Converts an angle measured in radians to degrees.
     * @param x Angle in radians
     * @return The angle in degrees.
     */
    @ExportSymbol("Converts an angle measured in radians to degrees.")
    public static double degrees(double x) {
        return Math.toDegrees(x);
    }

    /**
     * Converts an angle measured in degrees to radians.
     * @param x Angle in degrees
     * @return The angle in radians.
     */
    @ExportSymbol("Converts an angle measured in degrees to radians.")
    public static double radians(double x) {
        return Math.toRadians(x);
    }

    /**
     * Converts a real value to boolean value. This method returns true if the
     * number is non-zero otherwise false.
     * @param x Real value
     * @return An boolean state.
     */
    @ExportSymbol("Converts a real value to boolean value.")
    public static boolean bool(double x) {
        return x != 0;
    }

    /**
     * Converts a boolean value to real value.
     * @param x Boolean state
     * @return The zero or one real value.
     */
    public static double number(boolean x) {
        return x ? 1.0 : 0;
    }
    
    /**
     * Returns the smaller of two real values.
     * @param a Real value
     * @param b Real value
     * @return The minimum value.
     */
    @ExportSymbol("Returns the smaller of two numbers.")
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    /**
     * Returns the greater of two real values.
     * @param a Real value
     * @param b Real value
     * @return The maximum value.
     */
    @ExportSymbol("Returns the greater of two numbers.")
    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    /**
     * Returns a pseudorandom real value between zero and one (exclusive).
     * @return A pseudorandom real value.
     */
    @ExportSymbol("Returns a pseudorandom number between zero and one (exclusive).")
    public static double rand() {
        return random.nextDouble();
    }

    /**
     * Returns a pseudorandom positive integer value.
     * @return A pseudorandom positive integer value.
     */
    @ExportSymbol("Returns a pseudorandom positive integer number.")
    public static int seed() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Returns true if the given value is a finite real value and false
     * otherwise.
     * @param x Real value
     * @return The finite real value state.
     */
    @ExportSymbol("Returns whether the number is a finite value.")
    public static boolean isfinite(double x) {
        return Double.isFinite(x);
    }

    /**
     * Returns true if the given value is equal to the negative or positive
     * infinity constant and false otherwise.
     * @param x Real value
     * @return The negative or positive infinity state.
     * @see INFINITY
     */
    @ExportSymbol("Returns whether the number is infinite.")
    public static boolean isinf(double x) {
        return Double.isInfinite(x);
    }

    /**
     * Returns true if the given value is undefined and false otherwise.
     * @param x Real value
     * @return The undefined state.
     * @see UNDEFINED
     */
    @ExportSymbol("Returns whether the number is undefined.")
    public static boolean isnan(double x) {
        return Double.isNaN(x);
    }

    /**
     * Returns true if the given value is even and false otherwise.
     * @param x Real value
     * @return The even state.
     */
    @ExportSymbol("Returns whether the number is even.")
    public static boolean iseven(double x) {
        return x % 2 == 0;
    }

    /**
     * Returns true if the given value is odd and false otherwise.
     * @param x Real value
     * @return The odd state.
     */
    @ExportSymbol("Returns whether the number is odd.")
    public static boolean isodd(double x) {
        return x % 2 != 0;
    }
    
    /**
     * Returns true if the real values are equal and false otherwise.
     * @param a Real value
     * @param b Real value
     * @return The equality state.
     */
    @ExportSymbol("Returns whether the numbers are equal.")
    public static boolean isequal(double a, double b) {
        return a == b;
    }
    
    /**
     * Computes the approximate root of a cubic equation using Newton-Raphson
     * algorithm. If the algorithm converges this method returns the approximate
     * root when the tolerance is reached.
     * @param a Third degree coefficient
     * @param b Second degree coefficient
     * @param c First degree coefficient
     * @param d Zero degree coefficient
     * @param x0 Initial value
     * @param eps Tolerance value
     * @return The approximate root of the cubic equation.
     */
    @ExportSymbol("Computes the approximate root of a cubic equation.")
    public static double newton(double a, double b, double c, double d,
            double x0, double eps) {
        while (true) {
            double y = x0 * (x0 * (x0 * a + b) + c) + d;
            double dy = x0 * (3.0 * x0 * a + 2.0 * b) + c;

            if (dy == 0)
                break;

            double x1 = x0 - y / dy;

            if (abs(x1 - x0) <= eps)
                break;

            x0 = x1;
        }
        
        return x0;
    }
}