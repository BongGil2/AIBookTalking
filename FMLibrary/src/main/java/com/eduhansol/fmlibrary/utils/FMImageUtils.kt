package com.friendsmon.fmlibrary.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FMImageUtils {

    companion object {

        /**
         * bitmap 이미지 저장 PNG
         *
         * @param bitmap  저장 할 이미지
         * @param strFilePath 저장 경로
         * @param filename 저장할 file 이름
         */

        @JvmStatic
        fun saveBitmapToFileCache(bitmap: Bitmap, strFilePath: String, filename: String) {

            val file = File(strFilePath)

            // If no folders
            if (!file.exists()) {
                file.mkdir()
            }

            val fileCacheItem = File(strFilePath + filename)
            var out: OutputStream? = null

            try {
                fileCacheItem.createNewFile()
                out = FileOutputStream(fileCacheItem)

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * bitmap 이미지 저장 (원하는 포맷으로 가능)
         * Bitmap.CompressFormat.???? 로 가능
         *
         * @param bitmap   저장 할 이미지
         * @param strFilePath 저장 경로
         * @param filename 저장할 file 이름
         * @param format 저장할 bitmap 포멧
         * @param quality 저장 비율
         */

        @JvmStatic
        fun saveBitmapToFileCache(
            bitmap: Bitmap,
            strFilePath: String,
            filename: String,
            format: Bitmap.CompressFormat,
            quality: Int,
        ) {

            val file = File(strFilePath)

            // If no folders
            if (!file.exists()) {
                file.mkdir()
            }

            val fileCacheItem = File(strFilePath + filename)
            var out: OutputStream? = null

            try {
                fileCacheItem.createNewFile()
                out = FileOutputStream(fileCacheItem)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * bitmap을 회전하고 회전된 이미지 저장 (PNG)
         *
         * @param bitmap      원본 이미지
         * @param degrees     회전 정도
         * @param strFilePath 저장 경로
         * @param filename    저장 파일명
         * @return 회전된 이미지 bitmap
         */

        @JvmStatic
        fun saveBitmapRotateToFile(
            bitmap: Bitmap,
            degrees: Int,
            strFilePath: String,
            filename: String,
        ): Bitmap? {
            var bitmap: Bitmap? = null
            var isSuccess: Boolean = true
            if (degrees != 0 && bitmap != null) {
                val m = Matrix()
                m.setRotate(
                    degrees.toFloat(), bitmap.width.toFloat() / 2,
                    bitmap.height.toFloat() / 2
                )

                try {
                    val converted: Bitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
                    if (bitmap != converted) {
                        bitmap.recycle()
                        bitmap = converted
                    }
                } catch (ex: OutOfMemoryError) {
                    isSuccess = false
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                }
            }

            if (isSuccess) {
                saveBitmapToFileCache(bitmap!!, strFilePath, filename)
            }
            return bitmap
        }

        /**
         * bitmap 이미지 회전
         *
         * @param bitmap 원본 이미지
         * @param degrees 회전 정도
         * @return 회전된 이미지 bitmap
         */
        fun bitmapRotate(bitmap: Bitmap, degrees: Int): Bitmap? {
            var bitmap: Bitmap? = null
            if (degrees != 0 && bitmap != null) {
                val m = Matrix()
                m.setRotate(
                    degrees.toFloat(),
                    bitmap.width.toFloat() / 2,
                    bitmap.height.toFloat() / 2
                )

                try {
                    val converted: Bitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
                    if (bitmap != converted) {
                        bitmap.recycle()
                        bitmap = converted
                    }
                } catch (ex: OutOfMemoryError) {
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                }
            }
            return bitmap
        }

        /**
         *  Bitmap을 비율에 맞게 크기를 조절 한다.
         *
         *  @param bitmap 원본 bitmap 이미지
         *  @param maxResol 원하는 이미지 최대 크기 (가로, 세로 필요 없음)
         *  @return 변경된 bitmap
         */
        fun resizeBitmap(bitmap: Bitmap, maxResol: Int): Bitmap {
            var iWidth = bitmap.width
            var iHeight = bitmap.height
            var newWidth = iWidth
            var newHeight = iHeight
            var rate: Float = 0.0f

            //이미지의 가로 세로 비율에 맞게 조절
            if (iWidth > iHeight) {
                if (maxResol < iWidth) {
                    rate = maxResol / iWidth.toFloat()
                    newHeight = (iHeight * rate).toInt()
                    newWidth = maxResol
                }
            } else {
                if (maxResol < iHeight) {
                    rate = maxResol / iHeight.toFloat()
                    newWidth = (iWidth * rate).toInt()
                    newHeight = maxResol
                }
            }
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }

    }
}