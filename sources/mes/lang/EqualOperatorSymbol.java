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

/**
 * Equal operator abstraction.
 * @author Danilo Ferreira, João de Oliveira and Lucas Alves
 * @version 1.0.0
 * @see BinaryOperatorSymbol
 */
public class EqualOperatorSymbol extends BinaryOperatorSymbol {
    /** Initializes the equal operator. */
    public EqualOperatorSymbol() {
        this(0);
    }

    /**
     * Initializes the equal operator. By default the equal operator is
     * left associative with precedence 3.
     * @param position The symbol position at the source code
     * @see Lexer#Lexer(String)
     * @see OperatorData
     */
    public EqualOperatorSymbol(int position) {
        super(SymbolType.Equal, position);
    }

    /** {@inheritDoc} */
    @Override
    public LiteralSymbol compute(LiteralSymbol left, LiteralSymbol right) {
        return new NumberLiteralSymbol(MathUtils.number(
                left.getDoubleValue() == right.getDoubleValue()), position);
    }
}