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
 * Not operator abstraction.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see UnaryOperatorSymbol
 */
public class NotOperatorSymbol extends UnaryOperatorSymbol {
    /** Initializes the <i>not</i> operator. By default the position index is zero. */
    public NotOperatorSymbol() {
        this(0);
    }

    /**
     * Initializes the <i>not</i> operator.
     * @param position The symbol position at the source code
     * @see UnaryOperatorSymbol#UnaryOperatorSymbol(Symbol.SymbolType, int)
     */
    public NotOperatorSymbol(int position) {
        super(SymbolType.Not, position);
    }

    /** {@inheritDoc} */
    @Override
    public LiteralSymbol evaluate() {
        LiteralSymbol inputOperand = (LiteralSymbol)left;
        return new NumberLiteralSymbol(!inputOperand.getBooleanValue(), position);
    }
}