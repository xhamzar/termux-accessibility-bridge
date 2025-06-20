package com.namaanda.termuxbridge

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        val btnOpenSettings: Button = findViewById(R.id.btnOpenSettings)

        btnOpenSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

    private fun updateServiceStatus() {
        if (isAccessibilityServiceEnabled()) {
            tvStatus.text = "Status Layanan: AKTIF"
            tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            tvStatus.text = "Status Layanan: TIDAK AKTIF"
            tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceId = "$packageName/com.namaanda.termuxbridge.accessibility.MyAccessibilityService"
        val settingValue = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return settingValue?.let {
            val stringColonSplitter = TextUtils.SimpleStringSplitter(':')
            stringColonSplitter.setString(it)
            while (stringColonSplitter.hasNext()) {
                if (stringColonSplitter.next().equals(serviceId, ignoreCase = true)) {
                    return true
                }
            }
            false
        } ?: false
    }
}