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
import mes.lang.Token.TokenType;

public abstract class Lexer {
    public static TokenStream tokenize(String source) {
        TokenStream tokens = new TokenStream();

        for (int i = 0; i < source.length(); i++) {
            String s = source.substring(i);
            char c = s.charAt(0);

            int endIndex;

            if (Character.isWhitespace(c))
                continue;
            else if ((endIndex = isIdentifier(s)) != 0) {
                tokens.add(new Token(TokenType.Identifier, s.substring(0, endIndex), i));
                i += endIndex - 1;
            } else if ((endIndex = isNumber(s)) != 0) {
                tokens.add(new Token(TokenType.Number, source.substring(0, endIndex), i));
                i += endIndex - 1;
            } else if (s.startsWith("=="))
                tokens.add(new Token(TokenType.Equal, i++));
            else if (s.startsWith("!="))
                tokens.add(new Token(TokenType.NotEqual, i++));
            else if (s.startsWith("<="))
                tokens.add(new Token(TokenType.LessEqual, i++));
            else if (s.startsWith(">="))
                tokens.add(new Token(TokenType.GreaterEqual, i++));
            else if (s.startsWith("&&"))
                tokens.add(new Token(TokenType.And, i++));
            else if (s.startsWith("||"))
                tokens.add(new Token(TokenType.Or, i++));
            else
                switch (c) {
                    case '+':
                        tokens.add(new Token(TokenType.Addition, i));
                        break;
                    case '-':
                        tokens.add(new Token(TokenType.Subtraction, i));
                        break;
                    case '*':
                        tokens.add(new Token(TokenType.Multiplication, i));
                        break;
                    case '/':
                        tokens.add(new Token(TokenType.Division, i));
                        break;
                    case '%':
                        tokens.add(new Token(TokenType.Modulo, i));
                        break;
                    case '^':
                        tokens.add(new Token(TokenType.Exponentiation, i));
                        break;
                    case '<':
                        tokens.add(new Token(TokenType.Less, i));
                        break;
                    case '>':
                        tokens.add(new Token(TokenType.Greater, i));
                        break;
                    case '!':
                        tokens.add(new Token(TokenType.Not, i));
                        break;
                    case '=':
                        tokens.add(new Token(TokenType.Assignment, i));
                        break;
                    case ',':
                        tokens.add(new Token(TokenType.Comma, i));
                        break;
                    case '(':
                        tokens.add(new Token(TokenType.LParenthesis, i));
                        break;
                    case ')':
                        tokens.add(new Token(TokenType.RParenthesis, i));
                        break;
                    default:
                        throw new ExceptionContent(ExceptionMessage.UnknownToken, i);
                }
        }

        if (tokens.isEmpty())
            throw new ExceptionContent(ExceptionMessage.InvalidExpression, 0);

        tokens.add(new Token(TokenType.EOL, source.length()));

        return tokens;
    }

    private static int isIdentifier(String source) {
        int i = 0;
        char c = source.charAt(i);

        if (c == '_' || Character.isAlphabetic(c))
            while (++i < source.length()) {
                c = source.charAt(i);

                if (c != '_' && !Character.isDigit(c) && !Character.isAlphabetic(c))
                    break;
            }

        return i;
    }

    private static int isNumber(String source) {
        int i = 0;
        char c = source.charAt(i);

        if (!Character.isDigit(c))
            return i;

        while (i < source.length() && Character.isDigit(c))
            c = source.charAt(++i);

        if (i < source.length() - 1 && c == '.') {
            c = source.charAt(++i);

            if (!Character.isDigit(c))
                return i - 1;

            while (i < source.length() && Character.isDigit(c))
                c = source.charAt(++i);
        }

        if (i < source.length() - 1 && c == 'e') {
            c = source.charAt(++i);

            if (i < source.length() - 1 && (c == '+' || c == '-')) {
                c = source.charAt(++i);

                if (!Character.isDigit(c))
                    return i - 2;
            }

            if (!Character.isDigit(c))
                return i - 1;

            while (i < source.length() && Character.isDigit(c))
                c = source.charAt(++i);
        }

        return i;
    }
}