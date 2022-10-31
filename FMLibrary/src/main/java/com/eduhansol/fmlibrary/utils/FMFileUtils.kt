/**
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
package com.friendsmon.fmlibrary.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class FMFileUtils {
    companion object {

        /* external 저장공간에 읽기/쓰기가 가능한지 확인한다.*/
        @JvmStatic
        fun isExternalStorageWritable(): Boolean {
            val state: String = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true
            }
            return false
        }

        /* external 저장공간에서 최소한 읽기라도 할 수 있는지 확인한다. */
        @JvmStatic
        fun isExternalStorageReadable(): Boolean {
            val state: String = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(
                    state
                )
            ) {
                return true
            }
            return false
        }

        @JvmStatic
        fun getInternalCachePath(c: Context): String {
            return c.cacheDir.absolutePath
        }

        @JvmStatic
        fun getInternalDatabasePath(c: Context, name: String): String {
            return c.getDatabasePath(name).absolutePath
        }

        @JvmStatic
        fun getInternalFilePath(c: Context): String {
            return c.filesDir.absolutePath
        }

        @JvmStatic
        fun getInternalFileStreamPath(c: Context, name: String) {
            c.getFileStreamPath(name).absolutePath
        }


        /**
         * deprecated getExternalStorageDirectiory 확인
         * Android 10이상에서는 외부 저장소에 대해 Scoped storage 모드로 동작하게된다.
         * 대안은 getExternalFilesDir 이나 사진, 비디오, 오디오 파일등은 MediaStore
         * Downloads 파일에 저장되는 documents나 기타 파일을 읽고 쓰기 위해 SAF(Storage Access Framework)를 이용
         *
         */

        /**
         * 단말기 기본 경로
         * @param c
         * @return 경로
         */
        @JvmStatic
        fun getExternalBasePath(c: Context): String {
            return c.getExternalFilesDir(Environment.getExternalStorageState())!!.absolutePath
        }

        @JvmStatic
        fun getExternalBaseFilePath(c: Context): String {
            return c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
        }

        /**
         * database 경로 (디렉토리 까지만)
         * @param c
         * return 단말기의 database 저장 경로 ( 디렉토리 까지만 )
         */
        @JvmStatic
        fun getDatabasePath(c: Context): String {
            val path: String
            if (Build.VERSION.SDK_INT >= 17) {
                path = c.applicationInfo.dataDir + "/databases/"
            } else {
                path = "/data/data/" + c.packageName + "/databases"
            }

            return path
        }

        /**
         * 파일 존재여부 확인
         * @param isLivefile 파일 전체 경로(파일명 까지)
         * @return 존재 유무무
         */

        @JvmStatic
        fun fileIsLive(isLivefile: String): Boolean {
            val f1 = File(isLivefile)

            return f1.exists()
        }

        /**
         * 파일 생성
         * @param makeFileName 파일 전체 경로 (파일명 까지)
         */

        @JvmStatic
        fun fileMake(makeFileName: String) {
            val f1 = File(makeFileName)

            try {
                f1.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 파일 삭제
         * @param deleteFileName 파일 전체 경로 (파일명 까지)
         */

        @JvmStatic
        fun fileDelete(deleteFileName: String) {
            val I = File(deleteFileName)
            I.delete()
        }

        /**
         * 파일 복사
         * @param inFileName 원본 파일 전체 경로 (파일명 까지)
         * @param outFileName 복사될 파일 전체 경로 (파일명 까지)
         */

        @JvmStatic
        fun fileCopy(inFileName: String, outFileName: String) {
            try {
                makeDir(outFileName)
                val fis = FileInputStream(inFileName)
                val fos = FileOutputStream(outFileName)
                var data = 0
                while (fis.read().also { data = it } != -1) {
                    fos.write(data)
                }
                fis.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 파일 이동
         * @param inFileName 원본 파일 전체 경로
         * @param outFileName 이동될 파일 전체 경로
         */

        @JvmStatic
        fun fileMove(inFileName: String, outFileName: String) {
            try {
                makeDir(outFileName)
                val fis = FileInputStream(inFileName)
                val fos = FileOutputStream(outFileName)

                var data = 0
                while (fis.read().also { data = it } != -1) {
                    fos.write(data)
                }
                fis.close()
                fos.close()

                fileDelete(inFileName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 디렉토리 안에 파일 전체
         * @param dirPath 디렉토리 경로
         * @return File의 Array 형태 파일 목록 전달
         */

        @JvmStatic
        fun getDirFileList(dirPath: String): ArrayList<File>? {
            // 디렉토리 파일 리스트
            var dirFileList: java.util.ArrayList<File>? = null

            // 파일 목록을 요청한 디렉토리를 가지고 파일 객체를 생성함
            val dir = File(dirPath)

            // 디렉토리가 존재한다면
            if (dir.exists()) {
                // 파일 목록을 구함
                val files = dir.listFiles()

                // 파일 배열을 파일 리스트로 변화함
                dirFileList = java.util.ArrayList(Arrays.asList(*files))
            }
            return dirFileList
        }

        /**
         * 디렉토리 안에 파일 전체
         * @param dirpath 디렉토리 경로
         * @return String의 Array 형태 파일 목록 전달
         */

        @JvmStatic
        fun getDirFileNameList(dirPath: String): ArrayList<String> {
            val dirFileList: ArrayList<File> = getDirFileList(dirPath)!!
            val dirFileName: ArrayList<String> = ArrayList()

            for (i in dirFileList.indices) {
                val file = dirFileList[i]
                dirFileName.add(file.name)
            }

            return dirFileName

        }

        /**
         * splite 파일을 assets에서 database로 복사
         * @param c
         * @param dbName 복사할 DB Name
         */

        @JvmStatic
        fun copyDataBaseFromAssets(c: Context, dbName: String) {
            try {
                val mInput: InputStream = c.assets.open(dbName)
                val outFileName: String = getDatabasePath(c) + dbName
                val mOutput: OutputStream = FileOutputStream(outFileName)
                val mBuffer = ByteArray(1024)
                var mLength: Int
                while (mInput.read(mBuffer).also { mLength = it } > 0) {
                    mOutput.write(mBuffer, 0, mLength)
                }
                mOutput.flush()
                mOutput.close()
                mInput.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun makeDir(path: String) {
            val num = path.lastIndexOf("/")
            val dir = path.substring(0, num)
            val f = File(dir)
            if (!f.exists()) {
                f.mkdir()
            }
        }

        /**
         * getExternalStorageDirectory deprecated
         *
         * @param context
         * @param uri
         * @return
         */

        @JvmStatic
        fun getPath(context: Context, uri: Uri): String? {

            val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        private fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?,
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
            return null
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents".equals(uri.authority)
        }


        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents".equals(uri.authority)
        }


        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents".equals(uri.authority)
        }


        /**
         *  내부 저장소 전체 용량 확인
         *  @return 크기
         */
        fun checkInternalStorageAllMemory(): Long {
            val stat: StatFs = StatFs(Environment.getDataDirectory().path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks: Long = stat.blockCountLong

            return blockSize * totalBlocks
        }

        /**
         *  내부 저장소 이용가능 용량 확인
         *  @return 크기
         */
        fun checkInternalAvailableMemory(): Long {
            val stat: StatFs = StatFs(Environment.getDataDirectory().path)
            val blockSize: Long = stat.blockSizeLong
            val availableBlocks: Long = stat.availableBlocksLong

            return blockSize * availableBlocks
        }

        /**
         *  외부 저장소 전체 용량 확인
         *  @return 크기
         */
        fun checkExternalStorageAllMemory(context: Context): Long {
            if (isExternalStorageWritable()) {
                val stat: StatFs = StatFs(context.getExternalFilesDir(null)?.path)
                val blockSize: Long = stat.blockSizeLong
                val availableBlock: Long = stat.availableBlocksLong

                return blockSize * availableBlock


            }
            return 0
        }

        /**
         *  외부 저장소 이용가능 용량 확인
         *  @return 크기
         */
        fun checkExternalAvailableMemory(context: Context): Long {
            if (isExternalStorageWritable()) {
                val file: File = Environment.getExternalStorageDirectory()
                val stat: StatFs = StatFs(file.path)
                val blockSize: Long = stat.blockSizeLong
                val availableBlock: Long = stat.availableBlocksLong

                return blockSize * availableBlock
            }
            return 0
        }

        fun getFileSize(size: Long): String {
            if (size <= 0) return "0"

            val units: Array<String> = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups: Int = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                size / Math.pow(
                    1024.0,
                    digitGroups.toDouble()
                )
            ) + " " + units[digitGroups]
        }

    }
}