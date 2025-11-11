package com.spear.iriskt.annotations

/**
 * 모든 메시지에 반응하는 어노테이션
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnMessage

/**
 * 일반 텍스트 메시지에만 반응하는 어노테이션 (type=1, 첨부 없음)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnTextMessage instead", ReplaceWith("OnTextMessage"))
annotation class OnNormalMessage

/**
 * 일반 텍스트 메시지 (type=1, 첨부 없음)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnTextMessage

/**
 * 링크 메시지 (type=1, 첨부 있음)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnLinkMessage

/**
 * 사진 메시지에만 반응하는 어노테이션 (type=2)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnPhotoMessage

/**
 * 이미지 메시지에만 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnPhotoMessage instead", ReplaceWith("OnPhotoMessage"))
annotation class OnImageMessage

/**
 * 비디오 메시지에만 반응하는 어노테이션 (type=3)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnVideoMessage

/**
 * 연락처 메시지 (type=4)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnContactMessage

/**
 * 오디오 메시지에만 반응하는 어노테이션 (type=5)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnAudioMessage

/**
 * 이모티콘 메시지 (type=6)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnEmoticonMessage

/**
 * 이모티콘 썸네일 메시지 (type=12, 20)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnEmoticonThumbnailMessage

/**
 * 투표 메시지 (type=14)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnVoteMessage

/**
 * 프로필 메시지 (type=17)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnProfileMessage

/**
 * 파일 메시지에만 반응하는 어노테이션 (type=18)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnFileMessage

/**
 * 검색 메시지 (type=23)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnSearchMessage

/**
 * 공지 메시지 (type=24)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnNoticeMessage

/**
 * 답장 메시지 (type=26)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnReplyMessage

/**
 * 다중 사진 메시지에만 반응하는 어노테이션 (type=27)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnMultiPhotoMessage

/**
 * 새로운 다중 사진 메시지에만 반응하는 어노테이션 (하위 호환용)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use OnMultiPhotoMessage instead", ReplaceWith("OnMultiPhotoMessage"))
annotation class OnNewMultiPhotoMessage

/**
 * 보이스톡 메시지 (type=51)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnVoiceTalkMessage

/**
 * 투표 등록 메시지 (type=97)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnVoteRegisterMessage

/**
 * 공유 메시지 (type=98)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnShareMessage

/**
 * 지도 메시지 (더 이상 지원되지 않음)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Map messages are no longer supported")
annotation class OnMapMessage
