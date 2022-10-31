package com.friendsmon.fmlibrary.utils

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics


class FMUIUtils {

    companion object {
        private var DENSITY_LDPI: Float = 0.75f
        private var DENSITY_MDPI: Float = 1f
        private var DENSITY_HDPI: Float = 1.5f
        private var DENSITY_XDPI: Float = 2f
        private var DENSITY_XXDPI: Float = 3f
        private var TAG: String = "d1"

        //현재 기기의 DPI
        @JvmStatic
        fun currentDPI(context: Context): Int {
            val windowManager: WindowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                display?.getRealMetrics(displayMetrics)
                return displayMetrics.densityDpi
            } else {
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                return displayMetrics.densityDpi
            }
        }

        // 현재 기기의 Density
        @JvmStatic
        fun currentDensity(context: Context): Float {
            val windowManager: WindowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                display?.getRealMetrics(displayMetrics)
                return displayMetrics.density
            } else {
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                return displayMetrics.density
            }
        }


        @JvmStatic
        fun dpToPixel(context: Context, dp: Float): Int {
            return (dp * currentDensity(context)).toInt()
        }

        @JvmStatic
        fun dpToPixel(density: Float, dp: Float): Int {
            return (dp * density).toInt()
        }

        @JvmStatic
        fun pxToDo(context: Context, px: Float): Int {
            return (px / currentDensity(context)).toInt()
        }

        @JvmStatic
        fun pxToDp(density: Float, px: Float): Int {
            return (px / density).toInt()
        }

        //다른 Density일때 px 구하기
        @JvmStatic
        fun pxToPixel(baseDensity: Float, changeDensity: Float, px: Int): Int {
            val dp: Int = pxToDp(baseDensity, px.toFloat())
            return dpToPixel(changeDensity, dp.toFloat())
        }

        @JvmStatic
        fun pxToPixel(baseDensity: Float, context: Context, px: Int): Int {
            val dp: Int = pxToDp(baseDensity, px.toFloat())
            return dpToPixel(currentDensity(context), dp.toFloat())
        }

        @JvmStatic
        fun pxToPixel(context: Context, changeDensity: Float, px: Int): Int {
            val dp: Int = pxToDp(currentDensity(context), px.toFloat())
            return dpToPixel(changeDensity, dp.toFloat())
        }

        /**
         *  단말기 가로 해상도 구하기
         *
         *  defaultDisplay is deprecated
         *  @param activity
         *  @return width
         */
        @JvmStatic
        fun getScreenWidth(activity: Activity): Int {
            var width: Int = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                width = windowMetrics.bounds.width() - insets.left - insets.right
                Log.i(TAG, " Screen width = $width")

                return width
            } else {
                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                width = displayMetrics.widthPixels
                Log.i(TAG, "Screen width = $width")
                return width
            }
        }

        /**
         * Sp to px
         *
         * @param context
         * @param sp
         * @return
         */
        @JvmStatic
        fun spToPx(context: Context, sp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.resources.displayMetrics
            )
                .toInt()
        }

        /**
         *  단말기 세로 해상도 구하기
         *
         *  @param activity
         *  @return height
         */
        @JvmStatic
        fun getScreenHeight(activity: Activity): Int {
            var height: Int = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                height = windowMetrics.bounds.height() - insets.top - insets.bottom
                Log.i(TAG, " Screen width = $height")
                return height
            } else {
                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                height = displayMetrics.heightPixels
                Log.i(TAG, "Screen width = $height")
                return height
            }
        }

        /**
         *  단말기 가로 해상도 구하기
         *
         *  @param context
         */
        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            var width: Int
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets: Insets =
                    wm.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                width = wm.currentWindowMetrics.bounds.width() - insets.left - insets.right
                Log.i(TAG, "Screen width = $width")
                return width
            } else {
                val dis: Display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                width = dis.width
                Log.i(TAG, "Screen width = $width")
                return width
            }
        }

        /**
         *  단말기 세로 해상도 구하기
         *
         *
         *  @param context
         */
        @JvmStatic
        fun getScreenHeight(context: Context): Int {
            var height: Int
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets: Insets =
                    wm.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                height = wm.currentWindowMetrics.bounds.height() - insets.top - insets.bottom
                Log.i(TAG, "Screen height = $height")
                return height
            } else {
                val dis: Display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                height = dis.height
                Log.i(TAG, "Screen height = $height")
                return height
            }
        }

        /**
         *  단말기 상단 바 크기(status Bar)
         *
         *  @param activity
         *  @return 상단바 사이즈 (dp)
         */
        fun getTopbarSize(activity: Activity): Int {
            val checkRect = Rect()
            val window = activity.window
            window.decorView.getWindowVisibleDisplayFrame(checkRect)
            return checkRect.top
        }


    }
}