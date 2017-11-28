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

/**
 * Interpreter implementation for the language specification.
 * @author Danilo Ferreira, João de Oliveira and Lucas Alves
 * @version 1.0.0
 * @see Lexer
 * @see Parser
 * @see TraversalFunction
 */
public class Interpreter {
    private class EvaluationFunction extends TraversalFunction {
        public EvaluationFunction() {
            this(null);
        }

        public EvaluationFunction(Object[] parameters) {
            super(parameters);
        }

        @Override
        public AbstractSyntaxNode evaluate(AbstractSyntaxNode node,
                AbstractSyntaxNode left, AbstractSyntaxNode right) {
            return null;
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
            TokenStream tokens = Lexer.tokenize(source);
            AbstractSyntaxTree abstractSyntaxTree = Parser.parse(tokens);

            result = (LiteralSymbol)abstractSyntaxTree.traverse(new EvaluationFunction());
            exceptionContent = null;

            if (result.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)result;

                if (isDefaultSymbol(identifierSymbol))
                    throw new ExceptionContent(
                            ExceptionMessage.InvalidSymbolRedefinition, result.getPosition());
                else
                    addUserSymbol(identifierSymbol);
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

        for (LiteralSymbol literalSymbol : defaultSymbols.getConstants())
            if (literalSymbol.isIdentifierLiteral()
                    && identifierSymbol.isRedefinitionOf((IdentifierLiteralSymbol)literalSymbol))
                return true;

        for (LiteralSymbol literalSymbol : defaultSymbols.getFunctions())
            if (literalSymbol.isIdentifierLiteral()
                    && identifierSymbol.isRedefinitionOf((IdentifierLiteralSymbol)literalSymbol))
                return true;

        return false;
    }

    private void addUserSymbol(IdentifierLiteralSymbol userSymbol) {
        userSymbolTable.add(userSymbol);
        symbolTable.add(userSymbol);
    }
}