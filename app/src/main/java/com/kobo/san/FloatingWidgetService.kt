package com.kvoxkobo.san

import android.app.Notification
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import java.util.Random

class FloatingWidgetService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingWidgetView: View
    private lateinit var vibrator: Vibrator
    private val mainHandler = Handler(Looper.getMainLooper())

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isWidgetVisible = false

    private val daftarNama = listOf(
        "Ahmad", "Budi", "Chandra", "Dewi", "Edi", "Fani", "Gita", "Hani", "Irfan", "Janet",
        "Kartika", "Luki", "Mira", "Nadia", "Oscar", "Pia", "Qori", "Rita", "Satria", "Tania",
        "Uci", "Vina", "Wira", "Xena", "Yoga", "Zara", "Adi", "Ayu", "Fitri", "Taufik",
        "Dadi", "Wahyu", "Deni", "Agustin", "Rika", "Palamasari", "Silitonga", "Ariyani",
        "Sarawati", "Akmal", "Rahmat", "Saputra", "Ramayana", "Nur Aam", "Ramdani",
        "Ubaydillah", "Konaah", "Mansyir", "Latifa", "Noval", "Retno", "Umi", "Ardi", "Yogi",
        "yudi", "Harun"
    )

    private val hpPrefixes = arrayOf("089", "081", "085", "088", "087", "083")

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        floatingWidgetView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.END
        windowManager.addView(floatingWidgetView, params)

        val button1 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button1)
        val button2 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button2)
        val button3 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button3)
        val button4 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button4)
        val button5 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button5)
        val button6 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button6)
        val button7 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button7)
        val buttonLink1 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link1)
        val buttonLink2 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link2)
        val buttonLink3 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link3)
        val buttonLink4 = floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link4)

        // ✅ SEMBUNYIKAN tombol secondary saat awal
        button5.visibility = View.GONE
        button7.visibility = View.GONE
        buttonLink1.visibility = View.GONE
        buttonLink2.visibility = View.GONE
        buttonLink3.visibility = View.GONE
        buttonLink4.visibility = View.GONE

        val nameGenerator = NameGenerator()

        // ✅ Button 1 - Generate Nama
        button1.setOnClickListener {
            val generatedName = nameGenerator.generateRandomName()
            val nameWithSuffix = "$generatedName ${AppGlobals.customSuffix}"
            showToastAndCopyToClipboard(nameWithSuffix)
            makeDeviceVibrate()
        }

        // ✅ Button 2 - Generate HP
        button2.setOnClickListener {
            val generatedHp = generateHp()
            showToastAndCopyToClipboard(generatedHp)
            makeDeviceVibrate()
        }

        // ✅ Button 3 - Generate Alamat
        button3.setOnClickListener {
            val generatedAlamat = generateAlamat()
            showToastAndCopyToClipboard(generatedAlamat)
            makeDeviceVibrate()
        }

        // ✅ Button 4 - Copy Password
        button4.setOnClickListener {
            showToastAndCopyToClipboard(AppGlobals.customString)
            makeDeviceVibrate()
        }

        // ✅ Button 5 - Stop Service
        button5.setOnClickListener {
            stopSelf()
            makeDeviceVibrate()
        }

        // ✅ Button 6 - Generate Email
        button6.setOnClickListener {
            val generatedEmail = EmailGenerator().generateRandomEmail()
            showToastAndCopyToClipboard(generatedEmail)
            makeDeviceVibrate()
        }

        // ✅ Button 7 - Airplane Mode
        var isAirplaneModeOn = false
        button7.setOnClickListener {
            Thread {
                try {
                    val newState = if (isAirplaneModeOn) "0" else "1"

                    Runtime.getRuntime().exec(arrayOf(
                        "su", "-c",
                        "settings put global airplane_mode_on $newState"
                    ))

                    Thread.sleep(100)

                    Runtime.getRuntime().exec(arrayOf(
                        "su", "-c",
                        "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state $newState"
                    ))

                    isAirplaneModeOn = !isAirplaneModeOn

                    mainHandler.post {
                        Toast.makeText(
                            this@FloatingWidgetService,
                            if (!isAirplaneModeOn) "Airplane Mode OFF" else "Airplane Mode ON",
                            Toast.LENGTH_SHORT
                        ).show()
                        makeDeviceVibrate()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mainHandler.post {
                        Toast.makeText(
                            this@FloatingWidgetService,
                            "Gagal mengubah mode pesawat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.start()
        }

        // ✅ Button Link 1-4
        buttonLink1.setOnClickListener {
            openLink(AppGlobals.customLink1)
            makeDeviceVibrate()
        }

        buttonLink2.setOnClickListener {
            openLink(AppGlobals.customLink2)
            makeDeviceVibrate()
        }

        buttonLink3.setOnClickListener {
            openLink(AppGlobals.customLink3)
            makeDeviceVibrate()
        }

        buttonLink4.setOnClickListener {
            openLink(AppGlobals.customLink4)
            makeDeviceVibrate()
        }

        // ✅ Touch Listener untuk drag
        floatingWidgetView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (initialTouchX.toInt() - event.rawX.toInt())
                    params.y = initialY + (event.rawY.toInt() - initialTouchY.toInt())
                    windowManager.updateViewLayout(floatingWidgetView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val xDiff = Math.abs(params.x - initialX)
                    val yDiff = Math.abs(params.y - initialY)
                    if (xDiff < 5 && yDiff < 5) {
                        toggleWidgetVisibility()
                    }
                    true
                }
                else -> false
            }
        }

        // ✅ Foreground Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = Notification.Builder(this, "CHANNEL_ID")
                .setContentTitle("Floating Widget")
                .setContentText("Floating Widget sedang berjalan")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::floatingWidgetView.isInitialized && floatingWidgetView.parent != null) {
                windowManager.removeView(floatingWidgetView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun makeDeviceVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }

    private fun toggleWidgetVisibility() {
        if (isWidgetVisible) {
            // Mode NORMAL - tampilkan tombol utama
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button1).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button2).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button3).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button4).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button5).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button6).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button7).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link1).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link2).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link3).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link4).visibility = View.GONE
        } else {
            // Mode TOGGLE - tampilkan tombol secondary
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button1).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button2).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button3).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button4).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button5).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button6).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button7).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link1).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link2).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link3).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button_link4).visibility = View.VISIBLE
        }
        isWidgetVisible = !isWidgetVisible
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToastAndCopyToClipboard(text: String) {
        showToast(text)
        copyToClipboard(text)
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("random_text", text)
        clipboard.setPrimaryClip(clip)
    }

    private inner class NameGenerator {
        fun generateRandomName(): String {
            return getRandomString(6)
        }

        private fun getRandomString(length: Int): String {
            val pattern = "#%$%$%"
            val stringBuilder = StringBuilder()
            for (i in 0 until pattern.length) {
                val c = pattern[i]
                stringBuilder.append(
                    when (c) {
                        '#' -> generateRandomChar()
                        '!' -> generateRandomCharA()
                        '$' -> generateRandomchar()
                        '%' -> generateRandomcharA()
                        '@' -> generateRandomNumber()
                        else -> c
                    }
                )
            }
            return stringBuilder.toString().substring(0, length)
        }
    }

    private inner class EmailGenerator {
        fun generateRandomEmail(): String {
            return AppGlobals.customHttps + getRandomString(11) + getRandomNumber(2) + AppGlobals.customDomain
        }

        private fun getRandomString(length: Int): String {
            val pattern = "#%$%$%$%$%$%@@@"
            val stringBuilder = StringBuilder()
            for (i in 0 until pattern.length) {
                val c = pattern[i]
                stringBuilder.append(
                    when (c) {
                        '#' -> generateRandomChar()
                        '!' -> generateRandomCharA()
                        '$' -> generateRandomchar()
                        '%' -> generateRandomcharA()
                        '@' -> generateRandomNumber()
                        else -> c
                    }
                )
            }
            return stringBuilder.toString().substring(0, length)
        }
    }

    private fun generateHp(): String {
        val pattern = "08@@@@@@@@@@@"
        var result = ""
        for (i in pattern.indices) {
            val c = pattern[i]
            if (c == '@') {
                var randomDigit: Char
                do {
                    randomDigit = generateRandomNumber()
                } while (i == 2 && (randomDigit == '6' || randomDigit == '4'))
                result += randomDigit
            } else {
                result += c
            }
        }
        return result
    }

    private fun generateAlamat(): String {
        val pattern = "#%$%$%$%$% #%$%$%$% Jl.#%$%$% #%$%$%$%$ @ Arah #%$%$%$%$% No.@@@ Rt@@@ Rw@@@ "+ AppGlobals.customAddress +" Tempat #%$%$%$% #%$%$%"
        val directions = listOf("timur", "selatan", "barat", "utara")
        val placement = listOf("Toko", "Store", "TBK", "Kantor", "Kosan", "Gudang", "Pabrik", "GOR")

        var result = pattern
        val randomDirection = directions.random()
        result = result.replaceFirst("Arah", randomDirection)

        val randomPlacement = placement.random()
        result = result.replaceFirst("Tempat", randomPlacement)

        val randomNo = (1..99).random()
        val randomRt = (1..15).random()
        val randomRw = (1..31).random()

        result = result.replaceFirst("@@@", String.format("%03d", randomNo))
        result = result.replaceFirst("@@@", String.format("%03d", randomRt))
        result = result.replaceFirst("@@@", String.format("%03d", randomRw))

        var numGenerated = 0
        for (i in result.indices) {
            val c = result[i]
            if (c == '#') {
                if (numGenerated < 7) {
                    result = result.replaceFirst("#", generateRandomChar().toString())
                    numGenerated++
                }
            } else if (c == '!') {
                result = result.replaceFirst("!", generateRandomCharA().toString())
            } else if (c == '$') {
                result = result.replaceFirst("$", generateRandomchar().toString())
            } else if (c == '%') {
                result = result.replaceFirst("%", generateRandomcharA().toString())
            } else if (c == '@') {
                result = result.replaceFirst("@", generateRandomNumber().toString())
            }
        }

        return result
    }

    private fun generateRandomChar(): Char {
        val alphabet = "BCDFGHJKLMNPRSTVWYZ"
        return alphabet.random()
    }

    private fun generateRandomCharA(): Char {
        val alphabet = "AEIOU"
        return alphabet.random()
    }

    private fun generateRandomchar(): Char {
        val alphabet = "bcdfghjklmnprstvwyz"
        return alphabet.random()
    }

    private fun generateRandomcharA(): Char {
        val alphabet = "aeiou"
        return alphabet.random()
    }

    private fun generateRandomNumber(): Char {
        val alphabet = "12356789"
        return alphabet.random()
    }

    private fun getRandomNumber(length: Int): String {
        val numbers = "0123456789"
        val random = Random()
        val stringBuilder = StringBuilder()
        for (i in 0 until length) {
            stringBuilder.append(numbers[random.nextInt(numbers.length)])
        }
        return stringBuilder.toString()
    }

    private fun openLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.post {
                Toast.makeText(this@FloatingWidgetService, "Tidak bisa membuka link", Toast.LENGTH_SHORT).show()
            }
        }
    }
}