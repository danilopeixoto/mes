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

package mes.io;

import java.io.Serializable;

/**
 * UI Command Line data and property representation to save in document.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see Document
 */
public class CommandLineData implements Serializable {
    private String text;
    private boolean error;

    /** Initializes the default parameters. */
    public CommandLineData() {
        this("", false);
    }

    /**
     * Initializes parameters.
     * @param text The command line text content
     * @param error The flag to indicate the command line error representation
     */
    public CommandLineData(String text, boolean error) {
        this.text = text;
        this.error = error;
    }

    /**
     * Sets the command line text content.
     * @param text The command line text content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the command line error representation flag.
     * @param error The flag to indicate the command line error representation
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Returns the command line text content.
     * @return The text content.
     * @see #setText(String)
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the command line error representation flag.
     * @return The error flag.
     * @see #setError(boolean)
     */
    public boolean isError() {
        return error;
    }
}