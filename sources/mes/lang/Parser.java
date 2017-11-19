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

import mes.lang.ExceptionContent.ExceptionMessage;
import mes.lang.Token.TokenType;
import mes.lang.TokenStream.TokenIterator;

public abstract class Parser {
    public static AbstractSyntaxTree parse(TokenStream tokens) {
        TokenIterator token = tokens.iterator();
        return new AbstractSyntaxTree(parseStatement(token));
    }

    public static Symbol parseStatement(TokenIterator token) {
        Symbol symbol;

        if ((symbol = parseExpression(token)) != null) {

        } else if (symbol = parseDefinition(token) != null) {

        } else
            throw ExceptionContent(ExceptionMessage.InvalidExpression, 0);

    }

    public static Symbol parseDefinition(TokenIterator token) {

    }

    public static Symbol parseDeclaration(TokenIterator token) {
    }

    public static Symbol parseExpression(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();
        TokenType type = token.getType();

        checkUnexpectedEOL(token);

        Symbol leftTermSymbol;

        if ((leftTermSymbol = parseTerm(tokenIterator)) != null) {
            tokenIterator.next();

            token = tokenIterator.get();
            type = token.getType();

            try {
                checkUnexpectedEOL(token);
            } catch (ExceptionContent exception) {
                return leftTermSymbol;
            }

            if (isBooleanBinaryOperator(type)) {
                Symbol rightTermSymbol, operatorSymbol;

                if (type == TokenType.And)
                    operatorSymbol = new AndOperatorSymbol(token.getPosition());
                else
                    operatorSymbol = new OrOperatorSymbol(token.getPosition());

                tokenIterator.next();
                token = tokenIterator.get();

                if ((rightTermSymbol = parseTerm(tokenIterator)) != null) {
                    operatorSymbol.setLeft(leftTermSymbol);
                    operatorSymbol.setRight(rightTermSymbol);

                    tokenIterator.next();

                    return operatorSymbol;
                } else
                    expectedLiteral(token);
            }
        } else
            throw new ExceptionContent(ExceptionMessage.InvalidExpression,
                    token.getPosition());

        return leftTermSymbol;
    }

    public static Symbol parseTerm(TokenIterator token) {
    }

    public static Symbol parseFactor(TokenIterator token) {
    }

    public static Symbol parseFunctionCall(TokenIterator token) {
    }

    public static Symbol parseFunctionPrototype(TokenIterator token) {
    }

    public static Symbol parseFunctionCallArguments(TokenIterator token) {
    }

    public static Symbol parseFunctionPrototypeArguments(TokenIterator token) {
    }

    public static Symbol parseLiteral(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();
        TokenType type = token.getType();

        checkUnexpectedEOL(token);

        Symbol literalSymbol;

        if ((literalSymbol = parseFunctionCall(tokenIterator)) != null)
            tokenIterator.next();
        else if ((literalSymbol = parseExpression(tokenIterator)) != null)
            tokenIterator.next();
        else {
            switch (type) {
                case Identifier:
                    literalSymbol = new VariableLiteralSymbol(
                            token.getValue(), token.getPosition());
                    break;
                case Number:
                    literalSymbol = new NumberLiteralSymbol(
                            Double.parseDouble(token.getValue()), token.getPosition());
                    break;
                default:
                    expectedLiteral(token);
            }

            tokenIterator.next();
        }

        return literalSymbol;
    }

    private static boolean isBooleanBinaryOperator(TokenType type) {
        return type == TokenType.And || type == TokenType.Or;
    }

    private static void expectedLiteral(Token token) {
        throw new ExceptionContent(ExceptionMessage.unexpect("end of line"),
                token.getPosition());
    }

    private static void checkUnexpectedEOL(Token token) {
        if (token.getType() == TokenType.EOL)
            throw new ExceptionContent(ExceptionMessage.unexpect("end of line"),
                    token.getPosition());
    }
}