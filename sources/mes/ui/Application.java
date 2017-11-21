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

package mes.ui;

import com.sun.javafx.application.LauncherImpl;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.application.Platform;
import mes.io.File;
import mes.io.Preferences;

public class Application {
    public static final String name = "MES";
    public static final String fullName = "Mathematical Expression Solver";
    public static final String version = "1.0.0";
    public static final String license = "BSD-3-Clause license";
    public static final String licenseLink = "https://github.com/danilopeixoto/mes/blob/master/LICENSE";
    public static final String copyright = "Copyright © 2017, Danilo Peixoto. All rights reserved.";

    public static final String styleSheet = "styles/general.css";
    public static final String className = "GlassWndClass-GlassWindowClass-3";

    private static final Logger logger = Logger.getLogger(name);
    private static final File preferenceFile = new File(name.toLowerCase() + ".pref");

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

    public static Preferences loadPreferences() {
        return (Preferences)preferenceFile.read();
    }

    public static void savePreferences(Preferences preferences) {
        preferenceFile.write(preferences);
    }

    public static void logInformation(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void exit() {
        preferenceFile.close();

        Handler[] handlers = logger.getHandlers();

        if (handlers != null)
            for (Handler handler : handlers)
                handler.close();

        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            LogFormatter formatter = new LogFormatter();

            FileHandler fileHandler = new FileHandler(name.toLowerCase() + ".log");
            fileHandler.setFormatter(formatter);

            logger.addHandler(fileHandler);
        } catch (Exception exception) {
            logWarning("cannot write log to file.");
        }

        LauncherImpl.launchApplication(MainWindow.class, SplashScreen.class, args);
    }
}