/*
 * Copyright (C) 2009 The Android Open Source Project
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

package android.jni.cts;

import junit.framework.TestCase;

/**
 * Basic static method tests. The "nonce" class being tested by this
 * class is a class defined in this package that declares the bulk of
 * its methods as native.
 */
public class JniStaticTest extends TestCase {
    /**
     * Test a simple no-op and void-returning method call.
     */
    public void test_nop() {
        StaticNonce.nop();
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnBoolean() {
        assertEquals(true, StaticNonce.returnBoolean());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnByte() {
        assertEquals(123, StaticNonce.returnByte());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnShort() {
        assertEquals(-12345, StaticNonce.returnShort());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnChar() {
        assertEquals(34567, StaticNonce.returnChar());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnInt() {
        assertEquals(12345678, StaticNonce.returnInt());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnLong() {
        assertEquals(-1098765432109876543L, StaticNonce.returnLong());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnFloat() {
        assertEquals(-98765.4321F, StaticNonce.returnFloat());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnDouble() {
        assertEquals(12345678.9, StaticNonce.returnDouble());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnNull() {
        assertNull(StaticNonce.returnNull());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call.
     */
    public void test_returnString() {
        assertEquals("blort", StaticNonce.returnString());
    }

    /**
     * Test a simple value-returning (but otherwise no-op) method call,
     * that returns the class that the method is defined on.
     */
    public void test_returnThisClass() {
        assertSame(StaticNonce.class, StaticNonce.returnThisClass());
    }

    // TODO: Add more tests here. E.g:
    //    call to method that instantiates its class (based on received jclass)
    //    call to method taking "this class", returning a "got expected" flag
    //    call to method taking boolean, returning a "got expected" flag
    //    call to method taking byte, returning a "got expected" flag
    //    call to method taking char, returning a "got expected" flag
    //    call to method taking short, returning a "got expected" flag
    //    call to method taking int, returning a "got expected" flag
    //    call to method taking long, returning a "got expected" flag
    //    call to method taking float, returning a "got expected" flag
    //    call to method taking double, returning a "got expected" flag
    //    call to method taking String, returning a "got expected" flag
    //    call to method taking (int, long), returning a "got expected" flag
    //    call to method taking (long, int), returning a "got expected" flag
}
