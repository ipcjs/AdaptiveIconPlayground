package uk.co.nickbutcher.adaptiveiconplayground;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;

import org.michaelevans.colorart.library.ColorArt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ipcjs on 07/31.
 */

public class AdaptiveIconDrawableCompat {

    private ColorDrawable mBackground;

    public static float getExtraInsetFraction() {
        return 0.5f;
    }

    private Drawable mDrawable;

    public AdaptiveIconDrawableCompat(Drawable drawable) {
//        int inset = 40;
        int inset = 35;
        mDrawable = new InsetDrawable(drawable, Math.round(dp2px(inset)));
        mBackground = new ColorDrawable(computeBackgroundColor6(drawable2bitmap(drawable)));
    }

    private static int computeBackgroundColor3(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        AvgColor avgColor = new AvgColor(Color.WHITE);
        for (int y = 0; y < bitmap.getHeight(); y++) {
            int leftColor = digestColor(bitmap, y, 20, false);
            int rightColor = digestColor(bitmap, y, 20, true);
            if (leftColor == rightColor) {
                avgColor.add(leftColor);
            }
        }
        return avgColor.getAvg();
    }

    private static int computeBackgroundColor5(Bitmap bitmap) {
        AvgColor avgColor = new AvgColor(Color.WHITE);
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) == 0xff) {
                    avgColor.add(pixel);
                }
            }
        }
        return avgColor.getMax();
    }

    private static int computeBackgroundColor6(Bitmap bitmap) {
        AvgColor avgColor = new AvgColor(Color.WHITE);
        for (int y = 0; y < bitmap.getHeight(); y++) {
            if (y > bitmap.getHeight() / 2 && y < bitmap.getHeight() * 2 / 3) continue;
            for (int x = 0; x < bitmap.getWidth(); x++) {
                if (x > bitmap.getWidth() / 3 && x < bitmap.getWidth() * 2 / 3) continue;
                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) == 0xff) {
                    avgColor.add(pixel);
                }

            }
        }
        return avgColor.getMax();
    }

    private static int computeBackgroundColor4(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        AvgColor avgColor = new AvgColor(Color.WHITE);
        for (int y = 0; y < bitmap.getHeight(); y++) {
            int leftColor = digestColor(bitmap, y, 5, false);
            int rightColor = digestColor(bitmap, y, 5, true);
            avgColor.add(leftColor);
            avgColor.add(rightColor);
        }
        return avgColor.getMax();
    }

    private static int digestColor(Bitmap bitmap, int y, int maxCount, boolean reverse) {
        int leftColor = 0;
        int leftCount = 0;
        int x = reverse ? bitmap.getWidth() - 1 : 0;
        while (reverse ? x >= 0 : x < bitmap.getWidth()) {
            int pixel = bitmap.getPixel(x, y);
            if (Color.alpha(pixel) == 0xff) {
                if (leftCount > 0) {
                    if (pixel == leftColor) {
                        leftCount++;
                    } else {
                        leftColor = pixel;
                        leftCount = 1;
                    }
                } else {
                    leftColor = pixel;
                    leftCount = 1;
                }
                if (leftCount >= 3) {
                    return leftColor;
                }
            }
            x = reverse ? x - 1 : x + 1;
        }
        return Color.WHITE;
    }

    static class AvgColor {
        private int alpha = 0;
        private int red = 0;
        private int green = 0;
        private int blue = 0;
        private int mCount = 0;
        private int mDefaultColor;
        private Map<Integer, Integer> mMap = new HashMap<>();

        public AvgColor(int defaultColor) {
            this.mDefaultColor = defaultColor;
        }

        public void add(int color) {
            alpha += Color.alpha(color);
            red += Color.red(color);
            green += Color.green(color);
            blue += Color.blue(color);
            mCount++;
            Integer count = mMap.get(color);
            if (count == null) {
                mMap.put(color, 1);
            } else {
                mMap.put(color, count + 1);
            }
        }

        public int getMax() {
            int maxColor = mDefaultColor;
            int maxCount = 0;
            for (Map.Entry<Integer, Integer> entry : mMap.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxColor = entry.getKey();
                    maxCount = entry.getValue();
                }
            }
            return maxColor;
        }

        public int getAvg() {
            if (mCount == 0) return mDefaultColor;
            return Color.argb(alpha / mCount, red / mCount, green / mCount, blue / mCount);
        }
    }

    private static int computeBackgroundColor2(Bitmap bitmap) {
        return new ColorArt(bitmap).getBackgroundColor();
    }

    private static int computeBackgroundColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return palette.getMutedColor(Color.WHITE);
    }

    public static Bitmap drawable2bitmap(Drawable drawable) {
        int width = Math.max(1, drawable.getIntrinsicWidth());
        int height = Math.max(1, drawable.getIntrinsicHeight());
        drawable.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getInstance().getResources().getDisplayMetrics());
    }

    public Drawable getBackground() {
        return mBackground;
    }

    public Drawable getForeground() {
        return mDrawable;
    }
}
