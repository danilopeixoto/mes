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

import java.util.ArrayList;
import java.util.Iterator;

public class FunctionArgumentList extends ArrayList<FunctionArgument> {
    public class FunctionArgumentIterator implements Iterator<FunctionArgument> {
        private int index;

        public FunctionArgumentIterator() {
            this(0);
        }

        public FunctionArgumentIterator(int index) {
            this.index = index;
        }

        public void set(FunctionArgument functionArgument) {
            FunctionArgumentList.this.set(index, functionArgument);
        }

        public FunctionArgument get() {
            return FunctionArgumentList.this.get(index);
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        public boolean hasPrevious() {
            return index >= 0;
        }

        @Override
        public FunctionArgument next() {
            return hasNext() ? FunctionArgumentList.this.get(index++) : null;
        }

        public FunctionArgument previous() {
            return hasPrevious() ? FunctionArgumentList.this.get(index--) : null;
        }

        public void reset() {
            index = 0;
        }
    }

    public FunctionArgumentList() {
        super();
    }

    public FunctionArgumentList(int size) {
        super(size);
    }

    @Override
    public FunctionArgumentIterator iterator() {
        return new FunctionArgumentIterator();
    }
}