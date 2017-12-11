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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.stream.Stream;
import mes.lang.Closure.ClosureType;
import mes.lang.ExceptionContent.ExceptionMessage;

/**
 * Function type representation.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see IdentifierLiteralSymbol
 */
public class FunctionLiteralSymbol extends IdentifierLiteralSymbol {
    private class ClosurePrecompiler extends TraversalFunction {
        public ClosurePrecompiler(SymbolTable globalSymbolTable) {
            super(globalSymbolTable);
        }

        @Override
        public AbstractSyntaxNode traverse(AbstractSyntaxNode node) {
            if (node == null)
                return null;

            Symbol root = (Symbol)node;

            for (AbstractSyntaxNode child : root.getChildren())
                traverse(child);

            if (root.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol
                        = (IdentifierLiteralSymbol)root;

                SymbolTable globalSymbolTable = (SymbolTable)arguments[0];
                identifierSymbol.precompile(globalSymbolTable);
            }

            if (root.getType() == SymbolType.Assignment)
                throw new ExceptionContent(ExceptionMessage.IllegalExpressionAssignment,
                        root.getPosition());

            return null;
        }
    }

    private FunctionArgumentList arguments;

    public FunctionLiteralSymbol() {
        this("", 0);
    }

    public FunctionLiteralSymbol(String name, int position) {
        this(name, new FunctionArgumentList(), position);
    }

    public FunctionLiteralSymbol(String name,
            FunctionArgumentList arguments, int position) {
        super(name, 0, SymbolType.Function, position);
        this.arguments = arguments;
    }

    public void setArguments(FunctionArgumentList arguments) {
        this.arguments = arguments;
    }

    public FunctionArgumentList getArguments() {
        return arguments;
    }

    @Override
    public void evaluate(SymbolTable globalSymbolTable) {
        if (closure.getType() == ClosureType.Empty) {
            if (!globalSymbolTable.contains(this))
                throw new ExceptionContent(ExceptionMessage.UndefinedSymbol, position);

            FunctionArgumentList functionArgumentIdentifiers = null;
            Closure functionClosure = null;

            for (IdentifierLiteralSymbol identifierSymbol : globalSymbolTable)
                if (identifierSymbol.equals(this)) {
                    FunctionLiteralSymbol functionSymbol
                            = (FunctionLiteralSymbol)identifierSymbol;

                    functionArgumentIdentifiers = functionSymbol.getArguments();
                    functionClosure = functionSymbol.getClosure();

                    break;
                }

            SymbolTable functionArgumentSymbols = new SymbolTable();

            for (int i = 0; i < arguments.size(); i++) {
                FunctionArgument argument = arguments.get(i);
                FunctionArgument argumentIdentifier = functionArgumentIdentifiers.get(i);

                NumberLiteralSymbol numberSymbol = (NumberLiteralSymbol)argument.traverse(
                        new LiteralEvaluation(globalSymbolTable));
                VariableLiteralSymbol variableSymbol
                        = (VariableLiteralSymbol)argumentIdentifier.getRoot();

                functionArgumentSymbols.add(new VariableLiteralSymbol(variableSymbol.getName(),
                        numberSymbol.getDoubleValue(), numberSymbol.getPosition()));
            }

            if (functionClosure.getType() == ClosureType.AbstractSyntaxTree) {
                AbstractSyntaxTree abstractSyntaxTree = functionClosure.getAbstractSyntaxTree();
                SymbolTable localSymbolTable = computeLocalSymbolTable(
                        globalSymbolTable, functionArgumentSymbols);

                LiteralSymbol literalSymbol = (LiteralSymbol)abstractSyntaxTree.traverse(
                        new LiteralEvaluation(localSymbolTable));

                value = literalSymbol.getDoubleValue();
            } else
                try {
                    Stream<Number> parameters
                            = functionArgumentSymbols.stream().map(this::mapArguments);

                    Method method = functionClosure.getMethod();
                    Object output = method.invoke(null, parameters.toArray());

                    if (output instanceof Number) {
                        Number number = (Number)output;
                        value = number.doubleValue();
                    } else {
                        Boolean bool = (Boolean)output;
                        value = MathUtils.number(bool);
                    }
                } catch (Exception exception) {
                    throw new ExceptionContent(
                            ExceptionMessage.FunctionEvaluationFailed, position);
                }
        } else {
            ensurePrototype();

            SymbolTable functionArgumentSymbols = new SymbolTable();
            AbstractSyntaxTree abstractSyntaxTree = closure.getAbstractSyntaxTree();

            for (FunctionArgument argument : arguments) {
                VariableLiteralSymbol identifierSymbol
                        = (VariableLiteralSymbol)argument.getRoot();

                functionArgumentSymbols.add(identifierSymbol);
            }

            SymbolTable localSymbolTable = computeLocalSymbolTable(globalSymbolTable,
                    functionArgumentSymbols);

            abstractSyntaxTree.traverse(new ClosurePrecompiler(localSymbolTable));
        }
    }

    @Override
    public void precompile(SymbolTable globalSymbolTable) {
        if (!globalSymbolTable.contains(this))
            throw new ExceptionContent(ExceptionMessage.UndefinedSymbol, position);

        for (FunctionArgument argument : arguments)
            argument.traverse(new ClosurePrecompiler(globalSymbolTable));

        closure.setEmpty();
    }

    @Override
    public String getPrototype() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(name);
        stringBuilder.append('(');

        int argumentCount = arguments.size();

        for (int i = 0; i < argumentCount; i++) {
            FunctionArgument argument = arguments.get(i);
            IdentifierLiteralSymbol identifierSymbol
                    = (IdentifierLiteralSymbol)argument.getRoot();

            stringBuilder.append(identifierSymbol.getName());

            if (i != argumentCount - 1)
                stringBuilder.append(", ");
        }

        stringBuilder.append("): number");

        return stringBuilder.toString();
    }

    private void ensurePrototype() {
        HashSet<String> argumentNames = new HashSet<>();

        for (FunctionArgument argument : arguments) {
            Symbol argumentSymbol = (Symbol)argument.getRoot();

            if (argumentSymbol.isLeaf() && argumentSymbol.getType() == SymbolType.Variable) {
                VariableLiteralSymbol variableSymbol = (VariableLiteralSymbol)argumentSymbol;
                String variableName = variableSymbol.getName();

                if (argumentNames.add(variableName))
                    continue;

                throw new ExceptionContent(ExceptionMessage.InvalidArgumentRedefinition,
                        argumentSymbol.getPosition());
            }

            throw new ExceptionContent(ExceptionMessage.InvalidArgumentDefinition,
                    argumentSymbol.getPosition());
        }
    }

    private SymbolTable computeLocalSymbolTable(SymbolTable globalSymbolTable,
            SymbolTable functionArgumentSymbols) {
        SymbolTable localSymbolTable = new SymbolTable();

        localSymbolTable.addAll(functionArgumentSymbols);
        localSymbolTable.addAll(globalSymbolTable);

        return localSymbolTable;
    }

    private Number mapArguments(IdentifierLiteralSymbol argumentSymbol) {
        return argumentSymbol.getDoubleValue();
    }
}