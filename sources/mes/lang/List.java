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

/**
 * Generic list implementation.
 * @param <E> Type of element stored in the list
 * @author Danilo Ferreira
 * @version 1.0.0
 */
public class List<E> extends ArrayList<E> {
    /**
     * Generic iterator implementation for {@link List}.
     * @author Danilo Ferreira
     * @version 1.0.0
     */
    public class Iterator implements java.util.Iterator<E> {
        private int index;

        private Iterator() {
            this(0);
        }

        private Iterator(int index) {
            this.index = index;
        }

        /**
         * Set the current object of the iterator. This iterator must be a valid
         * element of list.
         * @param object The new object
         * @throws NullPointerException An exception indicating an invalid
         * iterator object
         * @see #isNull()
         */
        public void set(E object) {
            try {
                List.this.set(index, object);
            } catch (Exception exception) {
                throw new NullPointerException("Invalid iterator object.");
            }
        }

        /**
         * Returns the current object of the iterator. This iterator must be a
         * valid element of list otherwise a null object is returned.
         * @return The current iterator object.
         * @see #set(Object)
         * @see #isNull()
         */
        public E get() {
            return isNull() ? null : List.this.get(index);
        }

        /**
         * Set the current object of the iterator by element index.
         * @param index The list element index
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Returns the current element index of the iterator object.
         * @return The element index.
         * @see #setIndex(int)
         */
        public int getIndex() {
            return index;
        }

        /**
         * Returns whether the current iterator object is a valid element of
         * list.
         * @return The state of the current iterator object.
         */
        public boolean isNull() {
            return index < 0 || index >= size();
        }

        /**
         * Returns whether the previous index is a valid iteration element.
         * @return The state of the previous index.
         */
        public boolean hasPrevious() {
            return index >= 0;
        }

        /**
         * Returns whether the next index is a valid iteration element.
         * @return The state of the next index.
         */
        @Override
        public boolean hasNext() {
            return index < size();
        }

        /**
         * Returns the current iterator object and decrements the iterator. If
         * the current element is invalid this method returns null.
         * @return The current iterator object.
         * @see #hasPrevious()
         */
        public E previous() {
            return hasPrevious() ? List.this.get(index--) : null;
        }

        /**
         * Returns the current iterator object and increments the iterator. If
         * the current element is invalid this method returns null.
         * @return The current iterator object.
         * @see #hasNext()
         */
        @Override
        public E next() {
            return hasNext() ? List.this.get(index++) : null;
        }

        /**
         * Decrements or increments the iterator.
         * @param offset The positive or negative index offset
         * @see #previous()
         * @see #next()
         */
        public void advance(int offset) {
            index += offset;
        }

        /**
         * Resets the iterator to the first element.
         */
        public void reset() {
            index = 0;
        }
    }

    /**
     * Initializes an empty list.
     */
    public List() {
        this(0);
    }

    /**
     * Initializes the list with initial size.
     * @param size The list size
     */
    public List(int size) {
        super(size);
    }

    /**
     * Returns an iterator for the first element in the list.
     * @return The first element iterator.
     * @see Iterator
     */
    @Override
    public Iterator iterator() {
        return new Iterator();
    }
}