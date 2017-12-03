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

import mes.lang.ExceptionContent.ExceptionMessage;
import mes.lang.Symbol.SymbolType;

/**
 * Interpreter implementation for the language specification.
 * @author Danilo Ferreira, João de Oliveira and Lucas Alves
 * @version 1.0.0
 * @see Lexer
 * @see Parser
 * @see TraversalFunction
 */
public class Interpreter {
    private class ExpressionEvaluation extends TraversalFunction {
        public ExpressionEvaluation() {
            super();
        }

        @Override
        public AbstractSyntaxNode traverse(AbstractSyntaxNode node) {
            if (node == null)
                return null;
            
            Symbol root = (Symbol)node;
            Symbol left, right;
            
            if (root.getType() == SymbolType.Assignment) {
                left = (Symbol)root.getLeft();
                
                if (left.isIdentifierLiteral()) {
                    OperatorSymbol assignmentOperator = (OperatorSymbol)root;
                    IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)left;
                    
                    if (isDefaultSymbol(identifierSymbol))
                        throw new ExceptionContent(ExceptionMessage.InvalidSymbolRedefinition,
                                identifierSymbol.getPosition());
                    
                    right = (Symbol)root.getRight();
                    
                    identifierSymbol = (IdentifierLiteralSymbol)
                            assignmentOperator.evaluate(left, right);
                    identifierSymbol.evaluate(symbolTable);
                    
                    return identifierSymbol;
                }
                else
                    throw new ExceptionContent(ExceptionMessage.IllegalExpressionAssignment,
                        root.getPosition());
            }
            
            left = (Symbol)traverse(root.getLeft());
            right = (Symbol)traverse(root.getRight());
            
            if (root.isNumberLiteral())
                return root;
            else if (root.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)root;
                identifierSymbol.evaluate(symbolTable);
                
                return identifierSymbol.getNumberLiteralSymbol();
            }
            
            OperatorSymbol operatorSymbol = (OperatorSymbol)root;
            return operatorSymbol.evaluate(left, right);
        }
    }
    
    private SymbolTable userSymbolTable;
    private SymbolTable symbolTable;

    private SymbolImporter defaultSymbols;

    public Interpreter() {
        userSymbolTable = new SymbolTable();
        symbolTable = new SymbolTable();

        defaultSymbols = SymbolImporter.importFrom(MathUtils.class);
        transferDefaultSymbols();
    }

    public Statement run(String source) {
        return run(source, false);
    }

    public Statement run(String source, boolean typeChecking) {
        LiteralSymbol result;
        ExceptionContent exceptionContent;

        try {
            Lexer lexer = new Lexer(source);
            Parser parser = new Parser(lexer);
            
            AbstractSyntaxTree abstractSyntaxTree = parser.getAbstractSyntaxTree();
            result = (LiteralSymbol)abstractSyntaxTree.traverse(new ExpressionEvaluation());
            
            if (result == null)
                result = new NumberLiteralSymbol();
            
            exceptionContent = null;
            
            if (!typeChecking && result.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)result;
                identifierSymbol.setDocumentation(parser.getComment());
                
                updateUserSymbol(identifierSymbol);
            }
        } catch (Exception exception) {
            result = null;

            if (exception instanceof ExceptionContent)
                exceptionContent = (ExceptionContent)exception;
            else
                exceptionContent = new ExceptionContent(ExceptionMessage.throwable(exception));
        }

        return new Statement(result, exceptionContent);
    }

    public void setUserSymbolTable(SymbolTable userSymbolTable) {
        this.userSymbolTable = userSymbolTable;

        transferDefaultSymbols();
        symbolTable.addAll(userSymbolTable);
    }

    public SymbolTable getUserSymbolTable() {
        return userSymbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void clearUserSymbolTable() {
        userSymbolTable.clear();
        transferDefaultSymbols();
    }

    public boolean hasDefaultSymbols() {
        return defaultSymbols != null;
    }

    private void transferDefaultSymbols() {
        symbolTable.clear();

        if (defaultSymbols != null) {
            symbolTable.addAll(defaultSymbols.getConstants());
            symbolTable.addAll(defaultSymbols.getFunctions());
        }
    }

    private boolean isDefaultSymbol(IdentifierLiteralSymbol identifierSymbol) {
        if (!hasDefaultSymbols())
            return false;
        
        for (LiteralSymbol literalSymbol: defaultSymbols.getConstants())
            if (literalSymbol.isIdentifierLiteral()
                    && identifierSymbol.equals(literalSymbol))
                return true;

        for (LiteralSymbol literalSymbol: defaultSymbols.getFunctions())
            if (literalSymbol.isIdentifierLiteral()
                    && identifierSymbol.equals(literalSymbol))
                return true;
        
        return false;
    }

    private void updateUserSymbol(IdentifierLiteralSymbol userSymbol) {
        userSymbolTable.remove(userSymbol);
        symbolTable.remove(userSymbol);
        
        userSymbolTable.add(userSymbol);
        symbolTable.add(userSymbol);
    }
}