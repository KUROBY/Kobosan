package com.kobo.san

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.Random
import android.app.Notification
import android.widget.ImageButton
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context.VIBRATOR_SERVICE




class FloatingWidgetService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingWidgetView: View
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isWidgetVisible = false

    private val daftarNama = listOf(
        "Ahmad", "Budi", "Chandra", "Dewi", "Edi", "Fani", "Gita", "Hani", "Irfan", "Janet",
        "Kartika", "Luki", "Mira", "Nadia", "Oscar", "Pia", "Qori", "Rita", "Satria", "Tania",
        "Uci", "Vina", "Wira", "Xena", "Yoga", "Zara" , "Adi" , "Ayu" , "Fitri" , "Taufik" ,
        "Dadi" , "Wahyu" , "Deni" , "Agustin"  , "Rika" , "Palamasari" , "Silitonga" , "Ariyani" ,
        "Sarawati" , "Akmal" , "Rahmat" , "Saputra" , "Ramayana" , "Nur Aam" , "Ramdani" ,
        "Ubaydillah" , "Konaah" , "Mansyir" , "Latifa" , "Noval" , "Retno" , "Umi" , "Ardi" , "Yogi"
        , "yudi" , "Harun"

    )

    private val hpPrefixes = arrayOf("089", "081", "085", "088", "087", "083")

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        floatingWidgetView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

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

        button5.visibility = View.GONE  // Sembunyikan button5 secara awal

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        val nameGenerator = NameGenerator()

        button1.setOnClickListener {
            val generatedName = nameGenerator.generateRandomName()
            val nameWithSuffix = "$generatedName ${AppGlobals.customSuffix}"
            showToastAndCopyToClipboard(nameWithSuffix)
            makeDeviceVibrate(vibrator)
        }



        button2.setOnClickListener {
            val generatedHp = generateHp()
            showToastAndCopyToClipboard(generatedHp)
            makeDeviceVibrate(vibrator)
        }

        button3.setOnClickListener {
            val generatedAlamat = generateAlamat()
            showToastAndCopyToClipboard(generatedAlamat)
            makeDeviceVibrate(vibrator)
        }

        button4.setOnClickListener {
            showToastAndCopyToClipboard(AppGlobals.customString)
            makeDeviceVibrate(vibrator)
        }

        button5.setOnClickListener {
            stopSelf()
            makeDeviceVibrate(vibrator)
        }

        button6.setOnClickListener {
            val generatedEmail = EmailGenerator().generateRandomEmail()
            showToastAndCopyToClipboard(generatedEmail)
            makeDeviceVibrate(vibrator)
        }

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
                    params.x = initialX + (initialTouchX.toInt() - event.rawX.toInt()) // membalikkan arah gerakan pada sumbu x
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

        // Tetapkan notifikasi foreground untuk mempertahankan layanan di latar belakang
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
        if (floatingWidgetView.isAttachedToWindow) {
            windowManager.removeView(floatingWidgetView)
        }
    }

    private fun makeDeviceVibrate(vibrator: Vibrator) {
        // Jika versi perangkat lebih dari atau sama dengan API 26
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15) // untuk perangkat dengan versi dibawah API 26
        }
    }

    private fun toggleWidgetVisibility() {
        if (isWidgetVisible) {
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button1).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button2).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button3).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button4).visibility = View.GONE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button5).visibility = View.VISIBLE  // Tampilkan button5 ketika yang lain disembunyikan
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button6).visibility = View.GONE
        } else {
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button1).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button2).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button3).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button4).visibility = View.VISIBLE
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button5).visibility = View.GONE  // Sembunyikan button5 ketika yang lain ditampilkan
            floatingWidgetView.findViewById<ImageButton>(R.id.floating_button6).visibility = View.VISIBLE
        }
        isWidgetVisible = !isWidgetVisible
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
        val pattern = " Jl.#%$%$% @ Arah No.@@ Rt@@ Rw@@ " + AppGlobals.customAddress + " Tempat #%$%$%"
        val directions = listOf("timur", "selatan", "barat", "utara")
        val placement = listOf("Toko", "Store", "TBK", "Kantor" ,"Kosan" , "Gudang" , "Pabrik" ,"GOR")

        var result = pattern

        // Replace the first occurrence of "raya" with a random direction
        val randomDirection = directions.random()
        result = result.replaceFirst("Arah", randomDirection)

        // Replace the first occurrence of "tempat" with a random placement
        val randomPlacement = placement.random()
        result = result.replaceFirst("Tempat", randomPlacement)

        // Generate random numbers for No., Rt, and Rw
        val randomNo = (1..99).random()
        val randomRt = (1..15).random()
        val randomRw = (1..15).random()

        result = result.replaceFirst("@@", String.format("%02d", randomNo))
        result = result.replaceFirst("@@", String.format("%02d", randomRt))
        result = result.replaceFirst("@@", String.format("%02d", randomRw))

        var numGenerated = 0
        for (i in result.indices) {
            val c = result[i]
            if (c == '#') {
                if (numGenerated < 3) {
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

    // Kelas EmailGenerator untuk menghasilkan alamat email secara acak
    private inner class EmailGenerator {
        fun generateRandomEmail(): String {
            val email = getRandomString(11) + getRandomNumber(2) + AppGlobals.customDomain
            return email
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
    }

    private fun showEmailDomainInputDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this).create()
        val editText = EditText(this)
        val container = android.widget.FrameLayout(this)
        val params = android.widget.FrameLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editText.layoutParams = params
        editText.hint = "Masukkan domain email baru (misal: @gmail.com)"
        editText.setText(AppGlobals.customDomain)
        container.addView(editText)
        alertDialog.setView(container)
        alertDialog.setTitle("Ubah Domain Email")
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            val newDomain = editText.text.toString()
            if (newDomain.isNotBlank()) {
                AppGlobals.customDomain = newDomain
            }
        }
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}
