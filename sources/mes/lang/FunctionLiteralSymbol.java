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
import java.util.stream.Stream;

public class FunctionLiteralSymbol extends IdentifierLiteralSymbol {
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

    private FunctionArgumentList arguments;
    private Closure closure;

    public FunctionLiteralSymbol() {
        this("", true, 0);
    }

    public FunctionLiteralSymbol(String name, int position) {
        this(name, true, position);
    }

    public FunctionLiteralSymbol(String name, boolean nullIdentifier, int position) {
        super(name, nullIdentifier, SymbolType.Function, position);

        arguments = new FunctionArgumentList();
        closure = new Closure();
    }

    public void setArguments(FunctionArgumentList arguments) {
        this.arguments = arguments;
    }

    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    public FunctionArgumentList getArguments() {
        return arguments;
    }

    public Closure getClosure() {
        return closure;
    }

    @Override
    public double getDoubleValue() {
        double value;

        switch (closure.getType()) {
            case AbstractSyntaxTree:
                AbstractSyntaxTree abstractSyntaxTree = closure.getAbstractSyntaxTree();

                NumberLiteralSymbol result = (NumberLiteralSymbol)abstractSyntaxTree.traverse(
                        new EvaluationFunction());
                value = result.getDoubleValue();

                break;
            case Method:
                try {
                    Stream<Object> parameters = arguments.stream().map(this::mapArguments);

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
                    value = 0;
                }

                break;
            default:
                value = 0;
        }

        return value;
    }

    @Override
    public boolean getBooleanValue() {
        return MathUtils.bool(getDoubleValue());
    }

    @Override
    public String getPrototype() {
        if (!isNullIdentifier())
            return "";

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

    private Object mapArguments(FunctionArgument argument) {
        LiteralSymbol literalSymbol = (LiteralSymbol)argument.getRoot();
        return literalSymbol.getDoubleValue();
    }
}