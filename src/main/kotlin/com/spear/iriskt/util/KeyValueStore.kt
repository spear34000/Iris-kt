package com.spear.iriskt.util

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * ê°„ë‹¨????ê°??€?¥ì†Œ (ë©”ëª¨ë¦?ê¸°ë°˜)
 * ?¤ì œ êµ¬í˜„?ì„œ??SQLiteë¥??¬ìš©?´ì•¼ ?©ë‹ˆ??
 */
object KeyValueStore {
    @PublishedApi
    internal val storage = mutableMapOf<String, Any>()
    @PublishedApi
    internal val jsonStorage = mutableMapOf<String, JsonElement>()

    /**
     * ê°’ì„ ?€?¥í•©?ˆë‹¤.
     */
    fun put(key: String, value: Any) {
        storage[key] = value
    }

    /**
     * ê°’ì„ ì¡°íšŒ?©ë‹ˆ??
     */
    fun get(key: String): Any? {
        return storage[key]
    }

    /**
     * ??ê°??ì„ ?? œ?©ë‹ˆ??
     */
    fun delete(key: String): Boolean {
        return storage.remove(key) != null
    }

    /**
     * ëª¨ë“  ?¤ì˜ ëª©ë¡??ë°˜í™˜?©ë‹ˆ??
     */
    fun listKeys(): List<String> {
        return storage.keys.toList()
    }

    /**
     * ?€?¥ì†Œ???¬ê¸°ë¥?ë°˜í™˜?©ë‹ˆ??
     */
    fun size(): Int {
        return storage.size
    }

    /**
     * ?€?¥ì†Œë¥?ì´ˆê¸°?”í•©?ˆë‹¤.
     */
    fun clear() {
        storage.clear()
        jsonStorage.clear()
    }

    /**
     * ë¬¸ì??ê²€??(ê°’ì—??ë¬¸ì?´ì„ ê²€??
     */
    fun search(searchString: String): List<String> {
        return storage.entries.filter { (_, value) ->
            value.toString().contains(searchString, ignoreCase = true)
        }.map { it.key }
    }

    /**
     * JSON ê°ì²´???¹ì • ?¤ì—??ë¬¸ì??ê²€??
     */
    fun searchJson(valueKey: String, searchString: String): List<String> {
        return jsonStorage.entries.filter { (_, jsonElement) ->
            if (jsonElement is JsonObject) {
                jsonElement[valueKey]?.let { value ->
                    when (value) {
                        is JsonPrimitive -> value.content.contains(searchString, ignoreCase = true)
                        else -> false
                    }
                } ?: false
            } else {
                false
            }
        }.map { it.key }
    }

    /**
     * ?¤ì—??ë¬¸ì??ê²€??
     */
    fun searchKey(searchString: String): List<String> {
        return storage.keys.filter { it.contains(searchString, ignoreCase = true) }
    }

    /**
     * ?€???ˆì „??ê°?ì¡°íšŒ
     */
    inline fun <reified T> getTyped(key: String): T? {
        return storage[key] as? T
    }

    /**
     * ë¬¸ì??ê°?ì¡°íšŒ
     */
    fun getString(key: String): String? {
        return getTyped<String>(key)
    }

    /**
     * ?•ìˆ˜ ê°?ì¡°íšŒ
     */
    fun getInt(key: String): Int? {
        return getTyped<Int>(key)
    }

    /**
     * ë¶ˆë¦¬??ê°?ì¡°íšŒ
     */
    fun getBoolean(key: String): Boolean? {
        return getTyped<Boolean>(key)
    }

    /**
     * JSON ê°??€??
     */
    fun putJson(key: String, jsonElement: JsonElement) {
        jsonStorage[key] = jsonElement
    }

    /**
     * JSON ê°?ì¡°íšŒ
     */
    fun getJson(key: String): JsonElement? {
        return jsonStorage[key]
    }
}

/**
 * ??ê°??€?¥ì†Œ ê´€ë¦¬ì (?±ê???
 */
class KVStoreManager private constructor() {
    companion object {
        @Volatile
        private var instance: KVStoreManager? = null

        fun getInstance(): KVStoreManager {
            return instance ?: synchronized(this) {
                instance ?: KVStoreManager().also { instance = it }
            }
        }
    }

    @PublishedApi
    internal val store = KeyValueStore

    fun put(key: String, value: Any) = store.put(key, value)
    fun get(key: String) = store.get(key)
    fun delete(key: String) = store.delete(key)
    fun listKeys() = store.listKeys()
    fun size() = store.size()
    fun clear() = store.clear()
    fun search(searchString: String) = store.search(searchString)
    fun searchJson(valueKey: String, searchString: String) = store.searchJson(valueKey, searchString)
    fun searchKey(searchString: String) = store.searchKey(searchString)

    inline fun <reified T> getTyped(key: String): T? = store.getTyped<T>(key)
    fun getString(key: String) = store.getString(key)
    fun getInt(key: String) = store.getInt(key)
    fun getBoolean(key: String) = store.getBoolean(key)
    fun putJson(key: String, jsonElement: JsonElement) = store.putJson(key, jsonElement)
    fun getJson(key: String) = store.getJson(key)
}
