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

import mes.lang.Closure.ClosureType;
import mes.lang.ExceptionContent.ExceptionMessage;

/**
 * Variable type representation.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see IdentifierLiteralSymbol
 */
public class VariableLiteralSymbol extends IdentifierLiteralSymbol {
    public VariableLiteralSymbol() {
        this("", 0);
    }

    public VariableLiteralSymbol(String name, int position) {
        this(name, 0, position);
    }

    public VariableLiteralSymbol(String name, boolean booleanValue, int position) {
        super(name, MathUtils.number(booleanValue), SymbolType.Variable, position);
    }

    public VariableLiteralSymbol(String name, double doubleValue, int position) {
        super(name, doubleValue, SymbolType.Variable, position);
    }

    @Override
    public void evaluate(SymbolTable globalSymbolTable) {
        if (closure.getType() == ClosureType.AbstractSyntaxTree) {
            AbstractSyntaxTree abstractSyntaxTree = closure.getAbstractSyntaxTree();

            LiteralSymbol literalSymbol = (LiteralSymbol)abstractSyntaxTree.traverse(
                    new LiteralEvaluation(globalSymbolTable));

            value = literalSymbol.getDoubleValue();
            closure.setEmpty();
        } else {
            if (!globalSymbolTable.contains(this))
                throw new ExceptionContent(ExceptionMessage.UndefinedSymbol, position);

            for (IdentifierLiteralSymbol identifierSymbol : globalSymbolTable)
                if (identifierSymbol.equals(this)) {
                    value = identifierSymbol.getDoubleValue();
                    return;
                }
        }
    }

    @Override
    public void precompile(SymbolTable globalSymbolTable) {
        if (!globalSymbolTable.contains(this))
            throw new ExceptionContent(ExceptionMessage.UndefinedSymbol, position);

        closure.setEmpty();
    }

    @Override
    public String getPrototype() {
        return String.format("%s: %s", name, getFormatedValue());
    }
}