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

package mes.ui;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import com.sun.javafx.application.LauncherImpl;

public class Application {
    public static final String name = "MES";
    public static final String fullName = "Mathematical Expression Solver";
    public static final String version = "1.0.0";
    public static final String license = "BSD-3-Clause license";
    public static final String licenseLink = "https://github.com/danilopeixoto/mes/blob/master/LICENSE";
    public static final String copyright = "Copyright © 2017, Danilo Peixoto. All rights reserved.";

    public static final String styleSheet = "styles/general.css";
    public static final String className = "GlassWndClass-GlassWindowClass-3";

    public static final Logger logger = Logger.getLogger(name);

    private static class LogFormatter extends Formatter {
        public LogFormatter() {
            super();
        }

        @Override
        public String format(LogRecord record) {
            Date date = new Date(record.getMillis());
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append('[');
            stringBuilder.append(date);
            stringBuilder.append("] ");
            stringBuilder.append(record.getLevel().getName());
            stringBuilder.append(": ");
            stringBuilder.append(record.getMessage());
            stringBuilder.append(System.lineSeparator());

            return stringBuilder.toString();
        }
    }

    public static void main(String[] args) {
        if (ApplicationInstance.isRunning())
            ApplicationInstance.focusWindow();
        else {
            try {
                LogFormatter formatter = new LogFormatter();

                FileHandler fileHandler = new FileHandler(name.toLowerCase() + ".log");
                fileHandler.setFormatter(formatter);

                logger.addHandler(fileHandler);
            } catch (IOException exception) {
                logger.log(Level.WARNING, "cannot write log to file.");
            }

            LauncherImpl.launchApplication(MainWindow.class, SplashScreen.class, args);
        }
    }
}