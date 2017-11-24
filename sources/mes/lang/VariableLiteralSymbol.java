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

public class VariableLiteralSymbol extends IdentifierLiteralSymbol {
    private double value;

    public VariableLiteralSymbol() {
        this("", 0, true, 0);
    }

    public VariableLiteralSymbol(String name, int position) {
        this(name, 0, true, position);
    }

    public VariableLiteralSymbol(String name, double value, int position) {
        this(name, value, false, position);
    }

    public VariableLiteralSymbol(String name, double value, boolean empty, int position) {
        super(name, empty, SymbolType.Variable, position);
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public boolean getBooleanValue() {
        return MathUtils.bool(getDoubleValue());
    }

    @Override
    public String getPrototype() {
        return String.format(value == 0 || (value >= 0.1 && value < 10)
                ? "%s: %.5f" : "%s: %1.5e", name, value);
    }
}