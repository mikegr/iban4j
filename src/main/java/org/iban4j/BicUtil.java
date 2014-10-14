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

import org.iban4j.support.Assert;

import static org.iban4j.BicFormatException.Constraint.bank_code_only_letters;
import static org.iban4j.BicFormatException.Constraint.bic_upper_case;
import static org.iban4j.BicFormatException.Constraint.branch_code_only_letters_or_digits;
import static org.iban4j.BicFormatException.Constraint.country_only_upper_case;
import static org.iban4j.BicFormatException.Constraint.is_null;
import static org.iban4j.BicFormatException.Constraint.length;
import static org.iban4j.BicFormatException.Constraint.location_code_only_letters_or_digits;
import static org.iban4j.BicFormatException.Constraint.non_existing_country;
import static org.iban4j.BicFormatException.Constraint.unknown;

/**
 * Bic Utility Class
 */
public class BicUtil {

    private static final int BIC8_LENGTH = 8;
    private static final int BIC11_LENGTH = 11;

    private static final int BANK_CODE_INDEX = 0;
    private static final int BANK_CODE_LENGTH = 4;
    private static final int COUNTRY_CODE_INDEX = BANK_CODE_INDEX + BANK_CODE_LENGTH;
    private static final int COUNTRY_CODE_LENGTH = 2;
    private static final int LOCATION_CODE_INDEX = COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH;
    private static final int LOCATION_CODE_LENGTH = 2;
    private static final int BRANCH_CODE_INDEX = LOCATION_CODE_INDEX + LOCATION_CODE_LENGTH;
    private static final int BRANCH_CODE_LENGTH = 3;

    /**
     * Validates bic.
     *
     * @param bic to be validated.
     * @throws BicFormatException if bic is invalid.
     */
    public static void validate(final String bic) throws BicFormatException {

        try {

            if (bic == null) throw new BicFormatException(is_null);
            validateLength(bic);
            validateCase(bic);
            validateBankCode(bic);
            validateCountryCode(bic);
            validateLocationCode(bic);

            if (hasBranchCode(bic)) {
                validateBranchCode(bic);
            }

        } catch (Iban4jException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BicFormatException(unknown, ex);
        }
    }



    private static void validateLength(final String bic) {
        if(bic.length() != BIC8_LENGTH && bic.length() != BIC11_LENGTH) {
            throw new BicFormatException(length, BIC8_LENGTH, BIC11_LENGTH);
        }
    }

    private static void validateCase(final String bic) {
        if(!bic.equals(bic.toUpperCase())) {
            throw new BicFormatException(bic_upper_case);
        }
    }

    private static void validateBankCode(final String bic) {
        String bankCode = getBankCode(bic);
        for(char ch : bankCode.toCharArray()) {
            if(!Character.isLetter(ch)) {
                throw new BicFormatException(bank_code_only_letters);
            }
        }
    }

    private static void validateCountryCode(final String bic) {
        String countryCode = getCountryCode(bic);
        if( countryCode.trim().length() < COUNTRY_CODE_LENGTH ||
                !countryCode.equals(countryCode.toUpperCase()) ||
                !Character.isLetter(countryCode.charAt(0)) ||
                !Character.isLetter(countryCode.charAt(1))) {
            throw new BicFormatException(country_only_upper_case);
        }
        if (CountryCode.getByCode(countryCode) == null) {
            throw new BicFormatException(non_existing_country, countryCode);
        }
    }

    private static void validateLocationCode(final String bic) {
        String locationCode = getLocationCode(bic);
        for(char ch : locationCode.toCharArray()) {
            if(!Character.isLetterOrDigit(ch)) {
                throw new BicFormatException(location_code_only_letters_or_digits);
            }
        }
    }

    private static void validateBranchCode(final String bic) {
        String branchCode = getBranchCode(bic);
        for(char ch : branchCode.toCharArray()) {
            if(!Character.isLetterOrDigit(ch)) {
                throw new BicFormatException(branch_code_only_letters_or_digits);
            }
        }
    }

    protected static String getBankCode(final String bic) {
        return bic.substring(BANK_CODE_INDEX, BANK_CODE_INDEX + BANK_CODE_LENGTH);
    }

    protected static String getCountryCode(final String bic) {
        return bic.substring(COUNTRY_CODE_INDEX, COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH);
    }

    protected static String getLocationCode(final String bic) {
        return bic.substring(LOCATION_CODE_INDEX, LOCATION_CODE_INDEX + LOCATION_CODE_LENGTH);
    }

    protected static String getBranchCode(final String bic) {
        return bic.substring(BRANCH_CODE_INDEX, BRANCH_CODE_INDEX + BRANCH_CODE_LENGTH);
    }

    protected static boolean hasBranchCode(final String bic) {
        return bic.length() == BIC11_LENGTH;
    }
}
