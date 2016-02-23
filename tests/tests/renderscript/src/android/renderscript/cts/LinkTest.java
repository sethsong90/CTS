/*
 * Copyright (C) 2015-2016 The Android Open Source Project
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

package android.renderscript.cts;

import android.renderscript.Script;
import android.util.Log;

import com.example.renderscript.testallapi.ScriptC_all_api;

/**
 * all_api.rs is auto-generated by frameworks/rs/api/generate.sh for the
 * lastest platform API level.
 */
public class LinkTest extends RSBaseCompute {

    public void testLink() {
        Script script = new ScriptC_all_api(mRS);
        Log.v("LinkTest", "Testing all APIs = " + script);
        assertTrue(script != null);
    }
}
