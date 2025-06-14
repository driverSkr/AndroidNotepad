package com.ethan.android.notepad.model

enum class HandlerType {
    FACE_CARTOON, Face3D, HandDrawn, PencilStyle, FaceArt, FaceSketch, // 阿里的风格处理
    FACE_REPAIR, PHOTO_COLOR, AUTO_BEAUTY,

    @Deprecated("弃用了，使用FACE_DYNAMIC_V2")
    FACE_DYNAMIC,//
    REMOVE_OBJECT, REMOVE_OBJECT_SEG,// 物体移除相关
    SD, AI_VIDEO_STORY, AI_VIDEO_RESTYLE, CreaseRepair, AIPhoto,//
    AI_VIDEO_DANCE,// 视频跳舞
    AI_VIDEO_ENHANCER,// 视频超分
    AI_ANIMALS_DANCE,// 宠物跳舞
    PARTIAL_REDRAW,// 局部重绘
    ANIME_REAL,// 动漫真实化
    AI_HUG,// 拥抱
    AI_VIDEO_EXTENSION,// 拥抱
    AI_VIDEO_RESTYLE_COMFY,//
    AI_LIVE_PHOTO,//
    AI_VIDEO_IP_DANCE,//
    AI_TAKE_NAME,//
    AI_AGE_CHANGE,//
    GENERAL_BODY_BEAUTY,//
    AI_BABY,//
    FACE_DYNAMIC_V2,// face Dance v2 版本
    AI_MUSIC,// AI music
    FACE_SWAP,//
    AI_Dress,//
    AI_VIDEO_Drag,// 视频乱入
    AI_RETOUCH,// ai Retouch
    GENERAL_BODY_BEAUTY_AI_BREAST_ENLARGEMENT,// 美体丰胸
    AI_RETOUCH_FACE_BEAUTY,// Retouch功能的美颜
    FACE_DYNAMIC_V3,// face Dance v3 索尼克 版本 ,新增这个使用这是因为要对老版本作品结果做出兼容
}

enum class AiVideoExtensionCategory(var value: String) {
    AIHUG("Hug"), LIVE_PHOTO("LivePhoto");
}

enum class AiVideoExtraFeatures(var value: String) {
    AUTO_EXTEND("Auto-Extend"),// 自动延长
    CUSTOM_EXTEND("Custom-Extend"),// 自定义延长
    REGENERATE("Regenerate"),// 重新生成
    UNICOM_LP_BODYEDIT("BodyEditor_use16PL"), // 美体和livePhoto的联通
}

fun HandlerType.isOptSub(): Boolean {
    return (this == HandlerType.AI_VIDEO_DANCE || this == HandlerType.AI_VIDEO_ENHANCER || this == HandlerType.AI_ANIMALS_DANCE || this == HandlerType.AI_VIDEO_IP_DANCE || this == HandlerType.AI_VIDEO_STORY

           )
}

//所有视频类的功能
val isVideo = mutableListOf(
    HandlerType.AI_VIDEO_STORY.ordinal, HandlerType.AI_VIDEO_RESTYLE.ordinal, HandlerType.AI_VIDEO_DANCE.ordinal,
    HandlerType.AI_ANIMALS_DANCE.ordinal, HandlerType.AI_HUG.ordinal, HandlerType.AI_VIDEO_EXTENSION.ordinal,
    HandlerType.AI_VIDEO_RESTYLE_COMFY.ordinal, HandlerType.AI_LIVE_PHOTO.ordinal, HandlerType.AI_VIDEO_IP_DANCE.ordinal,
    HandlerType.AI_AGE_CHANGE.ordinal, HandlerType.AI_BABY.ordinal, HandlerType.FACE_DYNAMIC_V2.ordinal,
    HandlerType.FACE_DYNAMIC_V3.ordinal, HandlerType.AI_MUSIC.ordinal, HandlerType.AI_VIDEO_Drag.ordinal)

//ai video tab下的
val isAiVideo = mutableListOf(
    HandlerType.AI_VIDEO_STORY.ordinal, HandlerType.AI_VIDEO_RESTYLE.ordinal, HandlerType.AI_VIDEO_DANCE.ordinal,
    HandlerType.AI_ANIMALS_DANCE.ordinal, HandlerType.AI_HUG.ordinal, HandlerType.AI_VIDEO_EXTENSION.ordinal,
    HandlerType.AI_VIDEO_RESTYLE_COMFY.ordinal, HandlerType.AI_LIVE_PHOTO.ordinal, HandlerType.AI_VIDEO_IP_DANCE.ordinal,
    HandlerType.AI_VIDEO_Drag.ordinal)


// loading页是否是视频样式
fun isVideoLoadingView(handlerType: Int): Boolean {
    return isVideo.contains(handlerType)
}


