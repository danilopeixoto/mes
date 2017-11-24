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
import mes.ui.Application;

public class File {
    private boolean exceptions;
    private java.io.File file;

    public File() {
        open((java.io.File)null);
    }

    public File(java.io.File file) {
        open(file);
    }

    public File(String filename) {
        open(filename);
    }

    public void open(java.io.File file) {
        exceptions = false;
        this.file = file;

        if (file != null && !file.exists())
            try {
                file.createNewFile();
            } catch (Exception exception) {
                exceptions = true;
                file = null;
            }
    }

    public void open(String filename) {
        open(new java.io.File(filename));
    }

    public void close() {
        file = null;
    }

    public boolean isOpen() {
        return file != null && file.canRead() && file.canWrite();
    }

    public boolean hasExceptions() {
        return exceptions;
    }

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
}