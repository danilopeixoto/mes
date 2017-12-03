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
 * Assignment operator abstraction.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see BinaryOperatorSymbol
 */
public class AssignmentOperatorSymbol extends BinaryOperatorSymbol {
    /** Initializes the assignment operator. */
    public AssignmentOperatorSymbol() {
        this(0);
    }

    /**
     * Initializes the assignment operator. By default the assignment operator
     * is right associative with precedence 0.
     * @param position The symbol position at the source code
     * @see Lexer#Lexer(String)
     * @see OperatorData
     */
    public AssignmentOperatorSymbol(int position) {
        super(SymbolType.Assignment, position);
    }

    /** {@inheritDoc} */
    @Override
    public LiteralSymbol evaluate(Symbol left, Symbol right) {
        AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree(right);
        Closure closure = new Closure(abstractSyntaxTree);
        
        IdentifierLiteralSymbol identifierSymbol;
        
        if (left.getType() == SymbolType.Variable) {
            VariableLiteralSymbol variableSymbol = (VariableLiteralSymbol)left;
            
            identifierSymbol = new VariableLiteralSymbol(
                    variableSymbol.getName(), position);
        }
        else {
            FunctionLiteralSymbol functionSymbol = (FunctionLiteralSymbol)left;
            FunctionArgumentList functionArguments = functionSymbol.getArguments();
            
            identifierSymbol = new FunctionLiteralSymbol(
                    functionSymbol.getName(), functionArguments, position);
        }
        
        identifierSymbol.setClosure(closure);
        return identifierSymbol;
    }
}