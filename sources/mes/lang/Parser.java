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
            
        }
        else if (symbol = parseDefinition(token) != null) {
            
        }
        else
            throw ExceptionContent(ExceptionMessage.InvalidExpression, );
            
    }

    public static Symbol parseDefinition(TokenIterator token) {
    }

    public static Symbol parseDeclaration(TokenIterator token) {
    }

    public static Symbol parseExpression(TokenIterator token) {
        return null;
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

    public static Symbol parseLiteral(TokenIterator token) {
    }

    public static Token expect(TokenType type) {
    }

    public static void next(TokenIterator token) {
    }
}