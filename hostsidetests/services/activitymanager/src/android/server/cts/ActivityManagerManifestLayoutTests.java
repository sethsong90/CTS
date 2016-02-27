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

package android.server.cts;

import java.lang.Exception;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;

import java.awt.Rectangle;
import android.server.cts.WindowManagerState.WindowState;
import android.server.cts.WindowManagerState.Display;

import static com.android.ddmlib.Log.LogLevel.INFO;

import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.log.LogUtil.CLog;

public class ActivityManagerManifestLayoutTests extends ActivityManagerTestBase {

    // Clone of android DisplayMetrics.DENSITY_DEFAULT (DENSITY_MEDIUM)
    // (Needed in host-side test to convert dp to px.)
    private static final int DISPLAY_DENSITY_DEFAULT = 160;

    // Test parameters
    private static final int DEFAULT_WIDTH_DP = 160;
    private static final int DEFAULT_HEIGHT_DP = 160;
    private static final float DEFAULT_WIDTH_FRACTION = 0.25f;
    private static final float DEFAULT_HEIGHT_FRACTION = 0.25f;
    private static final int MINIMAL_SIZE_DP = 80;

    private static final int GRAVITY_VER_CENTER = 0x01;
    private static final int GRAVITY_VER_TOP    = 0x02;
    private static final int GRAVITY_VER_BOTTOM = 0x04;
    private static final int GRAVITY_HOR_CENTER = 0x10;
    private static final int GRAVITY_HOR_LEFT   = 0x20;
    private static final int GRAVITY_HOR_RIGHT  = 0x40;

    private List<WindowState> mTempWindowList = new ArrayList();
    private Display mDisplay;
    private WindowState mWindowState;

    public void testGravityAndDefaultSizeTopLeft() throws Exception {
        testLayout(GRAVITY_VER_TOP, GRAVITY_HOR_LEFT, false /*fraction*/);
    }

    public void testGravityAndDefaultSizeTopRight() throws Exception {
        testLayout(GRAVITY_VER_TOP, GRAVITY_HOR_RIGHT, true /*fraction*/);
    }

    public void testGravityAndDefaultSizeBottomLeft() throws Exception {
        testLayout(GRAVITY_VER_BOTTOM, GRAVITY_HOR_LEFT, true /*fraction*/);
    }

    public void testGravityAndDefaultSizeBottomRight() throws Exception {
        testLayout(GRAVITY_VER_BOTTOM, GRAVITY_HOR_RIGHT, false /*fraction*/);
    }

    public void testMinimalSizeFreeform() throws Exception {
        if (!supportsFreeform()) {
            CLog.logAndDisplay(INFO, "Skipping test: no freeform support");
            return;
        }
        testMinimalSize(FREEFORM_WORKSPACE_STACK_ID);
    }

    public void testMinimalSizeDocked() throws Exception {
        testMinimalSize(DOCKED_STACK_ID);
    }

    private void testMinimalSize(int stackId) throws Exception {
        final String activityName = "BottomRightLayoutActivity";

        // Issue command to resize to <0,0,1,1>. We expect the size to be floored at
        // MINIMAL_SIZE_DPxMINIMAL_SIZE_DP.
        if (stackId == FREEFORM_WORKSPACE_STACK_ID) {
            launchActivityInStack(activityName, stackId);
            resizeActivityTask(activityName, 0, 0, 1, 1);
        } else { // stackId == DOCKED_STACK_ID
            launchActivityInDockStack(activityName);
            resizeDockedStack(1, 1, 1, 1);
        }
        getDisplayAndWindowState(activityName);

        final int minimalSize = dpToPx(MINIMAL_SIZE_DP, mDisplay.getDpi());
        final Rectangle containingRect = mWindowState.getContainingFrame();
        final int actualSize = Math.min(containingRect.width, containingRect.height);

        // The shorter of width, height should be the minimal size.
        Assert.assertEquals("Minimum size is incorrect", minimalSize, actualSize);
    }

    private void testLayout(
            int vGravity, int hGravity, boolean fraction) throws Exception {
        if (!supportsFreeform()) {
            CLog.logAndDisplay(INFO, "Skipping test: no freeform support");
            return;
        }

        final String activityName = (vGravity == GRAVITY_VER_TOP ? "Top" : "Bottom")
                + (hGravity == GRAVITY_HOR_LEFT ? "Left" : "Right") + "LayoutActivity";

        // Launch in freeform stack
        launchActivityInStack(activityName, FREEFORM_WORKSPACE_STACK_ID);

        getDisplayAndWindowState(activityName);

        final Rectangle containingRect = mWindowState.getContainingFrame();
        final Rectangle appRect = mDisplay.getAppRect();
        final int expectedWidthPx, expectedHeightPx;
        // Evaluate the expected window size in px. If we're using fraction dimensions,
        // calculate the size based on the app rect size. Otherwise, convert the expected
        // size in dp to px.
        if (fraction) {
            expectedWidthPx = (int) (appRect.width * DEFAULT_WIDTH_FRACTION);
            expectedHeightPx = (int) (appRect.height * DEFAULT_HEIGHT_FRACTION);
        } else {
            final int densityDpi = mDisplay.getDpi();
            expectedWidthPx = dpToPx(DEFAULT_WIDTH_DP, densityDpi);
            expectedHeightPx = dpToPx(DEFAULT_HEIGHT_DP, densityDpi);
        }

        verifyFrameSizeAndPosition(
                vGravity, hGravity, expectedWidthPx, expectedHeightPx, containingRect, appRect);
    }

    private void getDisplayAndWindowState(String activityName) throws Exception {
        final String windowName = getWindowName(activityName);

        mAmWmState.computeState(mDevice, true /* visibleOnly */, new String[] {activityName});

        mAmWmState.assertSanity();

        mAmWmState.assertFocusedWindow("Test window must be the front window.", windowName);

        mAmWmState.getWmState().getMatchingWindowState(windowName, mTempWindowList);

        Assert.assertEquals("Should have exactly one window state for the activity.",
                1, mTempWindowList.size());

        mWindowState = mTempWindowList.get(0);
        Assert.assertNotNull("Should have a valid window", mWindowState);

        mDisplay = mAmWmState.getWmState().getDisplay(mWindowState.getDisplayId());
        Assert.assertNotNull("Should be on a display", mDisplay);
    }

    private void verifyFrameSizeAndPosition(
            int vGravity, int hGravity, int expectedWidthPx, int expectedHeightPx,
            Rectangle containingFrame, Rectangle parentFrame) {
        Assert.assertEquals("Width is incorrect", expectedWidthPx, containingFrame.width);
        Assert.assertEquals("Height is incorrect", expectedHeightPx, containingFrame.height);

        if (vGravity == GRAVITY_VER_TOP) {
            Assert.assertEquals("Should be on the top", parentFrame.y, containingFrame.y);
        } else if (vGravity == GRAVITY_VER_BOTTOM) {
            Assert.assertEquals("Should be on the bottom",
                    parentFrame.y + parentFrame.height, containingFrame.y + containingFrame.height);
        }

        if (hGravity == GRAVITY_HOR_LEFT) {
            Assert.assertEquals("Should be on the left", parentFrame.x, containingFrame.x);
        } else if (hGravity == GRAVITY_HOR_RIGHT){
            Assert.assertEquals("Should be on the right",
                    parentFrame.x + parentFrame.width, containingFrame.x + containingFrame.width);
        }
    }

    private static int dpToPx(float dp, int densityDpi){
        return (int) (dp * densityDpi / DISPLAY_DENSITY_DEFAULT + 0.5f);
    }
}