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
 * limitations under the License
 */

package android.cts.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Random;

public class BitmapUtils {
    private BitmapUtils() {}

    // Compares two bitmaps by pixels.
    public static boolean compareBitmaps(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1 == bmp2) {
            return true;
        }

        if (bmp1 == null || bmp2 == null) {
            return false;
        }

        if ((bmp1.getWidth() != bmp2.getWidth()) || (bmp1.getHeight() != bmp2.getHeight())) {
            return false;
        }

        for (int i = 0; i < bmp1.getWidth(); i++) {
            for (int j = 0; j < bmp1.getHeight(); j++) {
                if (bmp1.getPixel(i, j) != bmp2.getPixel(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Bitmap generateRandomBitmap(int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Random generator = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, generator.nextInt(Integer.MAX_VALUE));
            }
        }
        return bmp;
    }

    public static Bitmap generateWhiteBitmap(int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, Color.WHITE);
            }
        }
        return bmp;
    }

    public static Bitmap getWallpaperBitmap(Context context) throws Exception {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        Class<?> noparams[] = {};
        Class<?> wmClass = wallpaperManager.getClass();
        Method methodGetBitmap = wmClass.getDeclaredMethod("getBitmap", noparams);
        return (Bitmap) methodGetBitmap.invoke(wallpaperManager, null);
    }

    public static ByteArrayInputStream bitmapToInputStream(Bitmap bmp) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        return new ByteArrayInputStream(bitmapData);
    }
}
