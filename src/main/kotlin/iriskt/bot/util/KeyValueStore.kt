package iriskt.bot.util

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * 간단한 키-값 저장소 (메모리 기반)
 * 실제 구현에서는 SQLite를 사용해야 합니다.
 */
object KeyValueStore {
    @PublishedApi
    internal val storage = mutableMapOf<String, Any>()
    @PublishedApi
    internal val jsonStorage = mutableMapOf<String, JsonElement>()

    /**
     * 값을 저장합니다.
     */
    fun put(key: String, value: Any) {
        storage[key] = value
    }

    /**
     * 값을 조회합니다.
     */
    fun get(key: String): Any? {
        return storage[key]
    }

    /**
     * 키-값 쌍을 삭제합니다.
     */
    fun delete(key: String): Boolean {
        return storage.remove(key) != null
    }

    /**
     * 모든 키의 목록을 반환합니다.
     */
    fun listKeys(): List<String> {
        return storage.keys.toList()
    }

    /**
     * 저장소의 크기를 반환합니다.
     */
    fun size(): Int {
        return storage.size
    }

    /**
     * 저장소를 초기화합니다.
     */
    fun clear() {
        storage.clear()
        jsonStorage.clear()
    }

    /**
     * 문자열 검색 (값에서 문자열을 검색)
     */
    fun search(searchString: String): List<String> {
        return storage.entries.filter { (_, value) ->
            value.toString().contains(searchString, ignoreCase = true)
        }.map { it.key }
    }

    /**
     * JSON 객체의 특정 키에서 문자열 검색
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
     * 키에서 문자열 검색
     */
    fun searchKey(searchString: String): List<String> {
        return storage.keys.filter { it.contains(searchString, ignoreCase = true) }
    }

    /**
     * 타입 안전한 값 조회
     */
    inline fun <reified T> getTyped(key: String): T? {
        return storage[key] as? T
    }

    /**
     * 문자열 값 조회
     */
    fun getString(key: String): String? {
        return getTyped<String>(key)
    }

    /**
     * 정수 값 조회
     */
    fun getInt(key: String): Int? {
        return getTyped<Int>(key)
    }

    /**
     * 불리언 값 조회
     */
    fun getBoolean(key: String): Boolean? {
        return getTyped<Boolean>(key)
    }

    /**
     * JSON 값 저장
     */
    fun putJson(key: String, jsonElement: JsonElement) {
        jsonStorage[key] = jsonElement
    }

    /**
     * JSON 값 조회
     */
    fun getJson(key: String): JsonElement? {
        return jsonStorage[key]
    }
}

/**
 * 키-값 저장소 관리자 (싱글톤)
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
