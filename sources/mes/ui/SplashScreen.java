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

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Application splash screen (preloader).
 * @author Danilo Ferreira
 * @version 1.0.0
 */
public class SplashScreen extends Preloader {
    private final int width;
    private final int height;

    private final String icon;

    private Stage primaryStage;

    /** Initialize splash screen properties. */
    public SplashScreen() {
        width = 600;
        height = 600;

        icon = "images/icon_96.png";
    }

    private void centerWindowOnScreen() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    /**
     * Create root layout and show primary stage.
     * @param stage Splash screen primary stage
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Image image = new Image(icon);
        ImageView imageView = new ImageView(image);

        VBox rootLayout = new VBox();
        rootLayout.setSpacing(15.0);
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.getChildren().add(imageView);
        rootLayout.getStyleClass().setAll("splash-screen");

        Scene scene = new Scene(rootLayout, width, height);
        scene.getStylesheets().add(Application.styleSheet);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        centerWindowOnScreen();
    }

    /**
     * Handle {@link MainWindow} notification. This method hide primary stage
     * when the main window is ready to be shown.
     * @param notification Notification sent from the main window
     * @see MainWindow#start
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof ProgressNotification) {
            ProgressNotification progressNotification = (ProgressNotification)notification;

            if (progressNotification.getProgress() == 100.0)
                primaryStage.hide();
        }
    }
}