package com.spear.iriskt.android.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.spear.iriskt.R
import com.spear.iriskt.android.AndroidBotManager

/**
 * 앱 설정을 관리하는 액티비티
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var botManager: AndroidBotManager
    private lateinit var prefs: SharedPreferences
    
    private lateinit var switchAutoStart: Switch
    private lateinit var switchNotification: Switch
    private lateinit var editTextBotName: EditText
    private lateinit var editTextLogLimit: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonReset: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "설정"

        botManager = AndroidBotManager.getInstance(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // UI 요소 초기화
        switchAutoStart = findViewById(R.id.switchAutoStart)
        switchNotification = findViewById(R.id.switchNotification)
        editTextBotName = findViewById(R.id.editTextBotName)
        editTextLogLimit = findViewById(R.id.editTextLogLimit)
        buttonSave = findViewById(R.id.buttonSave)
        buttonReset = findViewById(R.id.buttonReset)

        // 현재 설정 로드
        loadSettings()

        // 버튼 이벤트 설정
        buttonSave.setOnClickListener {
            saveSettings()
        }

        buttonReset.setOnClickListener {
            resetSettings()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 현재 설정을 로드합니다.
     */
    private fun loadSettings() {
        switchAutoStart.isChecked = prefs.getBoolean("bot_auto_start", false)
        switchNotification.isChecked = prefs.getBoolean("bot_show_notification", true)
        editTextBotName.setText(prefs.getString("bot_name", "Iris-kt Bot"))
        editTextLogLimit.setText(prefs.getInt("bot_log_limit", 1000).toString())
    }

    /**
     * 설정을 저장합니다.
     */
    private fun saveSettings() {
        try {
            val logLimit = editTextLogLimit.text.toString().toIntOrNull() ?: 1000
            if (logLimit < 100 || logLimit > 10000) {
                Toast.makeText(this, "로그 제한은 100~10000 사이여야 합니다.", Toast.LENGTH_SHORT).show()
                return
            }

            val botName = editTextBotName.text.toString().trim()
            if (botName.isBlank()) {
                Toast.makeText(this, "봇 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return
            }

            prefs.edit().apply {
                putBoolean("bot_auto_start", switchAutoStart.isChecked)
                putBoolean("bot_show_notification", switchNotification.isChecked)
                putString("bot_name", botName)
                putInt("bot_log_limit", logLimit)
                apply()
            }

            // 봇 매니저에 설정 적용
            botManager.updateSettings()

            Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "설정 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 설정을 기본값으로 초기화합니다.
     */
    private fun resetSettings() {
        prefs.edit().apply {
            putBoolean("bot_auto_start", false)
            putBoolean("bot_show_notification", true)
            putString("bot_name", "Iris-kt Bot")
            putInt("bot_log_limit", 1000)
            apply()
        }

        loadSettings()
        Toast.makeText(this, "설정이 초기화되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
