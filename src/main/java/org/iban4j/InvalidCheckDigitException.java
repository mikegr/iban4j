/*
 * Copyright 2013 Artur Mkrtchyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iban4j;

import java.io.Serializable;

/**
 * Thrown to indicate that Iban's check digit is invalid
 */
public class InvalidCheckDigitException extends Iban4jException {


    private static final long serialVersionUID = -9193946653023753090L;

    private String iban;
    private String invalid;
    private String expected;

    /**
     * Constructs a <code>InvalidCheckDigitException</code> with no detail message.
     */
    public InvalidCheckDigitException() {
        super();
    }

    /**
     * Constructs a <code>InvalidCheckDigitException</code> with the
     * specified details
     *
     * @param iban tested iban
     * @param invalid invalid check digits in tested iban
     * @param expected expected check digits
     */
    public InvalidCheckDigitException(final String iban, String invalid, String expected) {
        super(getMessage(iban, invalid, expected));
        this.iban = iban;
        this.invalid = invalid;
        this.expected = expected;
    }

    /**
     * Constructs a <code>InvalidCheckDigitException</code> with the
     * specified detail message and cause.
     *
     * @param s the detail message.
     * @param t the cause.
     */
    public InvalidCheckDigitException(final String s, final Throwable t) {
        super(s, t);
    }

    /**
     * Constructs a <code>InvalidCheckDigitException</code> with the
     * specified cause.
     *
     * @param t the cause.
     */
    public InvalidCheckDigitException(final Throwable t) {
        super(t);
    }

    public static String getMessage(String iban, String invalid, String expected) {
        return iban + " has invalid check digit: " + invalid + ", expected check digit is: " + expected;
    }

    public String getIban() {
        return iban;
    }

    public String getInvalid() {
        return invalid;
    }

    public String getExpected() {
        return expected;
    }


}