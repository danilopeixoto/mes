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
    private class ClosureEvaluation extends TraversalFunction {
        public ClosureEvaluation(SymbolTable globalSymbolTable) {
            super(globalSymbolTable);
        }

        @Override
        public AbstractSyntaxNode traverse(AbstractSyntaxNode node) {
            if (node == null)
                return null;
            
            Symbol nodeSymbol = (Symbol)node;
            
            traverse(nodeSymbol.getLeft());
            traverse(nodeSymbol.getRight());
            
            if (nodeSymbol.isIdentifierLiteral()) {
                IdentifierLiteralSymbol identifierSymbol =
                        (IdentifierLiteralSymbol)nodeSymbol;
                
                SymbolTable globalSymbolTable = (SymbolTable)arguments[0];
                identifierSymbol.evaluate(globalSymbolTable);
            }
            
            if (nodeSymbol.getType() == SymbolType.Assignment)
                throw new ExceptionContent(ExceptionMessage.IllegalExpressionAssignment,
                        nodeSymbol.getPosition());
            
            return null;
        }
    }
    
    private FunctionArgumentList arguments;

    public FunctionLiteralSymbol() {
        this("", 0);
    }

    public FunctionLiteralSymbol(String name, int position) {
        super(name, 0, SymbolType.Function, position);
        arguments = new FunctionArgumentList();
    }

    public void setArguments(FunctionArgumentList arguments) {
        this.arguments = arguments;
    }

    public FunctionArgumentList getArguments() {
        return arguments;
    }
    
    @Override
    public void evaluate(SymbolTable globalSymbolTable) {
        if (closure.getType() == ClosureType.Empty){
            if (!globalSymbolTable.contains(this))
                throw new ExceptionContent(ExceptionMessage.UndefinedSymbol, position);
            
            FunctionArgumentList functionArgumentIdentifiers = null;

            for (IdentifierLiteralSymbol identifierSymbol: globalSymbolTable) {
                if (identifierSymbol.equals(this)) {
                    FunctionLiteralSymbol functionSymbol =
                            (FunctionLiteralSymbol)identifierSymbol;
                    
                    closure = functionSymbol.getClosure();
                    functionArgumentIdentifiers = functionSymbol.getArguments();
                    
                    break;
                }
            }
            
            for (FunctionArgument argument: arguments) {
                LiteralSymbol literalSymbol = (LiteralSymbol)argument.traverse(
                        new LiteralEvaluation(globalSymbolTable));
                
                argument.setRoot(literalSymbol);
            }
            
            if (closure.getType() == ClosureType.AbstractSyntaxTree) {
                for (int i = 0; i < arguments.size(); i++) {
                    FunctionArgument argument = arguments.get(i);
                    FunctionArgument argumentIdentifier = functionArgumentIdentifiers.get(i);
                    
                    NumberLiteralSymbol numberSymbol = (NumberLiteralSymbol)argument.getRoot();
                    VariableLiteralSymbol variableSymbol =
                            (VariableLiteralSymbol)argumentIdentifier.getRoot();
                    
                    argument.setRoot(new VariableLiteralSymbol(variableSymbol.getName(),
                            numberSymbol.getDoubleValue(), numberSymbol.getPosition()));
                }
                
                AbstractSyntaxTree abstractSyntaxTree = closure.getAbstractSyntaxTree();
                SymbolTable localSymbolTable = getLocalSymbolTable(globalSymbolTable);

                LiteralSymbol literalSymbol = (LiteralSymbol)abstractSyntaxTree.traverse(
                        new LiteralEvaluation(localSymbolTable));
                
                value = literalSymbol.getDoubleValue();
            }
            else {
                try {
                    Stream<Number> parameters = arguments.stream().map(this::mapArguments);
                    Method method = closure.getMethod();
                    
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
            }
        }
        else {
            ensurePrototype();
            
            AbstractSyntaxTree abstractSyntaxTree = closure.getAbstractSyntaxTree();
            SymbolTable localSymbolTable = getLocalSymbolTable(globalSymbolTable);
            
            abstractSyntaxTree.traverse(new ClosureEvaluation(localSymbolTable));
        }
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
        
        for (FunctionArgument argument: arguments) {
            Symbol argumentSymbol = (Symbol)argument.getRoot();
            
            if (argumentSymbol.isLeaf() && argumentSymbol.getType() == SymbolType.Variable) {
                VariableLiteralSymbol variableSymbol = (VariableLiteralSymbol)argumentSymbol;
                String variableName = variableSymbol.getName();
                
                if (argumentNames.add(variableName))
                    continue;
                
                throw new ExceptionContent(ExceptionMessage.InvalidArgumentRedefinition,
                            argumentSymbol.getPosition());
            }
            
            throw new ExceptionContent(ExceptionMessage.InvalidFunctionArgument,
                            argumentSymbol.getPosition());
        }
    }
    
    private SymbolTable getLocalSymbolTable(SymbolTable globalSymbolTable) {
        SymbolTable localSymbolTable = new SymbolTable();
        
        for (FunctionArgument argument: arguments)
            localSymbolTable.add((IdentifierLiteralSymbol)argument.getRoot());
        
        localSymbolTable.addAll(globalSymbolTable);
        
        return localSymbolTable;
    }

    private Number mapArguments(FunctionArgument argument) {
        LiteralSymbol literalSymbol = (LiteralSymbol)argument.getRoot();
        return literalSymbol.getDoubleValue();
    }
}