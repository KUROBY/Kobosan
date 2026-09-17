package com.kvoxkobo.san

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.Random
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.io.File
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class MainActivity : AppCompatActivity() {

    private val daftarNama = listOf(
        "Ahmad", "Budi", "Chandra", "Dewi", "Edi", "Fani", "Gita", "Hani", "Irfan", "Janet",
        "Kartika", "Luki", "Mira", "Nadia", "Oscar", "Pia", "Qori", "Rita", "Satria", "Tania",
        "Uci", "Vina", "Wira", "Xena", "Yoga", "Zara"
    )

    private val REQUEST_OVERLAY_PERMISSION = 123

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            startFloatingService()
        } else {
            Toast.makeText(this, "Izin overlay tidak diberikan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestRootAccess() {
        try {
            val process = Runtime.getRuntime().exec("su")
            process.inputStream // Untuk memastikan proses su dieksekusi
            Toast.makeText(this, "Izin root diminta", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Izin root ditolak", Toast.LENGTH_SHORT).show()
            Log.e("RootAccess", "Error requesting root access", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Meminta izin untuk WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestManageExternalStoragePermission()
        } else {
            createFolder() // Buat folder jika di bawah Android 11
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        val floatingButton = findViewById<Button>(R.id.floating_button)
        floatingButton.setOnClickListener {
            checkOverlayPermission()
        }


    }


    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            startFloatingService()
        }
    }

    private fun startFloatingService() {
        startService(Intent(this, FloatingWidgetService::class.java))
        Toast.makeText(this, "Floating diaktifkan", Toast.LENGTH_SHORT).show()
    }

    private fun generateRandomName(): String {
        val randomIndex = Random().nextInt(daftarNama.size)
        return daftarNama[randomIndex]
    }

    private fun generateRandomNamesWithCustomSuffix(): String {
        val randomName1 = generateRandomName()
        val randomName2 = generateRandomName()
        return "$randomName1 $randomName2 ${AppGlobals.customSuffix}"
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("random_name", text)
        clipboard.setPrimaryClip(clip)
    }

    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.button1 -> {
                val generatedNames = generateRandomNamesWithCustomSuffix()
                Toast.makeText(this, "Nama acak: $generatedNames", Toast.LENGTH_SHORT).show()
                copyToClipboard(generatedNames)
            }
            R.id.button3 -> {
                showSuffixInputDialog()
            }
            R.id.button4 -> {
                showCustomStringInputDialog()
            }
            R.id.button5 -> {
                showCustomDomainInputDialog()
            }
            R.id.button6 -> {
                showCustomAddresInputDialog()
            }
        }
    }

    fun onUbahLink1Click(view: View) {
        val editText = EditText(this).apply {
            hint = "Masukkan link 1"
            setText(AppGlobals.customLink1)
        }
        AlertDialog.Builder(this)
            .setTitle("Ubah Link 1")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                AppGlobals.customLink1 = editText.text.toString()
                Toast.makeText(this, "Link 1 berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    fun onUbahLink2Click(view: View) {
        val editText = EditText(this).apply {
            hint = "Masukkan link 2"
            setText(AppGlobals.customLink2)
        }
        AlertDialog.Builder(this)
            .setTitle("Ubah Link 2")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                AppGlobals.customLink2 = editText.text.toString()
                Toast.makeText(this, "Link 2 berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    fun onUbahLink3Click(view: View) {
        val editText = EditText(this).apply {
            hint = "Masukkan link 3"
            setText(AppGlobals.customLink3)
        }
        AlertDialog.Builder(this)
            .setTitle("Ubah Link 3")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                AppGlobals.customLink3 = editText.text.toString()
                Toast.makeText(this, "Link 3 berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    fun onUbahLink4Click(view: View) {
        val editText = EditText(this).apply {
            hint = "Masukkan link 4"
            setText(AppGlobals.customLink4)
        }
        AlertDialog.Builder(this)
            .setTitle("Ubah Link 4")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                AppGlobals.customLink4 = editText.text.toString()
                Toast.makeText(this, "Link 4 berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    fun onUbahHttpsClick(view: View) {
        val editText = EditText(this).apply {
            hint = "Masukkan URL baru (misal: https://generator.email/)"
            setText(AppGlobals.customHttps)
        }

        AlertDialog.Builder(this)
            .setTitle("Ubah HTTPS Email Generator")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                AppGlobals.customHttps = editText.text.toString()
                Toast.makeText(this, "Https berhasil diubah", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:${packageName}")
                startActivity(intent)
            }
        } else {
            createFolder() // Memanggil fungsi untuk membuat folder jika di bawah Android 11
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, Anda dapat membuat folder sekarang
                createFolder() // Panggil metode untuk membuat folder di sini
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //FOLDERYAHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
    private fun createFolder() {
        val folderName = "SUNTIK"
        val directory = File(Environment.getExternalStorageDirectory(), folderName)
        if (!directory.exists()) {
            if (directory.mkdir()) {
                Toast.makeText(this, "Folder $folderName berhasil dibuat di ${directory.path}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal membuat folder $folderName", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Folder $folderName sudah ada di ${directory.path}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuffixInputDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editText.layoutParams = params
        editText.hint = "Masukkan akhiran baru"
        editText.setText(AppGlobals.customSuffix)
        container.addView(editText)
        alertDialog.setView(container)
        alertDialog.setTitle("Ubah Akhiran")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            AppGlobals.customSuffix = editText.text.toString()
            saveDataToSharedPreferences() // Save data to SharedPreferences
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showCustomStringInputDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editText.layoutParams = params
        editText.hint = "Masukkan Password baru"
        editText.setText(AppGlobals.customString)
        container.addView(editText)
        alertDialog.setView(container)
        alertDialog.setTitle("Ubah Password")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            AppGlobals.customString = editText.text.toString()
            saveDataToSharedPreferences() // Save data to SharedPreferences
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showCustomDomainInputDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editText.layoutParams = params
        editText.hint = "Masukkan custom domain baru (misal: @gmail.com)"
        editText.setText(AppGlobals.customDomain)
        container.addView(editText)
        alertDialog.setView(container)
        alertDialog.setTitle("Ubah Custom Domain")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            AppGlobals.customDomain = editText.text.toString()
            saveDataToSharedPreferences() // Save data to SharedPreferences
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showCustomAddresInputDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        val editText = EditText(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editText.layoutParams = params
        editText.hint = "Masukkan custom Code baru (misal: CODE)"
        editText.setText(AppGlobals.customAddress)
        container.addView(editText)
        alertDialog.setView(container)
        alertDialog.setTitle("Ubah Custom CODE Alamat")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            AppGlobals.customAddress = editText.text.toString()
            saveDataToSharedPreferences() // Save data to SharedPreferences
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private val sharedPreferences by lazy {
        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    private fun saveDataToSharedPreferences() {
        with(sharedPreferences.edit()) {
            putString("customSuffix", AppGlobals.customSuffix)
            putString("customDomain", AppGlobals.customDomain)
            putString("customString", AppGlobals.customString)
            putString("customAddres", AppGlobals.customAddress)
            apply()
        }
    }

    private fun isDeviceRooted(): Boolean {
        Log.d("MainActivity", "Memeriksa akses root")
        val paths = arrayOf("/sbin/su", "/system/bin/su", "/system/xbin/su")
        for (path in paths) {
            Log.d("MainActivity", "Memeriksa path: $path")
            if (File(path).exists()) {
                Log.d("MainActivity", "Root ditemukan di: $path")
                return true
            }
        }
        Log.d("MainActivity", "Root tidak ditemukan")
        return false
    }


}
