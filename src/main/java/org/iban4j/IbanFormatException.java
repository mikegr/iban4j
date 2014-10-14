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

import static org.iban4j.support.ExceptionUtil.getValue;

/**
 * Thrown to indicate that the application has attempted to convert
 * a string to Iban, but that the string does not
 * have the appropriate format.
 */
public class IbanFormatException extends Iban4jException {

    private static final long serialVersionUID = 4385766780446382504L;


    public Constraint getConstraint() {
        return constraint;
    }

    public Object[] getValues() {
        return values;
    }

    private Constraint constraint;
    private Object[] values;

    /**
     * Constructs a <code>IbanFormatException</code> with no detail message.
     */
    public IbanFormatException() {
        super();
    }

    /**
     * Constructs a <code>IbanFormatException</code> with the
     * specified constraint
     *
     * @param c the constraint
     */
    public IbanFormatException(final Constraint c) {
        super(getMessage(c, null));
        this.constraint = c;
    }

    /**
     * Constructs a <code>IbanFormatException</code> with the
     * specified constraint and additional information.
     *
     * @param c the constraint
     * @param values additional info
     *
     */
    public IbanFormatException(final Constraint c, Object...values) {
        super(getMessage(c, values));
        this.constraint = c;
        this.values = values;
    }

    /**
     * Constructs a <code>IbanFormatException</code> with the
     * specified constraint, cause and additional information
     *
     * @param c the constraint
     * @param t the cause.
     * @param values additional information
     */
    public IbanFormatException(final Constraint c, final Throwable t, Object...values ) {
        super(getMessage(c, values), t);
        this.constraint = c;
        this.values = values;
    }

    /**
     * Constructs a <code>IbanFormatException</code> with the
     * specified cause.
     *
     * @param t the cause.
     */
    public IbanFormatException(final Throwable t) {
        super(t);
    }


    public enum Constraint {
        unknown,
        invalid_length,
        invalid_character,
        is_null,

        assert_msg_upper_letters,
        assert_msg_digits_and_letters,
        assert_msg_digits,

        bank_code_required,
        account_number_required,
        checksum_only_numeric,
        pos_uppercase_only,
        pos_numeric_only,
        pos_alphanumeric_only
    }

    public static String getMessage(Constraint c, Object[] values) {


        switch(c) {
            case invalid_length: return getValue(values, 0) + " length is " + getValue(values, 1) +" expected length is: " + getValue(values, 2);
            case invalid_character: return "Invalid Character[" + getValue(values, 0) + "] = \'" + getValue(values, 1) +"'";
            case is_null: return "Null can\'t be a valid Iban.";
            case assert_msg_upper_letters: return getValue(values, 0) + " must contain only upper case letters.";
            case assert_msg_digits_and_letters: return getValue(values, 0) + "must contain only digits or letters.";
            case assert_msg_digits: return getValue(values, 0) + "must contain only digits.";
            case bank_code_required: return "bankCode is required; it cannot be null";
            case account_number_required: return "accountNumber is required; it cannot be null";
            case checksum_only_numeric: return "Position 3 and 4 allow only digits";
            case pos_uppercase_only: return "Only upper case letters allowed at position " + getValue(values, 0);
            case pos_numeric_only:return "Only digits allowed at position " + getValue(values, 0);
            case pos_alphanumeric_only: return "Onyl letters and numbers allowed at position " + getValue(values, 0);

        }
        return null;
    }
    public String getValueAt(int pos) {
        return getValue(values, pos);
    }

}