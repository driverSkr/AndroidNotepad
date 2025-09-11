package com.ethan.android.notepad.repository.data

import com.ethan.android.notepad.common.model.MediaType

data class BlurLoadItemData(
    val mask: String,
    val url: String,
    val mediaType: MediaType
)


val blurDataSource = listOf(
    BlurLoadItemData("U9BqYW-U2v4:rCR6JUIVE1V@#,%M9GIVwv-o", "https://material.hitpaw.com/static/3478024bd15bfcce04b7a299f9977b15/upload/fcf2381445742d67ea2eab6adf2644c53DFigurineFactory.webp", MediaType.Webp),
    BlurLoadItemData("UQHnpZIU0Jtj~qoID%jGAHoxwca1?HMxRPo}", "https://material.hitpaw.com/static/45e689b2df56f1f366b6c2c6fc9ebd6e/upload/7156db22ea5fc08034a08a642decb141KissKiss.webp", MediaType.Webp),
    BlurLoadItemData("UHHBl34:_1s:?vIV0KbI~pM|9Fj[_3axn#Rj", "https://material.hitpaw.com/static/429d39d886e0d872166c0a3e46316ca3/upload/f9a94753687d9f4c9131e147601f3e44MemeSmashx3.webp", MediaType.Webp),
    BlurLoadItemData("UA96zN9u0#-UEms.xZNa0~xG=wNbxts.Ips.", "https://material.hitpaw.com/static/670b06b89e3c5f68d7b11c18f77ee7bb/upload/7b070f9c17b5d7eacfdab15a09224f3eFin-tasticMermaid.webp", MediaType.Webp),
    BlurLoadItemData("UEDl_|570..84-xvxtR*O[IA^iM_^%IVS5xu", "https://material.hitpaw.com/static/65a8096b8165165bb325d70deb5896a3/upload/148edd0d4912b1d1bbbb35dfee25f2acHiFiveEmojiTwin.webp", MediaType.Webp),
    BlurLoadItemData("UKF\$6B9b5Tr?ENDibvog.Toz?GkXyE9FV[sR", "https://material.hitpaw.com/static/236fe0cb8c3afe506448d7b5d05afe6b/upload/050f88a8e143a4e82e9af932da7dcf35LiquidMetal.webp", MediaType.Webp),
    BlurLoadItemData("UIHCcgyEE2WB?HDi-;%MV?M{-;IU?wR-snoJ", "https://material.hitpaw.com/static/e084d9a93475db4cbe0617c839e5a362/upload/2f888f94e88d8e473ff40582d817a08dRuinYourVow.webp", MediaType.Webp),
    BlurLoadItemData("UOA-VjGYivxu.ANFe9s:I8VsnOSd54\$RogNa", "https://material.hitpaw.com/static/1d041ad8cf6f1ce87ef6e9e6430016fd/upload/f3307948896445e6889d4af7f07657e6AnythingRobot.webp", MediaType.Webp),
    BlurLoadItemData("UIJtC*?wX.?cIA4:E0M{9aIAjD9Z%fM|Z~wb", "https://material.hitpaw.com/static/b106a970e62e40bd7db6faaeb0bd377e/upload/7872e7c3b02133df9fad6a6aa07396e1HolyWings.webp", MediaType.Webp),
)