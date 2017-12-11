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
 * Node implementation for {@link AbstractSyntaxTree}.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see AbstractSyntaxTree
 */
public abstract class AbstractSyntaxNode implements Serializable {
    protected List<AbstractSyntaxNode> children;

    /**
     * Initializes the node with no child.
     */
    public AbstractSyntaxNode() {
        children = new List<>();
    }

    /**
     * Sets the child node by index.
     * @param index The node index
     * @param child The child node
     */
    public void setChild(int index, AbstractSyntaxNode child) {
        children.set(index, child);
    }

    /**
     * Sets all the children of node.
     * @param children The node children
     */
    public void setChildren(List<AbstractSyntaxNode> children) {
        this.children = children;
    }

    /**
     * Returns the child node at index.
     * @param index The node index
     * @return The child node.
     * @see #setChild(int, AbstractSyntaxNode)
     */
    public AbstractSyntaxNode getChild(int index) {
        return children.get(index);
    }

    /**
     * Returns all the children of node.
     * @return The node children.
     * @see #setChildren(List)
     */
    public List<AbstractSyntaxNode> getChildren() {
        return children;
    }

    /**
     * Returns the child count of node.
     * @return The child count.
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns true if all children are null and false otherwise.
     * @return The leaf state.
     */
    public boolean isLeaf() {
        for (AbstractSyntaxNode node : children)
            if (node != null)
                return false;

        return true;
    }
}