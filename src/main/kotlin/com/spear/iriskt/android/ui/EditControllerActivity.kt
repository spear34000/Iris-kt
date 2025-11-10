package com.spear.iriskt.android.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.spear.iriskt.R
import com.spear.iriskt.android.AndroidBotManager
import kotlinx.coroutines.launch
import java.io.File

/**
 * 컨트롤러 코드를 편집하는 액티비티
 */
class EditControllerActivity : AppCompatActivity() {
    private lateinit var botManager: AndroidBotManager
    private lateinit var editTextCode: EditText
    private var controllerName: String? = null
    private var isNewController = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_controller)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        botManager = AndroidBotManager.getInstance(this)
        editTextCode = findViewById(R.id.editTextCode)
        
        // 기존 컨트롤러 편집 여부 확인
        controllerName = intent.getStringExtra("controller_name")
        if (controllerName != null) {
            isNewController = false
            supportActionBar?.title = "컨트롤러 편집: $controllerName"
            loadControllerCode(controllerName!!)
        } else {
            supportActionBar?.title = "새 컨트롤러 생성"
            loadTemplateCode()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                saveController()
                true
            }
            R.id.action_run -> {
                runController()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * 컨트롤러 코드를 로드합니다.
     */
    private fun loadControllerCode(className: String) {
        // 실제 구현에서는 클래스 이름으로부터 파일을 찾아 사용자가 로드해야 함
        // 여기서는 간단한 예시를 제공
        val fileName = className.substringAfterLast('.') + ".bot.kts"
        val file = File(getExternalFilesDir("controllers"), fileName)
        
        if (file.exists()) {
            editTextCode.setText(file.readText())
        } else {
            Toast.makeText(this, "컨트롤러 파일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            loadTemplateCode()
        }
    }
    
    /**
     * 템플릿 코드를 로드합니다.
     */
    private fun loadTemplateCode() {
        val templateCode = """
            import iriskt.bot.annotations.*
            import iriskt.bot.models.*
            
            @Controller
            @MessageController
            class MyBotController {
                
                @OnMessage
                @Command("hello")
                fun hello(context: ChatContext) {
                    context.reply("안녕하세요! 저는 Iris-kt 봇입니다.")
                }
                
                @OnMessage
                @Command("help")
                fun help(context: ChatContext) {
                    context.reply("[Help]\n!hello - Say hello\n!help - Show help")
                }
            }
        """.trimIndent()
        
        editTextCode.setText(templateCode)
    }
    
    /**
     * 컨트롤러를 저장합니다.
     */
    private fun saveController() {
        val code = editTextCode.text.toString()
        if (code.isBlank()) {
            Toast.makeText(this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 클래스 이름 추출 (간단한 정규식 사용)
        val classNameRegex = "class\\s+([A-Za-z0-9_]+)".toRegex()
        val matchResult = classNameRegex.find(code)
        val className = matchResult?.groupValues?.getOrNull(1)
        
        if (className == null) {
            Toast.makeText(this, "클래스 이름을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 파일 저장
        val fileName = "$className.bot.kts"
        val file = File(getExternalFilesDir("controllers"), fileName)
        
        try {
            file.parentFile?.mkdirs()
            file.writeText(code)
            Toast.makeText(this, "컨트롤러가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            
            // 새 컨트롤러인 경우 이름 업데이트
            if (isNewController) {
                controllerName = "com.spear.iriskt.bot.controllers.$className"
                isNewController = false
                supportActionBar?.title = "컨트롤러 편집: $className"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 컨트롤러를 실행합니다.
     */
    private fun runController() {
        val code = editTextCode.text.toString()
        if (code.isBlank()) {
            Toast.makeText(this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 먼저 저장
        saveController()
        
        // 스크립트 실행
        lifecycleScope.launch {
            botManager.runScript(code) { success, message ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this@EditControllerActivity, "컨트롤러가 로드되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditControllerActivity, "오류: $message", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
