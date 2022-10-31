/**
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 */

package com.friendsmon.fmlibrary.utils

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Insets
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.ClipboardManager
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class FMPhoneUtils {

    companion object {
        private const val PHONE_TELL_BASE = "tel:"
        private const val PHONE_TELL_FORMAT = "-"

        private const val PHONE_TYPE_DIAL = "dial"
        private const val PHONE_TYPE_DIRECT_CALL = "directCall"

        const val TYPE_CONNECTION_NONE = 0
        const val TYPE_CONNECTION_WIFI = 1
        const val TYPE_CONNECTION_PHONE = 2

        /**
         * --------------------------------------------------------------------------------
         * 사용법 : onWindowFocusChanged()를 오버라이드 해서 사용
         *
         * @Override public void onWindowFocusChanged(boolean hasFocus) {
         * //super.onWindowFocusChanged(hasFocus);
         * if( hasFocus ) {
         * getDecorView(this).setSystemUiVisibility( setDefaultView(this, type, viewType) );
         * }
         * }
         */

        private const val TYPE_VIEW_HIDE_ALL = 0
        private const val TYPE_VIEW_HIDE_ONLY_SOFTKEYS = 1
        private const val TYPE_VIEW_HIDE_ONLY_NAVIGATION = 2
        private const val TYPE_VIEW_TOUCH_SOFTKEYS = 0
        private const val TYPE_VIEW_UP_SOFTKEYS = 1


        /**
         * "tel: 010-1234-1234" 의 형태로 전달 "tel:01012341234" 도 가능
         */

        @JvmStatic
        fun makeACall(activity: Activity, callType: String, phoneNumber: String) {

            var intent: Intent? = null

            if (callType.equals(PHONE_TYPE_DIAL)) { // dial
                intent = Intent(Intent.ACTION_DIAL)
            } else { // directCall
                intent = Intent(Intent.ACTION_CALL)
            }

            intent.data = Uri.parse(PHONE_TELL_BASE + phoneNumber)
            activity.startActivity(intent)

        }

        /**
         * 숫자를 폰번호 형태로 변경
         *
         * @param phoneNumber 숫자
         * @return 변경된 전화번호
         */

        @JvmStatic
        fun makePhoneNumber(phoneNumber: String): String? {
            val regEx = "(\\d{3})(\\d{3,4})(\\d{4})"

            if (!Pattern.matches(regEx, phoneNumber)) return null

            return phoneNumber.replace(regEx.toRegex(), "$1-$2-$3")
        }

        @JvmStatic
        fun makePhoneFormat(number: String, format: String): String {
            return number.replace(format, PHONE_TELL_FORMAT)
        }

        /**
         * GPS OnOff 확인
         *
         * @param c
         * @return
         *
         * android.permission.ACCESS_FINE_LOCATION 퍼미션 체크
         */

        @JvmStatic
        fun isGPSOn(c: Context): Boolean {
            val gps: LocationManager =
                c.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val statusOfGps: Boolean = gps.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (!(gps.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                return false
            }

            return true

        }

        /**
         * 전화번호 구하기
         *
         * <uses-permission android:name="android.permission.READ_PHONE_STATE"/> 퍼미션 체크
         *
         * @param c
         * @return
         */

        @JvmStatic
        fun getPhoneNumber(c: Context): String? {
            val telManager: TelephonyManager = c.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    c,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    c,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    c,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return ""
            }

            if (telManager.line1Number.isNullOrEmpty()) {
                return ""
            } else {
                var number = telManager.line1Number
                if (number.startsWith("+82"))
                    number = number.replace("+82", "0")
                return number
            }

        }

        @JvmStatic
        fun getPhoneOperatorName(c: Context): String? {
            val telManager: TelephonyManager =
                c.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telManager.networkOperatorName
        }

        /**
         * 폰의 MAC ADDRESS 구하기
         * 필요 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
         * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
         * @param c
         * @return
         */
/*
        @JvmStatic
        fun getMacAddress(c: Context): String? {
            val wifiman: WifiManager = c.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiman.connectionInfo
            var wifiEnabled = wifiman.isWifiEnabled


            var strMacAddress = ""

            if (wifiInfo.macAddress != null) {
                strMacAddress = wifiInfo.macAddress
            } else {
                wifiEnabled = true

                if (wifiEnabled && wifiInfo.macAddress != null) {
                    strMacAddress = wifiInfo.macAddress
                }

                wifiEnabled = false
            }
            return strMacAddress
        }
*/
        /**
         * 폰에서 사용하는 WIFI Network 신호 감도 level
         * @param c
         * @param maxLevel 환산할 최고 감도
         * @return
         * 참고 : open static fun calculateSignalLevel(rssi: Int,numLevels: Int): Int -> deprecated
         * open fun calculateSignalLevel(rssi: Int): Int -> API 레벨 30에 추가
         * open fun getMaxSignalLevel(): Int -> 시스템 기본 최대 신호 레벨을 가져옵니다. APi 레벨 30에 추가
         */

        @JvmStatic
        fun getWIFINetworkLevel(c: Context, maxLevel: Int): Int {
            var signalLevel: Int = 0
            val wifiman: WifiManager = c.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiman.connectionInfo
            val rssi: Int = wifiInfo.rssi
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                signalLevel = wifiman.maxSignalLevel
            } else {
                signalLevel = WifiManager.calculateSignalLevel(rssi, maxLevel)
            }


            return signalLevel
        }

        /**
         *  폰 네트워크 연결 상태 확인
         *  필요 : <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
         *
         *  getNetworkInfo API 30에서 deprecated
         *  대안 isDefaultNetworkActive 사용
         * @param
         * @return
         */

        @JvmStatic
        fun isOnline(c: Context): Boolean {
            try {
                val conMan: ConnectivityManager =
                    c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return conMan.isDefaultNetworkActive

            } catch (e: NullPointerException) {
                return false
            }
        }

        /**
         *  연결된 네트워크 타입
         *
         *  @param c
         *  @return TYPE_CONNECTION_WIFI, TYPE_CONNECTION_PHONE, TYPE_CONNECTION_NONE
         *
         *  getNetworkInfo API 23 deprecated
         *  getActiveNetworkInfo API 29 deprecated
         */

        @JvmStatic
        fun connectionType(c: Context): Int { // network 연결 상태 확인
            val conMan: ConnectivityManager =
                c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val nc: NetworkCapabilities? =
                        conMan.getNetworkCapabilities(conMan.activeNetwork)

                    when (nc != null) {
                        nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return TYPE_CONNECTION_WIFI

                        nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return TYPE_CONNECTION_PHONE

                        else -> return TYPE_CONNECTION_NONE
                    }
                } catch (e: NullPointerException) {
                    return TYPE_CONNECTION_NONE
                }
            } else if (Build.VERSION.SDK_INT >= 29) {
                try {
                    val actNw = conMan.activeNetworkInfo
                    if (actNw != null && actNw.isConnectedOrConnecting) {
                        when (actNw.type) {
                            ConnectivityManager.TYPE_WIFI -> return TYPE_CONNECTION_WIFI

                            ConnectivityManager.TYPE_MOBILE -> return TYPE_CONNECTION_PHONE

                            else -> return TYPE_CONNECTION_NONE
                        }
                    }
                } catch (e: NullPointerException) {
                    return TYPE_CONNECTION_NONE
                }

            } else if (Build.VERSION.SDK_INT >= 23) {
                try {
                    val wifi: NetworkInfo.State = conMan.getNetworkInfo(1)!!.state // wifi
                    if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                        return TYPE_CONNECTION_WIFI
                    }
                    val mobile: NetworkInfo.State = conMan.getNetworkInfo(0)!!.state // mobile
                    if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                        return TYPE_CONNECTION_PHONE
                    }

                } catch (e: NullPointerException) {
                    return TYPE_CONNECTION_NONE
                }
            }

            return TYPE_CONNECTION_NONE
        }

        /**
         *  네트워크 모바일 연결 시 다운로드 속도
         *  Kbps
         *  @param context
         *  @return
         */

        @JvmStatic
        fun getMobileDownSpeed(context: Context): Int {
            var downSpeed: Int = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val conMan: ConnectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val nc: NetworkCapabilities =
                    conMan.getNetworkCapabilities(conMan.activeNetwork) as NetworkCapabilities
                downSpeed = nc.linkDownstreamBandwidthKbps
            }
            return downSpeed
        }

        /**
         *  네트워크 모바일 연결 시 업로드 속도
         *  Kbps
         *  @param context
         *  @return
         */

        @JvmStatic
        fun getMobileUpSpeed(context: Context): Int {
            var upSpeed: Int = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val conMan: ConnectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val nc: NetworkCapabilities =
                    conMan.getNetworkCapabilities(conMan.activeNetwork) as NetworkCapabilities
                upSpeed = nc.linkUpstreamBandwidthKbps
            }
            return upSpeed
        }

        /**
         *  현재 날짜를 가져온다
         *
         *  @param type format 형태로 가져온다 (ex. "yyyy.MM.dd HH:mm:ss") 24시 표시 : kk
         *  @return
         */

        @JvmStatic
        fun getCurrentDateTime(type: String): String {
            val formatter = SimpleDateFormat(type, Locale.KOREA)
            val currentTime = Date()
            var time: String = formatter.format(currentTime)

            return time
        }

        /**
         *  하단에 소프트 키가 있는지 유무 판단
         *  LG 폰, 넥서스 폰 류
         *
         *  @param activity
         *  @return 소프트 키 유무 판단
         */

        @JvmStatic
        fun hasBottomSoftKeys(activity: Activity): Boolean {
            var hasSoftwareKeys: Boolean = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                val displayManager = activity.getSystemService<DisplayManager>()
//                val defaultDisplay = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                    var insets: Insets =
                        windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

                    hasSoftwareKeys =
                        windowMetrics.bounds.width() - insets.left - insets.right > 0 || windowMetrics.bounds.height() - insets.top - insets.bottom > 0
                } else {
                    val hasMenuKey: Boolean = ViewConfiguration.get(activity).hasPermanentMenuKey()
                    val hasBackKey: Boolean = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
                    hasSoftwareKeys = !hasMenuKey && !hasBackKey

                }
            }
            return hasSoftwareKeys
        }

        /**
         * TYPE_VIEW_HIDE_ALL : 하단, 상단 view 숨김.
         * TYPE_VIEW_HIDE_ONLY_SOFTKEYS : 하단 소프트키만 숨김.
         * TYPE_VIEW_HIDE_ONLY_NAVIGATION : 상단 뷰만 숨김.
         * TYPE_VIEW_UP_SOFTKEYS : 하단소프트키가 화면 아래에서 위로 올리면 나타남
         * TYPE_VIEW_TOUCH_SOFTKEYS : 하단소프트키가 화면 터치시 나타남
         *
         * @param activity
         * @param type     숨길 기본 view
         * @param viewType 하단 소프트 키가 나타나는 방식
         * @return
         */
        @JvmStatic
        fun setDefaultView(activity: Activity, type: Int, viewType: Int) {
            var uiOption: Int = activity.window.decorView.systemUiVisibility

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowCompat.setDecorFitsSystemWindows(activity.window, false)
                val controller: WindowInsetsController? = activity.window.insetsController

                if (type != TYPE_VIEW_HIDE_ONLY_NAVIGATION || type == TYPE_VIEW_HIDE_ALL) {
                    controller?.hide(WindowInsets.Type.navigationBars())
                }
                if (type != TYPE_VIEW_HIDE_ONLY_SOFTKEYS || type == TYPE_VIEW_HIDE_ALL) {
                    controller?.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                if (viewType == TYPE_VIEW_UP_SOFTKEYS) {
                    controller?.hide(WindowInsets.Type.ime())
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (type != TYPE_VIEW_HIDE_ONLY_NAVIGATION || type == TYPE_VIEW_HIDE_ALL) {
                    uiOption = uiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (type != TYPE_VIEW_HIDE_ONLY_SOFTKEYS || type == TYPE_VIEW_HIDE_ALL) {
                    uiOption = uiOption or View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (viewType == TYPE_VIEW_UP_SOFTKEYS) {
                    uiOption = uiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }

            }

        }


        fun getDecorView(activity: Activity): View {
            return activity.window.decorView
        }

        /**
         * --------------------------------------------------------------------------------
         */


        /**
         * 앱의 버전 이름을 받는다
         *
         * @param c Context
         * @return 버전 이름
         */
        @JvmStatic
        fun getVersionName(c: Context): String {
            var version: String = "0"

            try {
                val i: PackageInfo = c.packageManager.getPackageInfo(c.packageName, 0)
                version = i.versionName

            } catch (e: PackageManager.NameNotFoundException) {

            }
            return version
        }

        /**
         * 앱의 버전 코드를 받는다
         *
         * @param c Context
         * @return 버전 코드
         */
        @JvmStatic
        fun getVersionCode(c: Context): Int {
            var code: Int = 0
            try {
                val i: PackageInfo = c.packageManager.getPackageInfo(c.packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    code = i.longVersionCode.toInt()
                } else {
                    code = i.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
            }
            return code
        }

        /**
         *  클립보드 복사
         *
         *  @param context
         *  @param text
         */
        @JvmStatic
        fun setClipboard(context: Context, text: String) {
            val sdk_Version: Int = android.os.Build.VERSION.SDK_INT
            if (sdk_Version < android.os.Build.VERSION_CODES.HONEYCOMB) {
                val clipboard: ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = text
            } else {
                val clipboard: android.content.ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Text Label", text)
                clipboard.setPrimaryClip(clip)
            }
        }

        /**
         *  해시키 보기
         *
         *  @param c
         */
        fun getHashKey(c: Context) {
            var packageInfo: PackageInfo? = null
            if (packageInfo == null) {
                Log.e("KeyHash", "KeyHash:null")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    val packageInfo = c.packageManager.getPackageInfo(
                        c.packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    )
                    val signatures = packageInfo.signingInfo.apkContentsSigners
                    val md = MessageDigest.getInstance("SHA")
                    for (signature in signatures) {
                        md.update(signature.toByteArray())
                        Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                    }
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    packageInfo = c.packageManager.getPackageInfo(
                        c.packageName,
                        PackageManager.GET_SIGNATURES
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                for (signature in packageInfo!!.signatures) {
                    try {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                    } catch (e: NoSuchAlgorithmException) {
                        Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
                    }
                }
            }
        }

    }
}
