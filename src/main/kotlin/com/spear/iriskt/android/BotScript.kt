package com.spear.iriskt.android

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.api.defaultImports

/**
 * Iris-kt 스크립트 정의
 */
@KotlinScript(
    fileExtension = "bot.kts",
    compilationConfiguration = BotScriptConfiguration::class
)
abstract class BotScript

/**
 * 스크립트 컴파일 설정
 */
class BotScriptConfiguration : ScriptCompilationConfiguration({
    // 현재 클래스로더의 모든 의존성 포함
    jvm {
        dependenciesFromClassloader(classLoader = BotScript::class.java.classLoader, wholeClasspath = true)
    }
    
    // 기본 임포트 설정
    defaultImports(
        "iriskt.bot.core.*",
        "iriskt.bot.annotations.*",
        "iriskt.bot.models.*",
        "kotlinx.coroutines.*"
    )
})
