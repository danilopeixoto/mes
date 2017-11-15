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

public class Complex {
    public static final Complex zero = new Complex();

    private double real, imaginary;

    public Complex() {
        this(0, 0);
    }

    public Complex(double real) {
        this(real, 0);
    }

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public static Complex polar(double radius, double angle) {
        return new Complex(radius * MathUtils.cos(angle), radius * MathUtils.sin(angle));
    }

    public void setReal(double real) {
        this.real = real;
    }

    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public Complex add(Complex other) {
        return new Complex(real + other.getReal(), imaginary + other.getImaginary());
    }

    public Complex subtract(Complex other) {
        return new Complex(real - other.getReal(), imaginary - other.getImaginary());
    }

    public Complex multiply(double other) {
        return new Complex(real * other, imaginary * other);
    }

    public Complex multiply(Complex other) {
        return new Complex(real * other.getReal() - imaginary * other.getImaginary(),
                real * other.getImaginary() + imaginary * other.getReal());
    }

    public Complex divide(double other) {
        return new Complex(real / other, imaginary / other);
    }

    public Complex divide(Complex other) {
        return multiply(other.inverse());
    }

    public Complex pow(double other) {
        return pow(new Complex(other));
    }

    public Complex pow(Complex other) {
        if (other.getImaginary() == 0) {
            if (other.getReal() == 0)
                return equals(zero) ? new Complex(1.0) : new Complex(MathUtils.UNDEFINED);
            else if (other.getReal() == 1.0)
                return new Complex(real, imaginary);
            else if (imaginary == 0 && (real >= 0 || MathUtils.frac(other.getReal()) == 0))
                return new Complex(MathUtils.pow(real, other.getReal()));

            return polar(MathUtils.pow(magnitude(), other.getReal()),
                    other.getReal() * argument());
        }

        double a = argument();
        double s = real * real + imaginary * imaginary;

        return polar(MathUtils.pow(s, 0.5 * other.getReal()) * MathUtils.exp(-a * other.getImaginary()),
                a * other.getReal() + 0.5 * other.getImaginary() * MathUtils.log(s));
    }

    public double dot(Complex other) {
        return real * other.getReal() + imaginary * other.getImaginary();
    }

    public double magnitude() {
        return MathUtils.sqrt(dot(this));
    }

    public double argument() {
        return MathUtils.atan(imaginary, real);
    }

    public Complex normalized() {
        return divide(magnitude());
    }

    public Complex conjugate() {
        return new Complex(real, -imaginary);
    }

    public Complex inverse() {
        return conjugate().divide(dot(this));
    }

    public boolean equals(Complex other) {
        return real == other.getReal() && imaginary == other.getImaginary();
    }
}