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

public class ExceptionContent extends RuntimeException {
    public interface ExceptionMessage {
        public static final String InvalidExpression = "invalid expression";
        public static final String UnknownToken = "unknown token";
        public static final String UndefinedSymbol = "undefined symbol";
        public static final String InvalidSymbolRedefinition = "invalid symbol redefinition";
        public static final String InvalidNumberArguments = "invalid number of arguments";

        public static String custom(String message) {
            return message;
        }

        public static String expect(String element) {
            return custom("expected " + element + "symbol");
        }

        public static String unexpect(String element) {
            return custom("unexpected " + element + "symbol");
        }

        public static String throwable(Exception exceptionObject) {
            String exceptionName;

            try {
                exceptionName = exceptionObject.getClass().getSimpleName();
                String[] words = exceptionName.split("(?=[A-Z])");

                StringBuilder stringBuilder = new StringBuilder();
                int length = words.length - 1;

                for (int i = 0; i < length; i++) {
                    String word = words[i].toLowerCase();
                    stringBuilder.append(word);

                    if (i != length - 1)
                        stringBuilder.append(' ');
                }

                exceptionName = stringBuilder.toString();
            } catch (Exception exception) {
                exceptionName = "unknown exception";
            }
            
            return exceptionName;
        }
    }

    private String message;
    private int position;

    public ExceptionContent(String message) {
        this(message, 0);
    }

    public ExceptionContent(String message, int position) {
        this.message = message;
        this.position = position;
    }

    @Override
    public String getMessage() {
        return String.format("Error: %s at column %d.", message, position + 1);
    }

    public int getPosition() {
        return position;
    }
}