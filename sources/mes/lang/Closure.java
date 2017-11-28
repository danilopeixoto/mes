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

import java.lang.reflect.Method;

/**
 * Adapter class for {@link FunctionLiteralSymbol} closure.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see AbstractSyntaxTree
 * @see Method
 */
public class Closure {
    /** Supported closure types. */
    public enum ClosureType {
        /** Abstract syntax tree closure. */
        AbstractSyntaxTree,
        /** Method closure. */
        Method,
        /** Empty closure. */
        Empty
    }
    
    private Object closureObject;

    /** Initializes an empty closure. */
    public Closure() {
        closureObject = null;
    }

    /**
     * Initializes a closure instance as abstract syntax tree. If the abstract
     * syntax tree is null an empty closure is assigned. 
     * @param abstractSyntaxTree The abstract syntax tree to use as closure
     */
    public Closure(AbstractSyntaxTree abstractSyntaxTree) {
        closureObject = abstractSyntaxTree;
    }

    /**
     * Initializes a closure instance as method. If the method is null an empty
     * closure is assigned.
     * @param method The method to use as closure
     */
    public Closure(Method method) {
        closureObject = method;
    }

    /**
     * Sets closure instance as abstract syntax tree. If the abstract syntax
     * tree is null an empty closure is assigned.
     * @param abstractSyntaxTree The abstract syntax tree to use as closure
     */
    public void setAbstractSyntaxTree(AbstractSyntaxTree abstractSyntaxTree) {
        closureObject = abstractSyntaxTree;
    }

    /**
     * Sets a closure instance as method. If the method is null an empty closure
     * is assigned.
     * @param method The method to use as closure
     */
    public void setMethod(Method method) {
        closureObject = method;
    }

    /**
     * Returns an abstract syntax tree closure. The type of closure must be
     * known to avoid type cast exception.
     * @return An abstract syntax tree closure.
     * @see #setAbstractSyntaxTree(AbstractSyntaxTree)
     * @see #getType()
     */
    public AbstractSyntaxTree getAbstractSyntaxTree() {
        return (AbstractSyntaxTree)closureObject;
    }

    /**
     * Returns a method closure. The type of closure must be known to avoid
     * type cast exception.
     * @return A method closure.
     * @see #setMethod(Method)
     * @see #getType()
     */
    public Method getMethod() {
        return (Method)closureObject;
    }
    
    /**
     * Returns the closure type of this instance.
     * @return The closure type.
     * @see ClosureType
     */
    public ClosureType getType() {
        if (closureObject instanceof AbstractSyntaxTree)
            return ClosureType.AbstractSyntaxTree;
        else if (closureObject instanceof Method)
            return ClosureType.Method;

        return ClosureType.Empty;
    }
}