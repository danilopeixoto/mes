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
 * Abstract syntax tree implementation for {@link Symbol}.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see AbstractSyntaxNode
 */
public class AbstractSyntaxTree {
    private AbstractSyntaxNode root;

    /** Initializes the abstract syntax tree with null root. */
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
     * Returns the number of nodes in the abstract syntax tree.
     * @return The node count.
     */
    public int getNodeCount() {
        return computeNodeCount(root);
    }

    /**
     * Returns true if the abstract syntax tree is empty and false otherwise.
     * @return The empty state.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /** Deletes all the nodes in the abstract syntax tree. */
    public void clear() {
        root = null;
    }

    /**
     * The post-order traversal of the abstract syntax tree. This method calls
     * the function object while traversing tree.
     * @param function A custom traversal function
     * @return The output node.
     * @see TraversalFunction
     */
    public AbstractSyntaxNode traverse(TraversalFunction function) {
        return traverse(root, function);
    }

    private int computeNodeCount(AbstractSyntaxNode node) {
        if (node == null)
            return 0;

        return 1 + computeNodeCount(node.getLeft()) + computeNodeCount(node.getRight());
    }

    private AbstractSyntaxNode traverse(AbstractSyntaxNode node, TraversalFunction function) {
        if (node == null)
            return null;

        AbstractSyntaxNode left = traverse(node.getLeft(), function);
        AbstractSyntaxNode right = traverse(node.getRight(), function);

        return function.evaluate(node, left, right);
    }
}