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

import java.io.Serializable;

/**
 * Abstract syntax tree implementation for {@link Symbol}.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see AbstractSyntaxNode
 */
public class AbstractSyntaxTree implements Serializable {
    private AbstractSyntaxNode root;

    /**
     * Initializes the abstract syntax tree with a null root.
     */
    public AbstractSyntaxTree() {
        this(null);
    }

    /**
     * Initializes the abstract syntax tree.
     * @param root The root node
     */
    public AbstractSyntaxTree(AbstractSyntaxNode root) {
        this.root = root;
    }

    /**
     * Sets the root node of the abstract syntax tree.
     * @param root The root node
     */
    public void setRoot(AbstractSyntaxNode root) {
        this.root = root;
    }

    /**
     * Returns the root node of the abstract syntax tree.
     * @return The root node.
     * @see #setRoot(AbstractSyntaxNode)
     */
    public AbstractSyntaxNode getRoot() {
        return root;
    }

    /**
     * Returns true if the abstract syntax tree is empty and false otherwise.
     * @return The empty state.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Deletes all the nodes in the abstract syntax tree.
     */
    public void clear() {
        root = null;
    }

    /**
     * Traverses the abstract syntax tree using a custom traversal function.
     * @param traversalFunction A custom traversal function
     * @return The output node.
     * @see TraversalFunction
     */
    public AbstractSyntaxNode traverse(TraversalFunction traversalFunction) {
        return traversalFunction.traverse(root);
    }
}