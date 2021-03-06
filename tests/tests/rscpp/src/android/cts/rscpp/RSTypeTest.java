/*
 * Copyright (C) 2015 The Android Open Source Project
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

package android.cts.rscpp;

import android.content.Context;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.renderscript.*;
import android.util.Log;

public class RSTypeTest extends RSCppTest {
    static {
        System.loadLibrary("rscpptest_jni");
    }

    native boolean testCreate(String path);
    public void testRSTypeTestCreate() {
        assertTrue(testCreate(this.getContext().getCacheDir().toString()));
    }

    native boolean testGet(String path);
    public void testRSTypeTestGet() {
        assertTrue(testGet(this.getContext().getCacheDir().toString()));
    }

    native boolean testGetCount(String path);
    public void testRSTypeTestGetCount() {
        assertTrue(testGetCount(this.getContext().getCacheDir().toString()));
    }
}
