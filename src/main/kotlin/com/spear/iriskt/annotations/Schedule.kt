package com.spear.iriskt.annotations

/**
 * ì£¼ê¸°???¤ì?ì¤??¤í–‰???„í•œ ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Schedule(
    val interval: Long = 0 // ë°€ë¦¬ì´ˆ ?¨ìœ„
)

/**
 * ?¤ì?ì¤„ëœ ë©”ì‹œì§€ ì²˜ë¦¬ë¥??„í•œ ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScheduleMessage(
    val key: String
)

/**
 * ë´??œì‘??ë¶€?¸ìŠ¤?¸ë© ?¤í–‰???„í•œ ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bootstrap(
    val priority: Int = 0 // ??? ?«ì ?°ì„ 
)
