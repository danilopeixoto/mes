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
 */
public class Token {
    /** Supported token types. */
    public enum TokenType {
        /** Identifier token. */
        Identifier,
        /** Number token. */
        Number,
        /** Addition operator token. */
        Addition,
        /** Subtraction operator token. */
        Subtraction,
        /** Multiplication operator token. */
        Multiplication,
        /** Division operator token. */
        Division,
        /** Modulo operator token. */
        Modulo,
        /** Exponentiation operator token. */
        Exponentiation,
        /** Equal operator token. */
        Equal,
        /** Not equal operator token. */
        NotEqual,
        /** Less equal operator token. */
        LessEqual,
        /** Less operator token. */
        Less,
        /** Greater equal operator token. */
        GreaterEqual,
        /** Greater operator token. */
        Greater,
        /** Not operator token. */
        Not,
        /** And operator token. */
        And,
        /** Or operator token. */
        Or,
        /** Assignment operator token. */
        Assignment,
        /** Left parenthesis token. */
        LParenthesis,
        /** Right parenthesis token. */
        RParenthesis,
        /** Comma token. */
        Comma,
        /** End of line token. */
        EOL
    }

    private TokenType type;
    private String value;
    private int position;

    public Token() {
    }

    public Token(TokenType type, int position) {
        this.type = type;
        this.position = position;
    }

    public Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }
}