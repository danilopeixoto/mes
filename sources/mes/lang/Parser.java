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
import mes.lang.TokenStream.TokenIterator;

public abstract class Parser {
    public static AbstractSyntaxTree parse(TokenStream tokens) {
        TokenIterator token = tokens.iterator();
        return new AbstractSyntaxTree(parseStatement(token));
    }

    private static Symbol parseStatement(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();

        Symbol statementSymbol;

        if ((statementSymbol = parseDefinition(tokenIterator)) != null);
        else if ((statementSymbol = parseExpression(tokenIterator)) != null);
        else
            invalidExpression(token.getPosition());

        token = tokenIterator.get();

        if (token.getType() != TokenType.EOL)
            expectedElement("end of line", token.getPosition());

        return statementSymbol;
    }

    private static Symbol parseDefinition(TokenIterator tokenIterator) {
        Symbol leftSymbol = parseIdentifier(tokenIterator);

        if (leftSymbol == null)
            return null;

        Token token = tokenIterator.get();

        if (token.getType() == TokenType.Assignment)
            tokenIterator.next();
        else
            return null;

        AssignmentOperatorSymbol assignmentSymbol
                = new AssignmentOperatorSymbol(token.getPosition());

        Symbol rightSymbol = parseExpression(tokenIterator);
        token = tokenIterator.get();

        if (rightSymbol == null)
            expectedElement("expression", token.getPosition());

        assignmentSymbol.setLeft(leftSymbol);
        assignmentSymbol.setRight(rightSymbol);

        return assignmentSymbol;
    }

    private static Symbol parseExpression(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();

        Symbol leftTermSymbol;

        if ((leftTermSymbol = parseTerm(tokenIterator)) != null) {
            token = tokenIterator.get();
            TokenType type = token.getType();

            if (isEqualityOperator(type)) {
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
                    expectedElement("literal", token.getPosition());
            }
        } else
            invalidExpression(token.getPosition());

        return leftTermSymbol;
    }

    private static Symbol parseTerm(TokenIterator token) {
        return null;
    }

    private static Symbol parseFactor(TokenIterator token) {
        return null;
    }

    private static Symbol parseFunctionCall(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();

        if (token.getType() == TokenType.Identifier)
            tokenIterator.next();
        else
            return null;

        FunctionLiteralSymbol functionSymbol
                = new FunctionLiteralSymbol(token.getValue(), token.getPosition());

        token = tokenIterator.get();

        if (token.getType() == TokenType.LParenthesis)
            tokenIterator.next();
        else
            return null;

        SymbolTable arguments;

        if ((arguments = parseFunctionCallArguments(tokenIterator)) != null)
            functionSymbol.setArguments(arguments);

        token = tokenIterator.get();

        if (token.getType() == TokenType.RParenthesis)
            tokenIterator.next();
        else
            expectedElement("right parenthesis", token.getPosition());

        return functionSymbol;
    }

    private static Symbol parseFunctionPrototype(TokenIterator token) {
        return null;
    }

    private static SymbolTable parseFunctionCallArguments(TokenIterator tokenIterator) {
        return null;
    }

    private static Symbol parseFunctionPrototypeArguments(TokenIterator tokenIterator) {
        return null;
    }

    private static Symbol parseIdentifier(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();

        Symbol identifierSymbol;

        if ((identifierSymbol = parseFunctionPrototype(tokenIterator)) != null);
        else if (token.getType() == TokenType.Identifier) {
            identifierSymbol = new VariableLiteralSymbol(
                    token.getValue(), token.getPosition());
            tokenIterator.next();
        } else
            identifierSymbol = null;

        return identifierSymbol;
    }

    private static Symbol parseLiteral(TokenIterator tokenIterator) {
        Token token = tokenIterator.get();

        Symbol literalSymbol;

        if ((literalSymbol = parseFunctionCall(tokenIterator)) != null);
        else if ((literalSymbol = parseExpression(tokenIterator)) != null);
        else
            switch (token.getType()) {
                case Identifier:
                    literalSymbol = new VariableLiteralSymbol(
                            token.getValue(), token.getPosition());

                    tokenIterator.next();
                    break;
                case Number:
                    literalSymbol = new NumberLiteralSymbol(
                            Double.parseDouble(token.getValue()), token.getPosition());

                    tokenIterator.next();
                    break;
                default:
                    literalSymbol = null;
            }

        return literalSymbol;
    }

    private static boolean isEqualityOperator(TokenType type) {
        return type == TokenType.And || type == TokenType.Or;
    }

    private static void invalidExpression(int position) {
        throw new ExceptionContent(ExceptionMessage.InvalidExpression, position);
    }

    private static void expectedElement(String element, int position) {
        throw new ExceptionContent(ExceptionMessage.expect(element), position);
    }

    private static void unexpectedElement(String element, int position) {
        throw new ExceptionContent(ExceptionMessage.unexpect(element), position);
    }
}