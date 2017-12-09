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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.beans.property.SimpleStringProperty;
import mes.ui.Application;

/**
 * IO binary file implementation.
 * @author Danilo Ferreira
 * @version 1.0.0
 */
public class File {
    private boolean exceptions;
    
    private SimpleStringProperty filename;
    private java.io.File file;

    /**
     * Initializes a null file object.
     */
    public File() {
        this((java.io.File)null);
    }
    
    /**
     * Initializes the file from a Java file object.
     * @param file A Java file object
     * @see #open(java.io.File)
     */
    public File(java.io.File file) {
        open(file);
    }

    /**
     * Initializes the file from a valid filename.
     * @param filename A valid filename
     * @see #open(String)
     */
    public File(String filename) {
        open(filename);
    }

    /**
     * Opens a file from a Java file object. If file doesn't exist a new one
     * will be created. A null file object is assigned if any exception is
     * thrown.
     * @param file A Java file object
     * @see java.io.File
     */
    public void open(java.io.File file) {
        exceptions = false;
        filename = new SimpleStringProperty("");

        this.file = file;

        if (file != null)
            if (file.exists())
                filename.set(file.getName());
            else
                try {
                    file.createNewFile();
                    filename.set(file.getName());
                } catch (Exception exception) {
                    exceptions = true;
                    file = null;
                }
    }

    /**
     * Opens a file from a valid filename. If file doesn't exist a new one will
     * be created. A null file object is assigned if any exception is thrown.
     * @param filename A valid filename
     * @see java.io.File
     */
    public void open(String filename) {
        open(new java.io.File(filename));
    }

    /**
     * Closes the file object.
     */
    public void close() {
        filename.set("");
        file = null;
    }

    /**
     * Returns true if the file is opened and false otherwise.
     * @return The file open state.
     */
    public boolean isOpen() {
        return file != null && file.canRead() && file.canWrite();
    }

    /**
     * Returns true if exceptions were thrown when opening file and false
     * otherwise.
     * @return The file exception state.
     */
    public boolean hasExceptions() {
        return exceptions;
    }
    
    /**
     * Returns a string containing the filename.
     * @return The filename.
     * @see #filenameProperty()
     */
    public String getFilename() {
        return filename.get();
    }
    
    /**
     * Returns a string property containing the filename.
     * @return The filename.
     * @see SimpleStringProperty
     */
    public SimpleStringProperty filenameProperty() {
        return filename;
    }

    /**
     * Reads an object from file. If any exception is thrown this method returns
     * null and a warning message is logged to application default logger.
     * @return The object read from file.
     * @see Application#warningLog(String)
     */
    public Object read() {
        try {
            FileInputStream fileReader = new FileInputStream(file);
            ObjectInputStream objectReader = new ObjectInputStream(fileReader);

            Object object = objectReader.readObject();

            objectReader.close();
            fileReader.close();

            return object;
        } catch (Exception exception) {
            Application.warningLog("cannot read data from file.");
        }

        return null;
    }

    /**
     * Writes an object to file. If any exception is thrown a warning message is
     * logged to application default logger.
     * @param object The object to write to file
     * @see Application#warningLog(String)
     */
    public void write(Object object) {
        try {
            FileOutputStream fileWriter = new FileOutputStream(file);
            ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);

            objectWriter.writeObject(object);

            objectWriter.close();
            fileWriter.close();
        } catch (Exception exception) {
            Application.warningLog("cannot write data to file.");
        }
    }
    
    /**
     * Initializes the file by copying the contents from another file object.
     * @param other Another file object
     */
    public void copyFrom(File other) {
        exceptions = other.hasExceptions();
        filename.set(other.getFilename());
        file = other.file;
    }
}