package com.friendsmon.fmlibrary.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.random.Random

class FMTextUtils {

    companion object {

        @JvmStatic
        fun strInsert(str: String, pos: Int, insertStr: String): String {
            val strBuilder = StringBuilder()
            strBuilder.append(str.substring(0, pos))
            strBuilder.append(insertStr)
            strBuilder.append(str.substring(pos))

            return strBuilder.toString()
        }

        /**
         * Android secure coding To String
         * equals()와 hashCode() 둘다 정의 필요
         *
         * @param str
         * @param obj
         * @return boolean
         */
        @JvmStatic
        fun dmEqual(str: String, obj: Object): Boolean {
            if (str == null || obj == null) {
                return false
            }

            var i1: Int = str.hashCode()
            var i2: Int = obj.hashCode()

            if (i1 == i2) {
                return str.equals(obj)
            } else {
                return false
            }
        }

        /**
         * sha256 암호화 (복호화 불가)
         *
         * @param str 암호화할 값
         * @return 암호화된 값
         */

        @JvmStatic
        fun getSHA256(str: String): String {
            var rtnSHA: String = ""
            try {
                val sh: MessageDigest = MessageDigest.getInstance("SHA-256")
                sh.update(str.toByteArray())
                val byteData = sh.digest()
                val sb = StringBuffer()

                for (i in byteData.indices) {
                    sb.append(
                        Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16)
                            .substring(1)
                    )
                }

                rtnSHA = sb.toString()
            } catch (e: NoSuchAlgorithmException) {
                rtnSHA = null.toString()
            }
            return rtnSHA
        }

        /**
         *  AES 암호화 (복호화 기능)
         *
         *  @param key 암호화 할 key 값
         *  @param text 암호화될 value
         *  @return 암호화된 값
         *  @throws Exception
         */
        @JvmStatic
        fun getEncrypt(key: String, text: String): String? {
            var value: String = ""

            try {
                val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val keyBytes = ByteArray(16)
                val b: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
                var len: Int = b.size
                if (len > keyBytes.size) len = keyBytes.size
                System.arraycopy(b, 0, keyBytes, 0, len)
                val keySpec = SecretKeySpec(keyBytes, "AES")
                val ivSpec = IvParameterSpec(keyBytes)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

                var results = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))

                value = Base64.encodeToString(results, 0)
            } catch (e: Exception) {
                return null
            }

            return value
        }

        /**
         *  랜덤한 int 값 전달
         *
         *  @return 랜덤한 값 전달
         */
        @JvmStatic
        fun getRandomInt(): Int {
            var r = Random(Date().time)

            return (r.nextInt() % 6) + 1
        }

        /**
         *  숫자를 가격표시 처럼 표현
         *
         *  @param str 숫자 String
         *  @return 콤마 추가 String
         */
        @JvmStatic
        fun makeStringComma(str: String): String {
            if (str.isEmpty()) return ""

            val value: Long = str.toLong()
            val format = DecimalFormat("###,###")
            return format.format(value)
        }

        /**
         *  중복된 3자 이상 사용 여부
         *
         *  @param str 검사할 String
         *  @return true : 중복 사용, false : 중복 미사용
         */
        @JvmStatic
        fun checkDuplicate3Character(str: String): Boolean {
            val p: Int = str.length
            val b = str.toByteArray()
            for (i in 0 until ((p * 2) / 3)) {
                val b1 = b[i + 1]
                val b2 = b[i + 2]
                if ((b[i] == b1) and (b[i] == b2)) {
                    return true
                } else {
                    continue
                }
            }
            return false
        }

        /**
         *  숫자, 영문, 특수문자 사용여부
         *
         *  @param passwd 검사할 문자
         *  @return 사용한 문자 갯수
         */
        @JvmStatic
        fun useStringCheck(passwd: String): Int {
            var varDigit = 0
            var varAlpha = 0
            var varHex = 0
            var varSum = 0
            for (i in passwd.indices) {
                var index: Char = passwd[i]

                if (index in '0'..'9') {
                    varDigit = 1
                } else if ((index in 'a'..'z') || (index in 'A'..'Z')) {
                    varAlpha = 1
                } else if (index == '!' || index == '@' || index == '$' || index == '%' || index == '^' || index == '&' || index == '*') {
                    varHex = 1
                }
            }
            varSum = varDigit + varAlpha + varHex

            return varSum
        }

        /**
         * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
         *
         * @param date
         * @param dateType
         * @return
         */

        @JvmStatic
        fun getDateDay(date: String, dateType: String): String {
            var day = ""
            var dateFormat = SimpleDateFormat(dateType)
            var nDate: Date = dateFormat.parse(date)
            var cal: Calendar = Calendar.getInstance()
            cal.time = nDate
            var dayNum: Int = cal.get(Calendar.DAY_OF_WEEK)
            when (dayNum) {
                1 -> day = "일"
                2 -> day = "월"
                3 -> day = "화"
                4 -> day = "수"
                5 -> day = "목"
                6 -> day = "금"
                7 -> day = "토"
            }
            return day
        }

        /**
         * 자릿수 만큼 앞에 '0' 붙이기
         *
         * @param digit 원하는 자릿수
         * @param num   변경할 숫자
         * @return 자릿수 만큼 변경된 값
         */
        fun getZeroDigit(digit: Int, num: Int): String {
            var digitFormat: String = "%0Xd".replace("X", digit.toString())
            return String.format(digitFormat, num)
        }

        /**
         *  이메일 유효성 검사
         *  @param email
         *  @return
         */
        fun isValidEmail(email: String): Boolean {
            var err: Boolean = false
            var regex: String = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+\$"
            var p: Pattern = Pattern.compile(regex)
            var m: Matcher = p.matcher(email)
            if (m.matches()) {
                err = true
            }

            return err
        }

        /**
         *  전화번호 유효성 검사
         *  @param phone
         *  @return
         */
        fun isValidPhone(phone: String): Boolean {
            var err: Boolean = false
            var regex: String = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}\$"
            var p: Pattern = Pattern.compile(regex)
            var m: Matcher = p.matcher(phone)
            if (m.matches()) {
                err = true
            }

            return true
        }

    }
}