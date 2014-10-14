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

import org.iban4j.support.ExceptionUtil;

/**
 * Thrown to indicate that the application has attempted to convert
 * a string to Bic or to validate Bic's string representation, but the string does not
 * have the appropriate format.
 */
public class BicFormatException extends Iban4jException {

    private static final long serialVersionUID = 5334207117955765652L;

    private Constraint constraint;
    private Object[] values;

    /**
     * Constructs a <code>BicFormatException</code> with no detail message.
     */
    public BicFormatException() {
        super();
    }

    /**
     * Constructs a <code>BicFormatException</code> with the
     * specified constraint
     *
     * @param c the constraint
     */
    public BicFormatException(final Constraint c) {
        super(getMessage(c, null));
        this.constraint = c;
    }

    /**
     * Constructs a <code>BicFormatException</code> with the
     * specified constraint
     *
     * @param c the constraint
     * @param values additional info
     */
    public BicFormatException(final Constraint c, Object...values) {
        super(getMessage(c, values));
        this.constraint = c;
        this.values = values;
    }

    /**
     * Constructs a <code>BicFormatException</code> with the
     * specified detail message and cause.
     *
     * @param c the constraint
     * @param t the cause.
     */
    public BicFormatException(final Constraint c, final Throwable t) {
        super(getMessage(c, null), t);
        this.constraint = c;
    }

    /**
     * Constructs a <code>BicFormatException</code> with the
     * specified constraint, cause and additional informatoin.
     *
     * @param c the contraint
     * @param t the cause.
     * @param values additional information
     */
    public BicFormatException(final Constraint c, final Throwable t, Object...values) {
        super(getMessage(c, values), t);
        this.constraint = c;
        this.values = values;
    }

    /**
     * Constructs a <code>BicFormatException</code> with the
     * specified cause.
     *
     * @param t the cause.
     */
    public BicFormatException(final Throwable t) {
        super(t);
    }


    public Constraint getConstraint() {
        return constraint;
    }


    public Object[] getValues() {
        return values;
    }

    public enum Constraint {
        unknown,
        country_only_upper_case,
        non_existing_country,
        is_null,
        length,
        bic_upper_case,
        bank_code_only_letters,
        location_code_only_letters_or_digits,
        branch_code_only_letters_or_digits
    }


    public static String getMessage(Constraint c, Object[] values) {
        switch(c) {
            case country_only_upper_case: return "Bic country code must contain upper case letters";
            case non_existing_country: return "Bic contains non existing country code: " + ExceptionUtil.getValue(values, 0);
            case is_null: return "Null can\'t be a valid Bic.";
            case length: return "Bic length must be " + values[0] + " or " + values[1];
            case bic_upper_case: return "Bic must contain only upper case letters.";
            case bank_code_only_letters: return "Bank code must contain only letters.";
            case location_code_only_letters_or_digits: return "Location code must contain only letters or digits.";
            case branch_code_only_letters_or_digits: return "Branch code must contain only letters or digits";
        }
        return "Unknown constraint";
    }

    public String getValueAt(int pos) {
        return ExceptionUtil.getValue(values, pos);
    }

}