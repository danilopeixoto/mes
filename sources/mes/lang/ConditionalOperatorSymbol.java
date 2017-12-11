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
 * Conditional operator representation.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see TernaryOperatorSymbol
 */
public class ConditionalOperatorSymbol extends TernaryOperatorSymbol {
    /**
     * Initializes the conditional operator.
     */
    public ConditionalOperatorSymbol() {
        this(0);
    }

    /**
     * Initializes the conditional operator. By default the conditional operator
     * is right associative with precedence 1.
     * @param position The symbol position at the source code
     * @see Lexer#Lexer(String)
     * @see OperatorData
     */
    public ConditionalOperatorSymbol(int position) {
        super(SymbolType.Conditional, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiteralSymbol evaluate(Symbol[] symbols) {
        LiteralSymbol conditionOperand = (LiteralSymbol)symbols[0];
        LiteralSymbol leftOperand = (LiteralSymbol)symbols[1];
        LiteralSymbol rightOperand = (LiteralSymbol)symbols[2];

        return new NumberLiteralSymbol(conditionOperand.getBooleanValue()
                ? leftOperand.getDoubleValue() : rightOperand.getDoubleValue(), position);
    }
}