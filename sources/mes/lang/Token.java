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

/**
 * Represents the source code components of the language specification.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see OperatorData
 */
public class Token {
    /**
     * Supported token types.
     */
    public enum TokenType {
        /**
         * Identifier token.
         */
        Identifier,
        /**
         * Number token.
         */
        Number,
        /**
         * Comment token.
         */
        Comment,
        /**
         * Positive or addition operator token.
         */
        Plus,
        /**
         * Negative or subtraction operator token.
         */
        Minus,
        /**
         * Multiplication operator token.
         */
        Multiplication,
        /**
         * Division operator token.
         */
        Division,
        /**
         * Modulo operator token.
         */
        Modulo,
        /**
         * Exponentiation operator token.
         */
        Exponentiation,
        /**
         * Less equal operator token.
         */
        LessEqual,
        /**
         * Less operator token.
         */
        Less,
        /**
         * Greater equal operator token.
         */
        GreaterEqual,
        /**
         * Greater operator token.
         */
        Greater,
        /**
         * Equal operator token.
         */
        Equal,
        /**
         * Not equal operator token.
         */
        NotEqual,
        /** <i>Not</i> operator token.
         */
        Not,
        /** <i>And</i> operator token.
         */
        And,
        /** <i>Or</i> operator token.
         */
        Or,
        /**
         * Assignment operator token.
         */
        Assignment,
        /**
         * Left parenthesis token.
         */
        LParenthesis,
        /**
         * Right parenthesis token.
         */
        RParenthesis,
        /**
         * Comma token.
         */
        Comma,
        /**
         * End of line token.
         */
        EOL
    }

    private TokenType type;
    private String value;
    private OperatorData unaryOperatorData;
    private OperatorData binaryOperatorData;
    private int position;

    private Token(TokenType type, int position) {
        this(type, "", position);
    }

    private Token(TokenType type, String value, int position) {
        this(type, value, null, null, position);
    }

    private Token(TokenType type, OperatorData unaryOperatorData,
            OperatorData binaryOperatorData, int position) {
        this(type, "", unaryOperatorData, binaryOperatorData, position);
    }

    private Token(TokenType type, String value, OperatorData unaryOperatorData,
            OperatorData binaryOperatorData, int position) {
        this.type = type;
        this.value = value;
        this.unaryOperatorData = unaryOperatorData;
        this.binaryOperatorData = binaryOperatorData;
        this.position = position;
    }

    public static Token createIdentifier(String value, int position) {
        return new Token(TokenType.Identifier, value, position);
    }

    public static Token createNumber(String value, int position) {
        return new Token(TokenType.Number, value, position);
    }

    public static Token createComment(String value, int position) {
        return new Token(TokenType.Comment, value, position);
    }

    public static Token createOperator(TokenType type, OperatorData unaryOperatorData,
            OperatorData binaryOperatorData, int position) {
        return new Token(type, unaryOperatorData, binaryOperatorData, position);
    }

    public static Token createStructure(TokenType type, int position) {
        return new Token(type, position);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public OperatorData getUnaryOperatorData() {
        return unaryOperatorData;
    }

    public OperatorData getBinaryOperatorData() {
        return binaryOperatorData;
    }

    public int getPosition() {
        return position;
    }

    public boolean isNumber() {
        return type == TokenType.Number;
    }

    public boolean isIdentifier() {
        return type == TokenType.Identifier;
    }

    public boolean isOperator() {
        return isUnaryOperator() || isBinaryOperator();
    }

    public boolean isUnaryOperator() {
        return unaryOperatorData != null;
    }

    public boolean isBinaryOperator() {
        return binaryOperatorData != null;
    }

    public boolean isLanguageStructure() {
        return type == TokenType.LParenthesis || type == TokenType.RParenthesis
                || type == TokenType.Comma || type == TokenType.EOL;
    }
}