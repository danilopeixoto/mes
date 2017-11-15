// Copyright (c) 2017, Danilo Peixoto. All rights reserved.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class DefaultSymbols {
    private Interpreter interpreter;

    private SymbolTable constants;
    private SymbolTable functions;

    public DefaultSymbols(Interpreter interpreter, Class container)
            throws IllegalArgumentException, IllegalAccessException {
        this.interpreter = interpreter;

        constants = new SymbolTable();
        functions = new SymbolTable();

        importDefaultSymbols(container);
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void load() {
        loadConstants();
        loadFunctions();
    }

    public void loadConstants() {
        interpreter.getSymbolTable().addAll(constants);
    }

    public void loadFunctions() {
        interpreter.getSymbolTable().addAll(functions);
    }

    private void importDefaultSymbols(Class container)
            throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = container.getDeclaredFields();
        Method[] methods = container.getDeclaredMethods();

        if (fields != null)
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                Class type = field.getType();

                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                        && Modifier.isFinal(modifiers) && (type == Number.class
                        || type == Boolean.class)) {
                    VariableLiteralSymbol constant = new VariableLiteralSymbol();
                    constant.setName(field.getName());
                    constant.setEmpty(false);

                    Object value = field.get(null);

                    if (value instanceof Number) {
                        Number number = (Number)value;
                        constant.setValue(number.doubleValue());
                    } else if (value instanceof Boolean) {
                        Boolean bool = (Boolean)value;
                        constant.setValue(bool.booleanValue() ? 1.0 : 0);
                    }

                    constants.add(constant);
                }
            }

        if (methods != null)
            for (Method method : methods) {
                int modifiers = method.getModifiers();
                Class type = method.getReturnType();

                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                        && (type == Number.class || type == Boolean.class)) {
                    SymbolTable arguments = new SymbolTable();

                    boolean invalidParameterFound = false;
                    Parameter[] parameters = method.getParameters();

                    if (parameters != null)
                        for (Parameter parameter : parameters) {
                            if (parameter.getType() != Number.class
                                    && parameter.getType() != Boolean.class) {
                                invalidParameterFound = true;
                                break;
                            }

                            VariableLiteralSymbol argument = new VariableLiteralSymbol();
                            argument.setName(parameter.getName());

                            arguments.add(argument);
                        }

                    if (invalidParameterFound)
                        continue;

                    AbstractSyntaxTree closure = new AbstractSyntaxTree();

                    FunctionLiteralSymbol function = new FunctionLiteralSymbol();
                    function.setName(method.getName());
                    function.setArguments(arguments);
                    function.setClosure(closure);
                    function.setEmpty(false);

                    functions.add(function);
                }
            }
    }
}