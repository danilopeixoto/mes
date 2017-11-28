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
 * File content to application preference representation.
 * @author Danilo Ferreira
 * @version 1.0.0
 * @see File
 */
public class Preferences implements Serializable {
    private boolean enableTypeChecking;
    private boolean enableAutocomplete;
    private boolean statusBarVisible;

    /**
     * Initializes the preferences. By default the type checking, text
     * autocomplete and status bar visibility are enable.
     */
    public Preferences() {
        this(true, true, true);
    }

    /** Initializes the preferences.
     * @param enableTypeChecking Enable type checking feature
     * @param enableAutocomplete Enable text autocomplete feature
     * @param statusBarVisible Enable status bar visibility
     */
    public Preferences(boolean enableTypeChecking, boolean enableAutocomplete,
            boolean statusBarVisible) {
        this.enableTypeChecking = enableTypeChecking;
        this.enableAutocomplete = enableAutocomplete;
        this.statusBarVisible = statusBarVisible;
    }

    /**
     * Sets the type checking feature state.
     * @param enableTypeChecking Enable type checking feature
     */
    public void setEnableTypeChecking(boolean enableTypeChecking) {
        this.enableTypeChecking = enableTypeChecking;
    }

    /**
     * Sets the text autocomplete feature state.
     * @param enableAutocomplete Enable text autocomplete feature
     */
    public void setEnableAutocomplete(boolean enableAutocomplete) {
        this.enableAutocomplete = enableAutocomplete;
    }

    /**
     * Sets the status bar visibility state.
     * @param statusBarVisible Enable status bar visibility
     */
    public void setStatusBarVisible(boolean statusBarVisible) {
        this.statusBarVisible = statusBarVisible;
    }

    /**
     * Returns the type checking feature state.
     * @return The type checking feature state.
     * @see #setEnableTypeChecking(boolean)
     */
    public boolean isEnableTypeChecking() {
        return enableTypeChecking;
    }

    /**
     * Returns the text autocomplete feature state.
     * @return The text autocomplete feature state.
     * @see #setEnableAutocomplete(boolean)
     */
    public boolean isEnableAutocomplete() {
        return enableAutocomplete;
    }

    /**
     * Returns the status bar visibility state.
     * @return The status bar visibility state.
     * @see #setStatusBarVisible(boolean)
     */
    public boolean isStatusBarVisible() {
        return statusBarVisible;
    }
}