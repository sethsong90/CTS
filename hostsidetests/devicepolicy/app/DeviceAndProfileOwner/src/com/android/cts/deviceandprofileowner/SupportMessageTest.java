/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.cts.deviceandprofileowner;

import java.lang.Character;

public class SupportMessageTest extends BaseDeviceAdminTest {

    // Longest allowed length of a support message before the system may truncate it.
    private static final int MAX_LENGTH = 200;

    // Declare a different string of the same type for both long and short messages, so we can be
    // sure they aren't mixed up by any API calls.
    private static class ShortMessage {
        static final String EMPTY = "";
        static final String SIMPLE = "short-message-short";
        static final String MAX_LEN = new String(new char[200]).replace('\0', 'X');
        static final String TOO_LONG = new String(new char[MAX_LENGTH + 10]).replace('\0', 'A');
        static final String UNICODE = new String(Character.toChars(0x1F634)) + " zzz";
        static final String CONTAINS_NULL = "short\0null";
    }
    private static class LongMessage {
        static final String EMPTY = "";
        static final String SIMPLE = "long-message-long";
        static final String MAX_LEN = new String(new char[MAX_LENGTH]).replace('\0', 'Y');
        static final String TOO_LONG = new String(new char[MAX_LENGTH + 10]).replace('\0', 'B');
        static final String UNICODE = new String(Character.toChars(0x1F609)) + " ;)";
        static final String CONTAINS_NULL = "long\0null";
    }

    @Override
    protected void tearDown() throws Exception {
        clearSupportMessages();
        super.tearDown();
    }

    public void testShortSupportMessageSetGetAndClear() {
        setShortMessage(ShortMessage.SIMPLE);
        setShortMessage(null);
    }

    public void testLongSupportMessageSetGetAndClear() {
        setLongMessage(LongMessage.SIMPLE);
        setLongMessage(null);
    }

    public void testLongAndShortMessagesDoNotClobber() {
        setShortMessage(ShortMessage.SIMPLE);
        setLongMessage(LongMessage.SIMPLE);

        assertEquals(ShortMessage.SIMPLE, getShortMessage());
        assertEquals(LongMessage.SIMPLE, getLongMessage());
    }

    public void testMaximumLengthPrefixIsSaved() {
        // Save and restore a string of exactly the maximum length
        setShortMessage(ShortMessage.MAX_LEN);
        setLongMessage(LongMessage.MAX_LEN);

        /*
         * Save and restore a string that is too large -- this may only store the first N
         * characters, not the whole thing, so we need to use {@link String#startsWith} here.
         */
        mDevicePolicyManager.setShortSupportMessage(ADMIN_RECEIVER_COMPONENT,
                ShortMessage.TOO_LONG);
        assertStartsWith(ShortMessage.TOO_LONG.substring(0, MAX_LENGTH), getShortMessage());

        mDevicePolicyManager.setLongSupportMessage(ADMIN_RECEIVER_COMPONENT, LongMessage.TOO_LONG);
        assertStartsWith(LongMessage.TOO_LONG.substring(0, MAX_LENGTH), getLongMessage());
    }

    public void testEmptySupportMessage() {
        setShortMessage(ShortMessage.EMPTY);
        setLongMessage(LongMessage.EMPTY);
    }

    public void testUnicodeCharactersInMessage() {
        setShortMessage(ShortMessage.UNICODE);
        setLongMessage(LongMessage.UNICODE);
    }

    public void testNullCharacterInMessage() {
        setShortMessage(ShortMessage.CONTAINS_NULL);
        setLongMessage(LongMessage.CONTAINS_NULL);
    }

    public void testSetOrGetSupportMessageWithNullAdminFails() {
        // Short support message
        try {
            mDevicePolicyManager.setShortSupportMessage(null, ShortMessage.SIMPLE);
            fail("Exception should have been thrown for null admin ComponentName");
        } catch (NullPointerException expected) {
        }
        try {
            String message = mDevicePolicyManager.getShortSupportMessage(null);
            fail("Exception should have been thrown for null admin ComponentName");
        } catch (NullPointerException expected) {
        }

        // Long support message
        try {
            mDevicePolicyManager.setLongSupportMessage(null, LongMessage.SIMPLE);
            fail("Exception should have been thrown for null admin ComponentName");
        } catch (NullPointerException expected) {
        }

        try {
            String message = mDevicePolicyManager.getLongSupportMessage(null);
            fail("Exception should have been thrown for null admin ComponentName");
        } catch (NullPointerException expected) {
        }
    }

    /**
     * Delete all admin-set support messsages.
     */
    private void clearSupportMessages() {
        setShortMessage(null);
        setLongMessage(null);
    }

    /**
     * Update the short support message.
     *
     * @throws AssertionError in the case that the message could not be set.
     */
    private void setShortMessage(String message) {
        mDevicePolicyManager.setShortSupportMessage(ADMIN_RECEIVER_COMPONENT, message);
        assertEquals(message, getShortMessage());
    }

    /**
     * Update the long support message.
     *
     * @throws AssertionError in the case that the message could not be set.
     */
    private void setLongMessage(String message) {
        mDevicePolicyManager.setLongSupportMessage(ADMIN_RECEIVER_COMPONENT, message);
        assertEquals(message, getLongMessage());
    }

    private String getShortMessage() {
        return mDevicePolicyManager.getShortSupportMessage(ADMIN_RECEIVER_COMPONENT);
    }

    private String getLongMessage() {
        return mDevicePolicyManager.getLongSupportMessage(ADMIN_RECEIVER_COMPONENT);
    }

    private static void assertStartsWith(String expectPrefix, String actual) {
        assertNotNull(expectPrefix);
        assertNotNull(actual);
        if (!actual.startsWith(expectPrefix)) {
            fail("Expected prefix: '" + expectPrefix + "'\n" +
                 "            got: '" + actual + "'");
        }
    }
}