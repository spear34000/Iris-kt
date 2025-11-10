package com.spear.iriskt.annotations

/**
 * ?¼ë“œ ë©”ì‹œì§€?ë§Œ ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnFeedMessage

/**
 * ?¬ìš©??ì´ˆë? ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnInviteUserFeed

/**
 * ?¬ìš©???´ì¥ ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnLeaveUserFeed

/**
 * ë©”ì‹œì§€ ?? œ ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnDeleteMessageFeed

/**
 * ë©”ì‹œì§€ ?¨ê? ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnHideMessageFeed

/**
 * ê´€ë¦¬ì ?¹ê¸‰ ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPromoteManagerFeed

/**
 * ê´€ë¦¬ì ê°•ë“± ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnDemoteManagerFeed

/**
 * ë°©ì¥ ?„ì„ ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnHandOverHostFeed

/**
 * ?¤í”ˆì±„íŒ… ?¬ìš©???…ì¥ ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnOpenChatJoinUserFeed

/**
 * ?¤í”ˆì±„íŒ… ?¬ìš©??ì¶”ë°© ?¼ë“œ??ë°˜ì‘?˜ëŠ” ?´ë…¸?Œì´?? */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnOpenChatKickedUserFeed
