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
 * Node implementation for {@link AbstractSyntaxTree}.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see AbstractSyntaxTree
 */
public abstract class AbstractSyntaxNode {
    protected AbstractSyntaxNode left;
    protected AbstractSyntaxNode right;

    /** Initializes the children of node to null. */
    public AbstractSyntaxNode() {
        left = null;
        right = null;
    }

    /**
     * Set the left child of node.
     * @param left The left child node
     */
    public void setLeft(AbstractSyntaxNode left) {
        this.left = left;
    }

    /**
     * Set the right child of node.
     * @param right The right child node
     */
    public void setRight(AbstractSyntaxNode right) {
        this.right = right;
    }

    /**
     * Returns the left child of node.
     * @return The left child node.
     * @see #setLeft(AbstractSyntaxNode)
     */
    public AbstractSyntaxNode getLeft() {
        return left;
    }

    /**
     * Returns the right child of node.
     * @return The right child node.
     * @see #setRight(AbstractSyntaxNode)
     */
    public AbstractSyntaxNode getRight() {
        return right;
    }

    /**
     * Returns true if all children are null and false otherwise.
     * @return The leaf state.
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * Returns true if all children are non-null and false otherwise.
     * @return The binary root state.
     */
    public boolean isBinaryRoot() {
        return left != null && right != null;
    }
}