/*
 * Copyright (C) 2015 Google Inc.
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
package android.location.cts;

import junit.framework.Assert;

import android.location.GnssMeasurementsEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Used for receiving GPS satellite measurements from the GPS engine.
 * Each measurement contains raw and computed data identifying a satellite.
 */
class TestGnssMeasurementListener extends GnssMeasurementsEvent.Callback {
    // Timeout in sec for count down latch wait
    private static final int TIMEOUT_IN_SEC = 90;

    private volatile int mStatus = -1;

    private final String mTag;
    private final int mEventsToCollect;
    private final List<GnssMeasurementsEvent> mMeasurementsEvents;
    private final CountDownLatch mCountDownLatch;

    TestGnssMeasurementListener(String tag) {
        this(tag, 0);
    }

    TestGnssMeasurementListener(String tag, int eventsToCollect) {
        mTag = tag;
        mCountDownLatch = new CountDownLatch(eventsToCollect);
        mEventsToCollect = eventsToCollect;
        mMeasurementsEvents = new ArrayList<>(eventsToCollect);
    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        mMeasurementsEvents.add(event);
        if (mMeasurementsEvents.size() >= mEventsToCollect) {
            mCountDownLatch.countDown();
        }
    }

    @Override
    public void onStatusChanged(int status) {
        mStatus = status;
        if (mStatus != GnssMeasurementsEvent.STATUS_READY) {
            mCountDownLatch.countDown();
        }
    }

    /**
     * See {@link java.util.concurrent.CountDownLatch#await()}.
     */
    public boolean await() throws InterruptedException {
        return mCountDownLatch.await(TIMEOUT_IN_SEC, TimeUnit.SECONDS);
    }

    /**
     * @return {@code true} if the state of the test ensures that data is expected to be collected,
     *         {@code false} otherwise.
     */
    public boolean verifyState() {
        switch (getStatus()) {
            case GnssMeasurementsEvent.STATUS_NOT_SUPPORTED:
                SoftAssert.failAsWarning(mTag, "GnssMeasurements is not supported in the device:"
                        + " verifications performed by this test will be skipped.");
                return false;
            case GnssMeasurementsEvent.STATUS_READY:
                return true;
            case GnssMeasurementsEvent.STATUS_GNSS_LOCATION_DISABLED:
                Log.i(mTag, "Location or GPS is disabled on the device: skipping the test.");
                return false;
            default:
                Assert.fail("GnssMeasurementsEvent status callback was not received.");
        }
        return false;
    }

    /**
     * Get GPS Measurements Status.
     *
     * @return mStatus Gps Measurements Status
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Get list of GPS Measurements Events.
     *
     * @return mMeasurementsEvents list of GPS Measurements Events
     */
    public List<GnssMeasurementsEvent> getEvents() {
        return mMeasurementsEvents;
    }
}