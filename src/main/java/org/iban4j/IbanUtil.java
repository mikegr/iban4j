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

import org.iban4j.bban.BbanStructure;
import org.iban4j.bban.BbanStructureEntry;
import static org.iban4j.IbanFormatException.Constraint.assert_msg_digits;
import static org.iban4j.IbanFormatException.Constraint.assert_msg_digits_and_letters;
import static org.iban4j.IbanFormatException.Constraint.assert_msg_upper_letters;
import static org.iban4j.IbanFormatException.Constraint.invalid_character;
import static org.iban4j.IbanFormatException.Constraint.invalid_length;
import static org.iban4j.IbanFormatException.Constraint.is_null;
import static org.iban4j.IbanFormatException.Constraint.unknown;
import static org.iban4j.UnsupportedCountryException.Constraint.non_existing;
import static org.iban4j.UnsupportedCountryException.Constraint.upper_case;

/**
 * Iban Utility Class
 */
public final class IbanUtil {

    private static final int MOD = 97;
    private static final long MAX = 999999999;

    private static final int MIN_IBAN_SIZE = 15;

    private static final int COUNTRY_CODE_INDEX = 0;
    private static final int COUNTRY_CODE_LENGTH = 2;
    private static final int CHECK_DIGIT_INDEX = COUNTRY_CODE_LENGTH;
    private static final int CHECK_DIGIT_LENGTH = 2;
    private static final int BBAN_INDEX = CHECK_DIGIT_INDEX + CHECK_DIGIT_LENGTH;

    /**
     * Calculates Iban
     * <a href="http://en.wikipedia.org/wiki/ISO_13616#Generating_IBAN_check_digits">Check Digit</a>.
     *
     * @param iban string value
     * @return check digit as String
     */
    public static String calculateCheckDigit(final String iban) {
        String reformattedIban = replaceCheckDigit(iban, Iban.DEFAULT_CHECK_DIGIT);
        int modResult = calculateMod(reformattedIban);
        int checkDigitIntValue = (98 - modResult);
        String checkDigit = Integer.toString(checkDigitIntValue);
        return checkDigitIntValue > 9 ? checkDigit : "0" + checkDigit;
    }

    /**
     * Validates iban.
     *
     * @param iban to be validated.
     * @throws IbanFormatException if iban is invalid.
     *         UnsupportedCountryException if iban's country is not supported.
     *         InvalidCheckDigitException if iban has invalid check digit.
     */
    public static void validate(final String iban) throws IbanFormatException,
            InvalidCheckDigitException, UnsupportedCountryException {

        try {
            if (iban == null) throw new IbanFormatException(is_null);
            validateCountryCode(iban);
            validateMinLength(iban);

            BbanStructure structure = getBbanStructure(iban);

            validateBbanLength(iban, structure);
            validateBbanEntries(iban, structure);

            validateCheckDigit(iban);
        } catch (Iban4jException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IbanFormatException(unknown, e);
        }
    }

    protected static void validateCheckDigit(final String iban) {
        String checkDigit = getCheckDigit(iban);
        String expectedCheckDigit = calculateCheckDigit(iban);
        if (!checkDigit.equals(expectedCheckDigit)) {
            throw new InvalidCheckDigitException(iban, checkDigit, expectedCheckDigit);
        }
    }

    public static void validateCountryCode(final String iban) {
        String countryCode = getCountryCode(iban);
        if( countryCode.trim().length() < COUNTRY_CODE_LENGTH ||
            !countryCode.equals(countryCode.toUpperCase()) ||
            !Character.isLetter(countryCode.charAt(0)) ||
            !Character.isLetter(countryCode.charAt(1))) {
            throw new UnsupportedCountryException(upper_case, countryCode);
        }
        if (CountryCode.getByCode(countryCode) == null) {
            throw new UnsupportedCountryException(non_existing, countryCode);
        }
    }

    private static void validateMinLength(final String iban) {
        if(iban.length() < MIN_IBAN_SIZE) {
            throw new IbanFormatException(invalid_length, iban, iban.length(), MIN_IBAN_SIZE);
        }
    }

    private static void validateBbanLength(final String iban, final BbanStructure structure) {
        int expectedBbanLength = structure.getBbanLength();
        String bban = getBban(iban);
        int bbanLength = bban.length();
        if (expectedBbanLength != bbanLength) {
            throw new IbanFormatException(invalid_length, bban, bbanLength, expectedBbanLength);
        }
    }


    private static void validateBbanEntries(final String iban, final BbanStructure structure) {
        final String bban = getBban(iban);
        int bbanEntryOffset = 0;
        for(BbanStructureEntry entry : structure.getEntries()) {
            final int entryLength = entry.getLength();
            final String entryValue = bban.substring(bbanEntryOffset, bbanEntryOffset + entryLength);

            bbanEntryOffset = bbanEntryOffset + entryLength;

            // validate character type
            validateBbanEntryCharacterType(entry, entryValue);
        }
    }

    private static void validateBbanEntryCharacterType(final BbanStructureEntry entry, final String entryValue) {
        switch (entry.getCharacterType()) {
            case a:
                for(char ch: entryValue.toCharArray()) {
                    if (! Character.isUpperCase(ch)) throw new IbanFormatException(assert_msg_upper_letters, entryValue);
                }
                break;
            case c:
                for(char ch: entryValue.toCharArray()) {
                    if (! Character.isLetterOrDigit(ch)) throw new IbanFormatException(assert_msg_digits_and_letters, entryValue);
                }
                break;
            case n:
                for(char ch: entryValue.toCharArray()) {
                    if (! Character.isDigit(ch)) throw new IbanFormatException(assert_msg_digits, entryValue);
                }
                break;
        }
    }

    protected static String calculateCheckDigit(final Iban iban) {
        return calculateCheckDigit(iban.toString());
    }

    /**
     * Returns an iban with replaced check digit.
     *
     * @param iban The iban
     * @return The iban without the check digit
     */
    protected static String replaceCheckDigit(final String iban, final String checkDigit) {
        return getCountryCode(iban) + checkDigit + getBban(iban);
    }


    /**
     * Calculates
     * <a href="http://en.wikipedia.org/wiki/ISO_13616#Modulo_operation_on_IBAN">Iban Modulo</a>.
     *
     * @param iban String value
     * @return modulo 97
     */
    private static int calculateMod(final String iban) {
        String reformattedIban = getBban(iban) + getCountryCodeAndCheckDigit(iban);
        long total = 0;
        for (int i = 0; i < reformattedIban.length(); i++) {
            int numericValue = Character.getNumericValue(reformattedIban.charAt(i));
            if (numericValue < 0 || numericValue > 35) {
                throw new IbanFormatException(invalid_character, i, numericValue);
            }
            total = (numericValue > 9 ? total * 100 : total * 10) + numericValue;

            if (total > MAX) {
                total = (total % MOD);
            }

        }
        return (int) (total % MOD);
    }

    protected static BbanStructure getBbanStructure(final String iban) {
        String countryCode = getCountryCode(iban);
        return BbanStructure.forCountry(CountryCode.getByCode(countryCode));
    }

    protected static String getCheckDigit(final String iban) {
        return iban.substring(CHECK_DIGIT_INDEX, CHECK_DIGIT_INDEX + CHECK_DIGIT_LENGTH);
    }

    public static String getCountryCode(final String iban) {
        return iban.substring(COUNTRY_CODE_INDEX, COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH);
    }

    protected static String getCountryCodeAndCheckDigit(final String iban) {
        return iban.substring(COUNTRY_CODE_INDEX, COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH + CHECK_DIGIT_LENGTH);
    }

    public static String getBban(final String iban) {
        return iban.substring(BBAN_INDEX);
    }

    protected static String getAccountNumber(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.c);
    }

    protected static String getBankCode(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.b);
    }

    protected static String getBranchCode(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.s);
    }

    protected static String getNationalCheckDigit(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.x);
    }

    protected static String getAccountType(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.t);
    }

    protected static String getOwnerAccountType(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.n);
    }

    protected static String getIdentificationNumber(final String iban) {
        return extractBbanEntry(iban, BbanStructureEntry.EntryType.i);
    }

    private static String extractBbanEntry(final String iban, final BbanStructureEntry.EntryType entryType) {
        final String bban = getBban(iban);
        final BbanStructure structure = getBbanStructure(iban);
        int bbanEntryOffset = 0;
        for(BbanStructureEntry entry : structure.getEntries()) {
            final int entryLength = entry.getLength();
            final String entryValue = bban.substring(bbanEntryOffset, bbanEntryOffset + entryLength);

            bbanEntryOffset = bbanEntryOffset + entryLength;
            if(entry.getEntryType() == entryType) {
                return entryValue;
            }
        }
        return null;
    }


}
