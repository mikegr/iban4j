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
 * Thrown to indicate that requested country is not supported.
 */
public class UnsupportedCountryException extends Iban4jException {

    private static final long serialVersionUID = -5193286194898199366L;

    private Constraint constraint;
    private Object[] values;

    public Constraint getConstraint() {
        return constraint;
    }

    public Object[] getValues() {
        return values;
    }

    /**
     * Constructs a <code>UnsupportedCountryException</code> with no detail message and cause.
     */
    public UnsupportedCountryException() {
        super();
    }

    /**
     * Constructs a <code>UnsupportedCountryException</code> with the
     * specified details
     *
     * @param c the contraint
     * @param values additional information
     */
    public UnsupportedCountryException(final Constraint c, Object...values) {
        super(getMessage(c, values));
        this.constraint = c;
        this.values = values;
    }

    /**
     * Constructs a <code>UnsupportedCountryException</code> with the
     * specified detail message and cause.
     *
     * @param s the detail message.
     * @param t the cause.
     */
    public UnsupportedCountryException(final String s, final Throwable t) {
        super(s, t);
    }

    /**
     * Constructs a <code>UnsupportedCountryException</code> with the
     * specified cause.
     *
     * @param t the cause.
     */
    public UnsupportedCountryException(final Throwable t) {
        super(t);
    }

    public static enum Constraint {
        is_null,
        upper_case,
        not_supported,
        non_existing
    }

    public static String getMessage(Constraint c, Object[] values) {
        switch(c) {
            case is_null: return "countryCode is required; it cannot be null";
            case upper_case: return "Iban country code must contain upper case letters";
            case non_existing: return "Iban contains non existing country code.";
            case not_supported: return "Country code: " + ExceptionUtil.getValue(values, 0)  + " is not supported.";
        }
        return "Unknown constraint";
    }
}