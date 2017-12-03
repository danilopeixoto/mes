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
import mes.lang.SymbolTable;

/**
 * File content to document representation.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see File
 */
public class Document implements Serializable {
    private CommandLineStream commandLineStream;
    private SymbolTable symbolTable;

    /**
     * Initializes the document with an empty command line stream and symbol
     * table.
     * @see CommandLineStream
     * @see SymbolTable
     */
    public Document() {
        this.commandLineStream = new CommandLineStream();
        this.symbolTable = new SymbolTable();
    }

    /**
     * Initializes the document with a non-null command line stream and symbol
     * table.
     * @param commandLineStream The command line data stream
     * @param symbolTable The identifier symbol table
     * @see CommandLineStream
     * @see SymbolTable
     */
    public Document(CommandLineStream commandLineStream, SymbolTable symbolTable) {
        this.commandLineStream = commandLineStream;
        this.symbolTable = symbolTable;
    }

    /**
     * Sets a non-null command line data stream.
     * @param commandLineStream Command line data stream
     */
    public void setCommandLineStream(CommandLineStream commandLineStream) {
        this.commandLineStream = commandLineStream;
    }

    /**
     * Sets a non-null identifier symbol table.
     * @param symbolTable The identifier symbol table
     */
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * Returns the command line data stream.
     * @return The command line data.
     * @see #setCommandLineStream(CommandLineStream)
     */
    public CommandLineStream getCommandLineStream() {
        return commandLineStream;
    }

    /**
     * Returns the identifier symbol table.
     * @return The identifier symbol table.
     * @see #setSymbolTable(SymbolTable)
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}