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
 * Parser to assembly an abstract syntax tree from a {@link Lexer}.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see Lexer
 * @see AbstractSyntaxTree
 */
public class Parser {
    private String comment;

    private TokenStream.Iterator currentTokenIterator;
    private AbstractSyntaxTree abstractSyntaxTree;

    /**
     * Initializes the parser by assembling an abstract syntax tree from the
     * token stream returned by a lexer object.
     * @param lexer The lexer object
     * @throws ExceptionContent Invalid syntax thrown to {@link Interpreter}
     * @see Interpreter#run(String, boolean)
     * @see Lexer
     * @see AbstractSyntaxTree
     */
    public Parser(Lexer lexer) {
        comment = "";

        TokenStream tokenStream = lexer.getTokenStream();

        currentTokenIterator = tokenStream.iterator();
        abstractSyntaxTree = new AbstractSyntaxTree(parseStatement());
    }

    /**
     * Returns the comment of the statement.
     * @return The comment string.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns an abstract syntax tree representing the token stream.
     * @return An abstract syntax tree.
     * @see AbstractSyntaxTree
     */
    public AbstractSyntaxTree getAbstractSyntaxTree() {
        return abstractSyntaxTree;
    }

    private Symbol parseStatement() {
        Symbol statementSymbol = parseExpression();

        if (expect(TokenType.Comment)) {
            Token currentToken = current();
            comment = currentToken.getValue();

            next();
        }

        if (expect(TokenType.EOL))
            next();
        else
            invalidExpression();

        return statementSymbol;
    }

    private Symbol parseExpression() {
        return parseExpression(0);
    }

    private Symbol parseExpression(int minimumPrecendence) {
        Symbol expressionSymbol = parseLiteral();
        Token currentToken = current();

        if (expressionSymbol == null || !currentToken.isBinaryOperator())
            return expressionSymbol;

        OperatorData binaryOperatorData = currentToken.getBinaryOperatorData();
        int precedence = binaryOperatorData.getPrecedence();

        while (currentToken.isBinaryOperator()
                && precedence >= minimumPrecendence) {
            Associativity associativity = binaryOperatorData.getAssociativity();

            if (associativity == Associativity.Left)
                precedence++;

            Symbol binaryOperator = parseBinaryOperator();
            binaryOperator.setLeft(expressionSymbol);

            expressionSymbol = parseExpression(precedence);

            if (expressionSymbol != null) {
                binaryOperator.setRight(expressionSymbol);

                expressionSymbol = binaryOperator;
                currentToken = current();
            } else
                expectedElement("a literal or expression after binary operator");
        }

        return expressionSymbol;
    }

    private Symbol parseUnaryOperator() {
        Token currentToken = current();
        Symbol unaryOperator;

        switch (currentToken.getType()) {
            case Plus:
                unaryOperator = new PositiveOperatorSymbol(currentToken.getPosition());
                break;
            case Minus:
                unaryOperator = new NegativeOperatorSymbol(currentToken.getPosition());
                break;
            default:
                unaryOperator = new NotOperatorSymbol(currentToken.getPosition());
        }

        next();
        return unaryOperator;
    }

    private Symbol parseBinaryOperator() {
        Token currentToken = current();
        Symbol binaryOperator;

        switch (currentToken.getType()) {
            case Plus:
                binaryOperator = new AdditionOperatorSymbol(currentToken.getPosition());
                break;
            case Minus:
                binaryOperator = new SubtractionOperatorSymbol(currentToken.getPosition());
                break;
            case Multiplication:
                binaryOperator = new MultiplicationOperatorSymbol(currentToken.getPosition());
                break;
            case Division:
                binaryOperator = new DivisionOperatorSymbol(currentToken.getPosition());
                break;
            case Modulo:
                binaryOperator = new ModuloOperatorSymbol(currentToken.getPosition());
                break;
            case Exponentiation:
                binaryOperator = new ExponentiationOperatorSymbol(currentToken.getPosition());
                break;
            case LessEqual:
                binaryOperator = new LessEqualOperatorSymbol(currentToken.getPosition());
                break;
            case Less:
                binaryOperator = new LessOperatorSymbol(currentToken.getPosition());
                break;
            case GreaterEqual:
                binaryOperator = new GreaterEqualOperatorSymbol(currentToken.getPosition());
                break;
            case Greater:
                binaryOperator = new GreaterOperatorSymbol(currentToken.getPosition());
                break;
            case Equal:
                binaryOperator = new EqualOperatorSymbol(currentToken.getPosition());
                break;
            case NotEqual:
                binaryOperator = new NotEqualOperatorSymbol(currentToken.getPosition());
                break;
            case And:
                binaryOperator = new AndOperatorSymbol(currentToken.getPosition());
                break;
            case Or:
                binaryOperator = new OrOperatorSymbol(currentToken.getPosition());
                break;
            default:
                binaryOperator = new AssignmentOperatorSymbol(currentToken.getPosition());
        }

        next();
        return binaryOperator;
    }

    private Symbol parseLiteral() {
        Token currentToken = current();
        Symbol literalSymbol = parseNumber();

        if (literalSymbol != null)
            return literalSymbol;
        else if ((literalSymbol = parseIdentifier()) != null)
            return literalSymbol;
        else if (currentToken.isUnaryOperator()) {
            OperatorData unaryOperatorData = currentToken.getUnaryOperatorData();

            Symbol unaryOperator = parseUnaryOperator();
            literalSymbol = parseExpression(unaryOperatorData.getPrecedence());

            if (literalSymbol != null) {
                unaryOperator.setLeft(literalSymbol);
                return unaryOperator;
            } else
                expectedElement("a literal or expression after unary operator");
        } else if (expect(TokenType.LParenthesis)) {
            next();
            literalSymbol = parseExpression();

            if (literalSymbol != null)
                if (expect(TokenType.RParenthesis)) {
                    next();
                    return literalSymbol;
                } else
                    expectedElement("a right parenthesis \")\" after literal or expression");
            else
                expectedElement("a literal or expression after left parenthesis \"(\"");
        }

        return null;
    }

    private Symbol parseIdentifier() {
        Symbol identifierSymbol = null;

        if (expect(TokenType.Identifier)) {
            Token identifierToken = current();
            next();

            if (expect(TokenType.LParenthesis)) {
                next();

                FunctionLiteralSymbol functionSymbol = new FunctionLiteralSymbol(
                        identifierToken.getValue(), identifierToken.getPosition());
                functionSymbol.setArguments(parseFunctionArguments());

                if (expect(TokenType.RParenthesis)) {
                    identifierSymbol = functionSymbol;
                    next();
                } else
                    expectedElement("a right parenthesis \")\" after function arguments");
            } else
                identifierSymbol = new VariableLiteralSymbol(
                        identifierToken.getValue(), identifierToken.getPosition());
        }

        return identifierSymbol;
    }

    private Symbol parseNumber() {
        if (expect(TokenType.Number)) {
            Token currentToken = current();

            double numberValue = Double.valueOf(currentToken.getValue());
            NumberLiteralSymbol numberSymbol = new NumberLiteralSymbol(
                    numberValue, currentToken.getPosition());

            next();
            return numberSymbol;
        }

        return null;
    }

    private FunctionArgumentList parseFunctionArguments() {
        FunctionArgumentList functionArgumentList = new FunctionArgumentList();
        Symbol expressionSymbol = parseExpression();

        while (expressionSymbol != null) {
            FunctionArgument functionArgument = new FunctionArgument(expressionSymbol);
            functionArgumentList.add(functionArgument);

            if (expect(TokenType.Comma)) {
                next();
                expressionSymbol = parseExpression();

                if (expressionSymbol == null)
                    expectedElement("a literal or expression as function argument");
            } else
                break;
        }

        return functionArgumentList;
    }

    private void expectedElement(String expectedElement) {
        Token currentToken = current();

        throw new ExceptionContent(ExceptionMessage.expect(expectedElement),
                currentToken.getPosition());
    }

    private void invalidExpression() {
        Token currentToken = current();

        throw new ExceptionContent(ExceptionMessage.InvalidExpression,
                currentToken.getPosition());
    }

    private boolean expect(TokenType type) {
        Token currentToken = current();
        return currentToken.getType() == type;
    }

    private Token next() {
        currentTokenIterator.next();
        return current();
    }

    private Token current() {
        return currentTokenIterator.get();
    }
}