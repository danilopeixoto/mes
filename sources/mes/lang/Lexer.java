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
import mes.lang.OperatorData.Associativity;
import mes.lang.Token.TokenType;

/**
 * Lexer to tokenize the source code.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see Parser
 */
public class Lexer {
    private TokenStream tokenStream;

    /**
     * Initializes the lexer by tokenizing the source code. An <i>end of
     * line</i> token is attached to the end of the stream.
     * @param source The source code
     * @throws ExceptionContent Invalid token or expression thrown to
     * {@link Interpreter}
     * @see Interpreter#run(String, boolean)
     * @see OperatorData
     */
    public Lexer(String source) {
        tokenStream = new TokenStream();

        for (int i = 0; i < source.length(); i++) {
            String s = source.substring(i);
            char c = s.charAt(0);

            int endIndex;

            if (Character.isWhitespace(c))
                continue;
            else if ((endIndex = isIdentifier(s)) != 0) {
                tokenStream.add(Token.createIdentifier(s.substring(0, endIndex), i));
                i += endIndex - 1;
            } else if ((endIndex = isNumber(s)) != 0) {
                tokenStream.add(Token.createNumber(s.substring(0, endIndex), i));
                i += endIndex - 1;
            } else if (s.startsWith("<="))
                tokenStream.add(Token.createOperator(TokenType.LessEqual,
                        null, new OperatorData(4, Associativity.Left), i++));
            else if (s.startsWith(">="))
                tokenStream.add(Token.createOperator(TokenType.GreaterEqual,
                        null, new OperatorData(4, Associativity.Left), i++));
            else if (s.startsWith("=="))
                tokenStream.add(Token.createOperator(TokenType.Equal,
                        null, new OperatorData(3, Associativity.Left), i++));
            else if (s.startsWith("!="))
                tokenStream.add(Token.createOperator(TokenType.NotEqual,
                        null, new OperatorData(3, Associativity.Left), i++));
            else if (s.startsWith("&&"))
                tokenStream.add(Token.createOperator(TokenType.And,
                        null, new OperatorData(2, Associativity.Left), i++));
            else if (s.startsWith("||"))
                tokenStream.add(Token.createOperator(TokenType.Or,
                        null, new OperatorData(1, Associativity.Left), i++));
            else
                switch (c) {
                    case '+':
                        tokenStream.add(Token.createOperator(TokenType.Plus,
                                 new OperatorData(7, Associativity.Right),
                                 new OperatorData(5, Associativity.Left), i));
                        break;
                    case '-':
                        tokenStream.add(Token.createOperator(TokenType.Minus,
                                new OperatorData(7, Associativity.Right),
                                new OperatorData(5, Associativity.Left), i));
                        break;
                    case '*':
                        tokenStream.add(Token.createOperator(TokenType.Multiplication,
                                null, new OperatorData(6, Associativity.Left), i));
                        break;
                    case '/':
                        tokenStream.add(Token.createOperator(TokenType.Division,
                                null, new OperatorData(6, Associativity.Left), i));
                        break;
                    case '%':
                        tokenStream.add(Token.createOperator(TokenType.Modulo,
                                null, new OperatorData(6, Associativity.Left), i));
                        break;
                    case '^':
                        tokenStream.add(Token.createOperator(TokenType.Exponentiation,
                                null, new OperatorData(8, Associativity.Right), i));
                        break;
                    case '<':
                        tokenStream.add(Token.createOperator(TokenType.Less,
                                null, new OperatorData(4, Associativity.Left), i));
                        break;
                    case '>':
                        tokenStream.add(Token.createOperator(TokenType.Greater,
                                null, new OperatorData(4, Associativity.Left), i));
                        break;
                    case '!':
                        tokenStream.add(Token.createOperator(TokenType.Not,
                                new OperatorData(7, Associativity.Right), null, i));
                        break;
                    case '=':
                        tokenStream.add(Token.createOperator(TokenType.Assignment,
                                null, new OperatorData(0, Associativity.Right), i));
                        break;
                    case ',':
                        tokenStream.add(Token.createStructure(TokenType.Comma, i));
                        break;
                    case '(':
                        tokenStream.add(Token.createStructure(TokenType.LParenthesis, i));
                        break;
                    case ')':
                        tokenStream.add(Token.createStructure(TokenType.RParenthesis, i));
                        break;
                    default:
                        throw new ExceptionContent(ExceptionMessage.UnknownToken, i);
                }
        }
        
        tokenStream.add(Token.createStructure(TokenType.EOL, source.length()));
    }

    /**
     * Returns a token stream representing the source code.
     * @return A token stream.
     * @see TokenStream
     */
    public TokenStream getTokenStream() {
        return tokenStream;
    }

    private int isIdentifier(String source) {
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

    private int isNumber(String source) {
        int i = 0;
        char c = source.charAt(i);

        if (!Character.isDigit(c))
            return i;
        
        while (Character.isDigit(c))
            c = ++i < source.length() ? source.charAt(i) : '\0';

        if (i < source.length() - 1 && c == '.') {
            c = source.charAt(++i);

            if (!Character.isDigit(c))
                return i - 1;
            
            while (Character.isDigit(c))
                c = ++i < source.length() ? source.charAt(i) : '\0';
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
            
            while (Character.isDigit(c))
                c = ++i < source.length() ? source.charAt(i) : '\0';
        }

        return i;
    }
}