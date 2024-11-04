package com.kobo.san

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floatingButton = findViewById<Button>(R.id.floating_button)
        floatingButton.setOnClickListener {
            checkOverlayPermission()
        }

        // Restore data from SharedPreferences
        AppGlobals.customSuffix = sharedPreferences.getString("customSuffix", "CODE").toString()
        AppGlobals.customDomain = sharedPreferences.getString("customDomain", "@hotmail.com").toString()
        AppGlobals.customString = sharedPreferences.getString("customString", "kaina1122@").toString()
        AppGlobals.customAddress = sharedPreferences.getString("customAddress", "CODE").toString()
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


}
