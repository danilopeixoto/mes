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

public abstract class Symbol extends AbstractSyntaxNode {
    public enum SymbolType {
        Number,
        Variable,
        Function,
        Plus,
        Minus,
        Addition,
        Subtraction,
        Multiplication,
        Division,
        Modulo,
        Exponentiation,
        GreaterEqual,
        Greater,
        LessEqual,
        Less,
        Equal,
        NotEqual,
        Not,
        And,
        Or,
        Assignment
    }

    protected SymbolType type;
    protected int position;

    public Symbol(SymbolType type, int position) {
        super();
        this.type = type;
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SymbolType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }
    
    public boolean isLiteral() {
        return this instanceof LiteralSymbol;
    }
    
    public boolean isNumberLiteral() {
        return this instanceof NumberLiteralSymbol;
    }
    
    public boolean isIdentifierLiteral() {
        return this instanceof IdentifierLiteralSymbol;
    }
    
    public boolean isOperator() {
        return this instanceof OperatorSymbol;
    }
    
    public boolean isUnaryOperator() {
        return this instanceof UnaryOperatorSymbol;
    }
    
    public boolean isBinaryOperator() {
        return this instanceof UnaryOperatorSymbol;
    }
}