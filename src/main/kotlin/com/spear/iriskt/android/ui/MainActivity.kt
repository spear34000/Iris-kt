package com.spear.iriskt.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spear.iriskt.R
import com.spear.iriskt.android.AndroidBotManager
import com.spear.iriskt.android.ui.adapter.ControllerAdapter
import kotlinx.coroutines.launch

/**
 * Iris-kt 안드로이드 앱의 메인 액티비티
 */
class MainActivity : AppCompatActivity() {
    private lateinit var botManager: AndroidBotManager
    private lateinit var controllerAdapter: ControllerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAddController: Button
    private lateinit var buttonToggle: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        botManager = AndroidBotManager.getInstance(this)
        
        // 리사?클?뷰 초기??        recyclerView = findViewById(R.id.recyclerViewControllers)
        controllerAdapter = ControllerAdapter(botManager.getLoadedControllers()) { controller ->
            // 컨트롤러 ?릭 ???집 ?면?로 ?동
            val intent = Intent(this, EditControllerActivity::class.java)
            intent.putExtra("controller_name", controller)
            startActivity(intent)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = controllerAdapter
        }
        
        // 컨트롤러 추? 버튼
        buttonAddController = findViewById(R.id.buttonAddController)
        buttonAddController.setOnClickListener {
            val intent = Intent(this, EditControllerActivity::class.java)
            startActivity(intent)
        }
        
        // ??작/중? 버튼
        buttonToggle = findViewById(R.id.buttonToggle)
        updateStartStopButton()
        buttonToggle.setOnClickListener {
            if (botManager.isBotServiceRunning()) {
                botManager.stopBotService()
            } else {
                if (botManager.getLoadedControllers().isEmpty()) {
                    Toast.makeText(this, "컨트롤러를 먼저 추가해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    botManager.startBotService()
                }
            }
            updateStartStopButton()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateControllerList()
        updateStartStopButton()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logs -> {
                val intent = Intent(this, LogViewerActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * 컨트롤러 목록을 업데이트합니다.
     */
    private fun updateControllerList() {
        lifecycleScope.launch {
            val controllers = botManager.getLoadedControllers()
            controllerAdapter.updateControllers(controllers)
        }
    }
    
    /**
     * 시작/중지 버튼 상태를 업데이트합니다.
     */
    private fun updateStartStopButton() {
        if (botManager.isBotServiceRunning()) {
            buttonToggle.text = "중지"
        } else {
            buttonToggle.text = "시작"
        }
    }
}
