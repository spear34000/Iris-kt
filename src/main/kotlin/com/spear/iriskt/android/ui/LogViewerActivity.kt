package com.spear.iriskt.android.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.spear.iriskt.R
import com.spear.iriskt.android.AndroidBotManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*

class LogViewerActivity : AppCompatActivity() {
    private lateinit var textViewLog: TextView
    private lateinit var buttonClear: Button
    private lateinit var buttonRefresh: Button
    private var isAutoRefresh = true
    private var logFile: File? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "로그 뷰어"
        
        textViewLog = findViewById(R.id.textViewLog)
        buttonClear = findViewById(R.id.buttonClear)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        
        // 로그 파일 경로 설정
        logFile = File(getExternalFilesDir("logs"), "bot.log")
        
        // 버튼 이벤트 설정
        buttonClear.setOnClickListener {
            clearLog()
        }
        
        buttonRefresh.setOnClickListener {
            isAutoRefresh = !isAutoRefresh
            buttonRefresh.text = if (isAutoRefresh) "자동 갱신 중지" else "자동 갱신 시작"
            if (isAutoRefresh) {
                startAutoRefresh()
            }
        }
        
        // 초기 로그 로드
        loadLog()
        
        // 자동 갱신 시작
        startAutoRefresh()
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
    
    override fun onPause() {
        super.onPause()
        isAutoRefresh = false
    }
    
    override fun onResume() {
        super.onResume()
        isAutoRefresh = true
        startAutoRefresh()
    }
    
    /**
     * 로그를 로드합니다.
     */
    private fun loadLog() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val log = if (logFile?.exists() == true) {
                    // 파일이 너무 크면 마지막 부분만 읽기
                    val maxSize = 50 * 1024 // 50KB
                    val fileSize = logFile!!.length()
                    
                    if (fileSize > maxSize) {
                        val raf = RandomAccessFile(logFile, "r")
                        val skipBytes = fileSize - maxSize
                        raf.seek(skipBytes)
                        val buffer = ByteArray(maxSize)
                        val bytesRead = raf.read(buffer)
                        raf.close()
                        
                        if (bytesRead > 0) {
                            String(buffer, 0, bytesRead)
                        } else {
                            "로그가 비어있습니다."
                        }
                    } else {
                        logFile!!.readText()
                    }
                } else {
                    "로그 파일이 없습니다."
                }
                
                withContext(Dispatchers.Main) {
                    textViewLog.text = log
                    // 스크롤을 아래로
                    textViewLog.post {
                        val scrollAmount = textViewLog.layout.getLineTop(textViewLog.lineCount) - textViewLog.height
                        if (scrollAmount > 0) {
                            textViewLog.scrollTo(0, scrollAmount)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textViewLog.text = "로그 로드 실패: ${e.message}"
                }
            }
        }
    }
    
    /**
     * 로그를 지웁니다.
     */
    private fun clearLog() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (logFile?.exists() == true) {
                    // 로그 파일 백업
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val backupFile = File(getExternalFilesDir("logs"), "bot_$timestamp.log")
                    logFile!!.copyTo(backupFile, overwrite = true)
                    
                    // 로그 파일 초기화
                    logFile!!.writeText("")
                }
                
                withContext(Dispatchers.Main) {
                    textViewLog.text = "로그가 초기화되었습니다."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textViewLog.text = "로그 초기화 실패: ${e.message}"
                }
            }
        }
    }
    
    /**
     * 자동 갱신을 시작합니다.
     */
    private fun startAutoRefresh() {
        lifecycleScope.launch {
            while (isAutoRefresh) {
                loadLog()
                delay(3000) // 3초마다 갱신
            }
        }
    }
}
