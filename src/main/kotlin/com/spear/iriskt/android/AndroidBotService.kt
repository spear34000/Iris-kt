package com.spear.iriskt.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.spear.iriskt.core.IrisBot
import com.spear.iriskt.core.LoggerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AndroidBotService : Service() {
    private val logger = LoggerManager.defaultLogger
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var bot: IrisBot
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "IrisKtBotService"
        private const val CHANNEL_NAME = "Iris-kt Bot Service"
        
        fun createStartIntent(context: Context, controllerClasses: List<String>): Intent {
            return Intent(context, AndroidBotService::class.java).apply {
                putStringArrayListExtra("controllerClasses", ArrayList(controllerClasses))
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        logger.info("AndroidBotService created")
        
        startForeground(NOTIFICATION_ID, createNotification("Iris-kt Bot Service running"))
        
        acquireWakeLock()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.info("AndroidBotService started")
        
        val controllerClassNames = intent?.getStringArrayListExtra("controllerClasses") ?: arrayListOf()
        
        serviceScope.launch {
            try {
                initializeBot(controllerClassNames)
            } catch (e: Exception) {
                logger.error("Bot initialization failed", e)
                stopSelf()
            }
        }
        
        return START_REDELIVER_INTENT
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        logger.info("AndroidBotService destroyed")
        
        if (::bot.isInitialized) {
            bot.shutdown()
        }
        
        releaseWakeLock()
        
        serviceScope.cancel()
    }
    
    private suspend fun initializeBot(controllerClassNames: List<String>) {
        val controllers = loadControllers(controllerClassNames)
        
        bot = IrisBot()
        bot.initialize(controllers)
        
        logger.info("Bot initialized successfully. ${controllers.size} controllers loaded.")
        updateNotification("Iris-kt Bot running (${controllers.size} controllers active)")
    }
    
    private fun loadControllers(controllerClassNames: List<String>): List<Any> {
        return controllerClassNames.mapNotNull { className ->
            try {
                val clazz = Class.forName(className)
                clazz.newInstance()
            } catch (e: Exception) {
                logger.error("Failed to load controller: $className", e)
                null
            }
        }
    }
    
    private fun createNotification(text: String): Notification {
        createNotificationChannel()
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Iris-kt")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification channel for Iris-kt Bot Service"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "IrisKt::BotServiceWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // Acquire WakeLock for 10 minutes (needs renewal)
        }
    }
    
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }
}
