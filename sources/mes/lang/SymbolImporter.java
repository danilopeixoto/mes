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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Class to import symbols from the native language into the MES
 * language specification.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see Interpreter
 * @see MathUtils
 */
public class SymbolImporter {
    private SymbolTable constants;
    private SymbolTable functions;

    private SymbolImporter() {
        constants = new SymbolTable();
        functions = new SymbolTable();
    }

    public SymbolTable getConstants() {
        return constants;
    }

    public SymbolTable getFunctions() {
        return functions;
    }

    public static SymbolImporter importFrom(Class container) {
        try {
            SymbolImporter instance = new SymbolImporter();
            instance.importSymbols(container);

            return instance;
        } catch (Exception exception) {
            return null;
        }
    }

    private void importSymbols(Class container) throws Exception {
        Field[] fields = container.getDeclaredFields();
        Method[] methods = container.getDeclaredMethods();

        if (fields != null)
            for (Field field : fields)
                if (field.isAnnotationPresent(ExportSymbol.class)) {
                    VariableLiteralSymbol constant = new VariableLiteralSymbol();
                    Object value = field.get(null);

                    if (value instanceof Number) {
                        Number number = (Number)value;
                        constant.setValue(number.doubleValue());
                    } else if (value instanceof Boolean) {
                        Boolean bool = (Boolean)value;
                        constant.setValue(MathUtils.number(bool));
                    }

                    constant.setName(field.getName());
                    constants.add(constant);
                }

        if (methods != null)
            for (Method method : methods)
                if (method.isAnnotationPresent(ExportSymbol.class)) {
                    FunctionArgumentList arguments = new FunctionArgumentList();
                    Parameter[] parameters = method.getParameters();

                    if (parameters != null)
                        for (Parameter parameter : parameters) {
                            VariableLiteralSymbol variableSymbol = new VariableLiteralSymbol();
                            variableSymbol.setName(parameter.getName());

                            arguments.add(new FunctionArgument(variableSymbol));
                        }

                    FunctionLiteralSymbol function = new FunctionLiteralSymbol();
                    function.setName(method.getName());
                    function.setArguments(arguments);
                    function.setClosure(new Closure(method));

                    functions.add(function);
                }
    }
}