package com.spear.iriskt.annotations

/**
 * 모든 메시지??반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnMessage

/**
 * ?�반 ?�스??메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnNormalMessage

/**
 * ?�진 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPhotoMessage

/**
 * ?��?지 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnImageMessage

/**
 * 비디??메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnVideoMessage

/**
 * ?�디??메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnAudioMessage

/**
 * ?�일 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnFileMessage

/**
 * 지??메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnMapMessage

/**
 * ?�모?�콘 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnEmoticonMessage

/**
 * ?�로??메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnProfileMessage

/**
 * ?�중 ?�진 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnMultiPhotoMessage

/**
 * ?�로???�중 ?�진 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnNewMultiPhotoMessage

/**
 * ?�장 메시지?�만 반응?�는 ?�노?�이?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnReplyMessage
