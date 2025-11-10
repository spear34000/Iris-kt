package com.spear.iriskt.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.spear.iriskt.core.LoggerManager
import com.spear.iriskt.core.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * 안드로이드 환경에서 Iris-kt 봇을 관리하는 매니저 클래스
 */
class AndroidBotManager private constructor(private val context: Context) {
    private val logger = LoggerManager.defaultLogger
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isServiceRunning = false
    private val loadedControllers = mutableListOf<String>()
    
    companion object {
        @Volatile
        private var instance: AndroidBotManager? = null
        
        fun getInstance(context: Context): AndroidBotManager {
            return instance ?: synchronized(this) {
                instance ?: AndroidBotManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    /**
     * 봇 서비스를 시작합니다.
     */
    fun startBotService() {
        if (isServiceRunning) {
            logger.info("봇 서비스가 이미 실행 중입니다.")
            return
        }
        
        val intent = AndroidBotService.createStartIntent(context, loadedControllers)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        
        isServiceRunning = true
        logger.info("봇 서비스가 시작되었습니다.")
    }
    
    /**
     * 봇 서비스를 중지합니다.
     */
    fun stopBotService() {
        if (!isServiceRunning) {
            logger.info("봇 서비스가 실행 중이 아닙니다.")
            return
        }
        
        context.stopService(Intent(context, AndroidBotService::class.java))
        isServiceRunning = false
        logger.info("봇 서비스가 중지되었습니다.")
    }
    
    /**
     * 봇 서비스가 실행 중인지 확인합니다.
     */
    fun isBotServiceRunning(): Boolean {
        return isServiceRunning
    }
    
    /**
     * 코드 스크립트 파일에서 컨트롤러를 로드합니다.
     */
    suspend fun loadControllerFromScript(scriptFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val scriptSource = scriptFile.toScriptSource()
                val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<BotScript>()
                val evaluationConfiguration = ScriptEvaluationConfiguration {
                    implicitReceivers(context)
                }
                
                val host = BasicJvmScriptingHost()
                val result = host.eval(scriptSource, compilationConfiguration, evaluationConfiguration)
                
                if (result.valueOrNull() != null) {
                    val className = result.valueOrNull()?.javaClass?.name
                    if (className != null) {
                        loadedControllers.add(className)
                        logger.info("컨트롤러 스크립트 로드 성공: $className")
                        true
                    } else {
                        logger.error("컨트롤러 스크립트 로드 실패: 클래스 이름을 가져올 수 없습니다.")
                        false
                    }
                } else {
                    logger.error("컨트롤러 스크립트 로드 실패: ${result.reports.joinToString { it.message }}")
                    false
                }
            } catch (e: Exception) {
                logger.error("컨트롤러 스크립트 로드 중 오류 발생", e)
                false
            }
        }
    }
    
    /**
     * 컨트롤러 클래스를 로드합니다.
     */
    fun loadControllerClass(className: String): Boolean {
        return try {
            Class.forName(className)
            loadedControllers.add(className)
            logger.info("컨트롤러 클래스 로드 성공: $className")
            true
        } catch (e: Exception) {
            logger.error("컨트롤러 클래스 로드 실패: $className", e)
            false
        }
    }
    
    /**
     * 모든 컨트롤러를 언로드합니다.
     */
    fun unloadAllControllers() {
        loadedControllers.clear()
        logger.info("모든 컨트롤러가 언로드되었습니다.")
    }
    
    /**
     * 특정 컨트롤러를 언로드합니다.
     */
    fun unloadController(className: String): Boolean {
        val removed = loadedControllers.remove(className)
        if (removed) {
            logger.info("컨트롤러 언로드 성공: $className")
        } else {
            logger.info("컨트롤러를 찾을 수 없습니다: $className")
        }
        return removed
    }
    
    /**
     * 로드된 모든 컨트롤러 목록을 반환합니다.
     */
    fun getLoadedControllers(): List<String> {
        return loadedControllers.toList()
    }
    
    /**
     * 봇 설정을 업데이트합니다.
     */
    fun updateSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val autoStartBot = prefs.getBoolean("bot_auto_start", false)
        val notificationEnabled = prefs.getBoolean("bot_show_notification", true)
        val logLevelString = prefs.getString("bot_log_level", "INFO") ?: "INFO"
        val logLevel = try {
            LogLevel.valueOf(logLevelString.uppercase())
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid log level from preferences: $logLevelString, defaulting to INFO", e)
            LogLevel.INFO
        }

        LoggerManager.setLogLevel(logLevel)
        logger.info("봇 설정이 업데이트되었습니다. 자동 시작: $autoStartBot, 알림: $notificationEnabled, 로그 레벨: $logLevel")

        if (autoStartBot && !isBotServiceRunning()) {
            startBotService()
        } else if (!autoStartBot && isBotServiceRunning()) {
            stopBotService()
        }
        // TODO: 알림 설정 (notificationEnabled) 적용 로직 추가
    }

    /**
     * 코드 스크립트를 컴파일합니다.
     */
    suspend fun compileScript(scriptContent: String, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                outputFile.writeText(scriptContent)
                logger.info("스크립트가 성공적으로 컴파일되었습니다: ${outputFile.absolutePath}")
                true
            } catch (e: Exception) {
                logger.error("스크립트 컴파일 중 오류 발생", e)
                false
            }
        }
    }
    
    /**
     * 봇 스크립트를 실행합니다.
     */
    fun runScript(scriptContent: String, onResult: (Boolean, String) -> Unit) {
        managerScope.launch {
            try {
                val tempFile = File(context.cacheDir, "temp_script_${System.currentTimeMillis()}.kts")
                val compiled = compileScript(scriptContent, tempFile)
                
                if (compiled) {
                    val loaded = loadControllerFromScript(tempFile)
                    if (loaded) {
                        onResult(true, "스크립트가 성공적으로 로드되었습니다.")
                    } else {
                        onResult(false, "스크립트 로드 실패")
                    }
                } else {
                    onResult(false, "스크립트 컴파일 실패")
                }
            } catch (e: Exception) {
                logger.error("스크립트 실행 중 오류 발생", e)
                onResult(false, "오류: ${e.message}")
            }
        }
    }
}
