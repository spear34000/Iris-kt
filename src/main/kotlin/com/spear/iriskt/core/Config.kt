package com.spear.iriskt.core

object Config {
    private val configMap = mutableMapOf<String, String>()

    init {
        // ?�경 변??로드
        loadFromEnvironment()
    }

    private fun loadFromEnvironment() {
        System.getenv().forEach { (key, value) ->
            configMap[key] = value
        }
    }

    fun get(key: String, defaultValue: String? = null): String? {
        return configMap[key] ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return configMap[key]?.toIntOrNull() ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return configMap[key]?.toBoolean() ?: defaultValue
    }

    fun set(key: String, value: String) {
        configMap[key] = value
    }

    fun has(key: String): Boolean {
        return configMap.containsKey(key)
    }

    fun getAll(): Map<String, String> {
        return configMap.toMap()
    }
}
