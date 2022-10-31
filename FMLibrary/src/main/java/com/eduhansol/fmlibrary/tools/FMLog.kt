package com.friendsmon.fmlibrary.tools

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class FMLog {
    enum class Type {
        Logger, String
    }

    companion object {
        private val TAG = "DM_LOG"
        private var ISDEBUGGABLE = false
        private var logType = Type.Logger

        /**
         * 초기 Activity에서 호출
         * Dev 버전, Real 버전 구분하여 로그 표시
         *
         * @param context
         */
        @JvmStatic
        fun debugSet(context: Context) {
            ISDEBUGGABLE = isDebuggable(context)
            init()
        }

        /**
         * 강제 로그 설정
         *
         * @param isDebug true : visible, false : invisible
         */
        @JvmStatic
        fun debugSet(isDebug: Boolean) {
            ISDEBUGGABLE = isDebug
            init()
        }

        /**
         * 보여지는 로그 형태를 선택한다.
         *
         * @param type Type.Logger : Logger 스타일, Type.String : String 스타일
         */
        @JvmStatic
        fun setLogType(type: Type) {
            logType = type
        }

        @JvmStatic
        fun d(tag: String, message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.t(tag)
                        Logger.d(message)
                    }
                    Type.String -> {
                        Log.d(tag, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun d(message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.d(message)
                    }
                    Type.String -> {
                        Log.d(TAG, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun e(tag: String, message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.t(tag)
                        Logger.e(message)
                    }
                    Type.String -> {
                        Log.e(tag, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun e(message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.e(message)
                    }
                    Type.String -> {
                        Log.e(TAG, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun i(tag: String, message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.t(tag)
                        Logger.i(message)
                    }
                    Type.String -> {
                        Log.i(tag, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun i(message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.i(message)
                    }
                    Type.String -> {
                        Log.i(TAG, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun w(tag: String, message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.t(tag)
                        Logger.w(message)
                    }
                    Type.String -> {
                        Log.w(tag, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun w(message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.w(message)
                    }
                    Type.String -> {
                        Log.w(TAG, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun v(tag: String?, message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.t(tag)
                        Logger.v(message)
                    }
                    Type.String -> {
                        Log.v(tag, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun v(message: String) {
            if (ISDEBUGGABLE) {
                when (logType) {
                    Type.Logger -> {
                        Logger.v(message)
                    }
                    Type.String -> {
                        Log.v(TAG, buildLogMsg(message))
                    }
                }
            }
        }

        @JvmStatic
        fun json(json: String) {
            Logger.json(json)
        }

        @JvmStatic
        fun xml(xml: String) {
            Logger.xml(xml)
        }

        private fun isDebuggable(context: Context): Boolean {
            var isDebuggable = true
            val pm = context.packageManager
            try {
                val appinfo = pm.getApplicationInfo(context.packageName, 0)
                isDebuggable = 0 != appinfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
            } catch (e: PackageManager.NameNotFoundException) {
                /* debuggable variable will remain false */
            }
            return isDebuggable
        }

        private fun init() {
            val pfs = PrettyFormatStrategy.newBuilder()
                .tag(TAG)
                .methodOffset(1)
                .methodCount(1)
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(pfs))
        }

        private fun buildLogMsg(message: String): String {
            val ste = Thread.currentThread().stackTrace[4]
            val sb = StringBuilder()
            sb.append("[")
            sb.append(ste.fileName)
            sb.append(" > ")
            sb.append(ste.methodName)
            sb.append("() > #Line: ")
            sb.append(ste.lineNumber)
            sb.append("] - ")
            sb.append(message)
            return sb.toString()
        }
    }
}