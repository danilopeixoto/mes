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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AbstractSyntaxTree {
    public abstract class AbstractSyntaxNode {
        protected AbstractSyntaxNode left;
        protected AbstractSyntaxNode right;
        
        public AbstractSyntaxNode() {
            left = null;
            right = null;
        }

        public void setLeft(AbstractSyntaxNode left) {
            this.left = left;
        }

        public void setRight(AbstractSyntaxNode right) {
            this.right = right;
        }

        public AbstractSyntaxNode getLeft() {
            return left;
        }

        public AbstractSyntaxNode getRight() {
            return right;
        }
        
        public boolean isLeaf() {
            return left == null && right == null;
        }
        
        public boolean isBinaryRoot() {
            return left != null && right != null;
        }
    }
    
    private AbstractSyntaxNode root;
    
    public AbstractSyntaxTree() {
        this(null);
    }
    public AbstractSyntaxTree(AbstractSyntaxNode root) {
        this.root = root;
    }

    public void setRoot(AbstractSyntaxNode root) {
        this.root = root;
    }

    public AbstractSyntaxNode getRoot() {
        return root;
    }
    
    public int getNodeCount() {
        return computeNodeCount(root);
    }
    
    public boolean isEmpty() {
        return root == null;
    }
    
    public void clear() {
        root = null;
    }
    
    public Object traverse(Method function, Object[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return traverse(root, function, arguments);
    }
    
    private int computeNodeCount(AbstractSyntaxNode node) {
        if (node == null)
            return 0;
        
        return 1 + computeNodeCount(node.getLeft()) + computeNodeCount(node.getRight());
    }
    
    private Object traverse(AbstractSyntaxNode node, Method function, Object[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (node == null)
            return null;
        
        Object left = traverse(node.getLeft(), function, arguments);
        Object right = traverse(node.getRight(), function, arguments);
        
        return function.invoke(null, node, left, right, arguments);
    }
}