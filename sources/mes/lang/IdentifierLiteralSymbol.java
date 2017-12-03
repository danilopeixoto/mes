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

import java.io.Serializable;
import mes.lang.ExceptionContent.ExceptionMessage;

/**
 * Identifier type abstraction.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see LiteralSymbol
 * @see Comparable
 */
public abstract class IdentifierLiteralSymbol extends LiteralSymbol
        implements Serializable, Comparable<IdentifierLiteralSymbol> {
    protected class LiteralEvaluation extends TraversalFunction {
        public LiteralEvaluation(SymbolTable globalSymbolTable) {
            super(globalSymbolTable);
        }

        @Override
        public AbstractSyntaxNode traverse(AbstractSyntaxNode node) {
            if (node == null)
                return null;
            
            Symbol root = (Symbol)node;
            
            LiteralSymbol left = (LiteralSymbol)traverse(root.getLeft());
            LiteralSymbol right = (LiteralSymbol)traverse(root.getRight());
            
            if (root.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)root;
                
                SymbolTable globalSymbolTable = (SymbolTable)arguments[0];
                identifierSymbol.evaluate(globalSymbolTable);
                
                return identifierSymbol.getNumberLiteralSymbol();
            }
            else if (root.getType() == SymbolType.Number)
                return root;
            else if (root.getType() == SymbolType.Assignment)
                throw new ExceptionContent(ExceptionMessage.IllegalExpressionAssignment,
                        root.getPosition());
            
            OperatorSymbol operatorSymbol = (OperatorSymbol)root;
            return operatorSymbol.evaluate(left, right);
        }
    }
    
    protected String name;
    protected Closure closure;
    
    protected String documentation;
    
    public IdentifierLiteralSymbol(String name, double doubleValue,
            SymbolType type, int position) {
        super(doubleValue, type, position);
        this.name = name;
        
        closure = new Closure();
        documentation = "";
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setClosure(Closure closure) {
        this.closure = closure;
    }
    
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getName() {
        return name;
    }

    public Closure getClosure() {
        return closure;
    }
    
    public String getDocumentation() {
        return documentation;
    }
    
    public NumberLiteralSymbol getNumberLiteralSymbol() {
        return new NumberLiteralSymbol(value, position);
    }
    
    public abstract void evaluate(SymbolTable globalSymbolTable);
    
    public abstract void precompile(SymbolTable globalSymbolTable);

    public abstract String getPrototype();
    
    @Override
    public boolean equals(Object other) {
        IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)other;
        
        if (type != identifierSymbol.getType() || !name.equals(identifierSymbol.getName()))
            return false;
        
        if (type == SymbolType.Function) {
            FunctionLiteralSymbol functionSymbol = (FunctionLiteralSymbol)this;
            FunctionLiteralSymbol otherFunctionSymbol = (FunctionLiteralSymbol)other;

            if (functionSymbol.getArguments().size()
                    != otherFunctionSymbol.getArguments().size())
                return false;
        }
        
        return true;
    }
    
    @Override
    public int compareTo(IdentifierLiteralSymbol other) {
        if (type != other.getType())
            return other.getType() != SymbolType.Variable ? -1 : 1;
        else if (!name.equals(other.getName())) {
            return name.compareTo(other.getName());
        } else if (type == SymbolType.Function) {
            FunctionLiteralSymbol functionSymbol = (FunctionLiteralSymbol)this;
            FunctionLiteralSymbol otherFunctionSymbol = (FunctionLiteralSymbol)other;
            
            int argumentCount = functionSymbol.getArguments().size();
            int otherArgumentCount = otherFunctionSymbol.getArguments().size();

            if (argumentCount != otherArgumentCount)
                return argumentCount < otherArgumentCount ? -1 : 1;
        }
        
        return 0;
    }
}