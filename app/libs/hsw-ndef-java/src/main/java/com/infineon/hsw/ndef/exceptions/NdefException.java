// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
//
// SPDX-License-Identifier: MIT

package com.infineon.hsw.ndef.exceptions;

import com.infineon.hsw.utils.annotation.NotNull;

/**
 * Exception class for NDEF message and NDEF record related exception.
 */
public class NdefException extends Exception {
    private static final long serialVersionUID = 5687903039190175843L;

    /**
     * Constructs an exception with the given exception message.
     *
     * @param message Message for the exception.
     */
    public NdefException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructs an exception with the exception message and exception stack.
     *
     * @param message   Message for the exception.
     * @param exception Exception stack
     */
    public NdefException(@NotNull String message,
                         @NotNull Exception exception) {
        super(message, exception);
    }

    /**
     * Constructs an exception with the exception message and exception detail.
     *
     * @param message   Message for the exception.
     * @param detail Exception detail (Eg: Input value) that caused the
     *         exception.
     */
    public NdefException(@NotNull String message, @NotNull String detail) {
        super(message + System.lineSeparator() + detail);
    }
}
