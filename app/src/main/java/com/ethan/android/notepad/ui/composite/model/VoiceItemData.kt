package com.ethan.android.notepad.ui.composite.model

data class VoiceItemDetail(
    val name: String,
    val gender: String,
    val age: String,
    val preViewUrl: String
)

data class VoiceCategoryData(
    val title: String,
    val detail: List<VoiceItemDetail>
)

data class VoiceItemList(
    val category: List<VoiceCategoryData>
)

val voiceDataSource = VoiceItemList(
    category = listOf(
        VoiceCategoryData(
            title = "所有",
            detail = listOf(
                VoiceItemDetail(name = "Alex - Young American Male", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/yl2ZDV1MzN4HbQJbMihG/TxJIOexqYqCv1Dzexs6Y.mp3"),
                VoiceItemDetail(name = "Brittney - Social Media Voice - Fun, Youthful & Informative", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/kPzsL2i3teMYv0FxEYQ6/4sLh92VdgT3Hppimhb4W.mp3"),
                VoiceItemDetail(name = "Dakota H", gender = "female", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/6l8UG4tggVaDsLClGvbvAcDf7ap1/voices/P7x743VjyZEOihNNygQ9/LZlYGXR9ACWHJ9pSXs0a.mp3"),
                VoiceItemDetail(name = "Russell - Dramatic British TV", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NYC9WEgkq1u4jiqBseQ9/cK07157YMomRml8se448.mp3"),
                VoiceItemDetail(name = "Amrut Deshmukh - Booklet Guy", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/vO7hjeAjmsdlGgUdvPpe/OjCTOjpdxyFPInvFYrxB.mp3"),
                VoiceItemDetail(name = "Adam Stone - late night radio", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NFG5qt843uXKj4pFvR7C/BgPFcmyMBm88O9O05Myn.mp3"),
                VoiceItemDetail(name = "Dan Dan", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/oDQDaQK0SGT9AtdG8VDe17L4KjX2/voices/9F4C8ztpNUmXkdDDbz3J/4d3e2bc3-b9f8-4bf0-84b2-22e0c15102f6.mp3"),
                VoiceItemDetail(name = "Finn", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/1da06ea679a54975ad96a2221fe6530d/voices/vBKc2FfBKJfcZNyEt1n6/o4o4mqfdaIsxzmOipiS9.mp3"),
                VoiceItemDetail(name = "Mark - Natural Conversations", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/y7Wmp2r8nCeGkLiXiwMOVWktro13/voices/UgBBYS2sOqTuMpoF3BR0/0Oc7jiXwWN9kRTXfQsmw.mp3"),
                VoiceItemDetail(name = "Nassim - Corporate Narration", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/NZbzesqA6CUDRU2CXJenhD5MeIu1/voices/repzAAjoKlgcT2oOAIWt/ZDAfM8XUJ6GRuoiuV1PQ.mp3"),
                VoiceItemDetail(name = "Hope - upbeat and clear", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/tnSpp4vdxKPjI9w0GnoV/LiIyxRT1qFJ1QJPr8sWl.mp3"),
                VoiceItemDetail(name = "Peter Hartlapp - Voiceactor (Werbesprecher und Moderator)", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/vvuCxlCys4W2EZtcqEctILTsG0X2/voices/oWJ0GSUjVyxG4cvdzY5t/FSGLd29ETdMMS0vPrxtk.mp3"),
                VoiceItemDetail(name = "Theo - Smart, warm, open", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NyxenPOqNyllHIzSoPbJ/JElmBasKEekIZFFmRpnJ.mp3"),
                VoiceItemDetail(name = "Sully", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/Bo6DtogKR9MFeVzUzR8msFIzsz92/voices/wAGzRVkxKEs8La0lmdrE/694524f9-fac6-4c16-a9de-8bb69e178312.mp3"),
                VoiceItemDetail(name = "Anjali - Soothing Hindi Voice", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/514d94e9241c48e8b7905375729c436f/voices/gHu9GtaHOXcSqFTK06ux/pI2RQaKcueXaen7zO23a.mp3")
            )
        ),
        VoiceCategoryData(
            title = "我的声音",
            detail = listOf(
                VoiceItemDetail(name = "Brittney - Social Media Voice - Fun, Youthful & Informative", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/kPzsL2i3teMYv0FxEYQ6/4sLh92VdgT3Hppimhb4W.mp3"),
                VoiceItemDetail(name = "Russell - Dramatic British TV", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NYC9WEgkq1u4jiqBseQ9/cK07157YMomRml8se448.mp3"),
                VoiceItemDetail(name = "Amrut Deshmukh - Booklet Guy", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/vO7hjeAjmsdlGgUdvPpe/OjCTOjpdxyFPInvFYrxB.mp3"),
                VoiceItemDetail(name = "Adam Stone - late night radio", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NFG5qt843uXKj4pFvR7C/BgPFcmyMBm88O9O05Myn.mp3"),
                VoiceItemDetail(name = "Dan Dan", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/oDQDaQK0SGT9AtdG8VDe17L4KjX2/voices/9F4C8ztpNUmXkdDDbz3J/4d3e2bc3-b9f8-4bf0-84b2-22e0c15102f6.mp3"),
                VoiceItemDetail(name = "Mark - Natural Conversations", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/y7Wmp2r8nCeGkLiXiwMOVWktro13/voices/UgBBYS2sOqTuMpoF3BR0/0Oc7jiXwWN9kRTXfQsmw.mp3"),
                VoiceItemDetail(name = "Nassim - Corporate Narration", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/NZbzesqA6CUDRU2CXJenhD5MeIu1/voices/repzAAjoKlgcT2oOAIWt/ZDAfM8XUJ6GRuoiuV1PQ.mp3"),
                VoiceItemDetail(name = "Hope - upbeat and clear", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/tnSpp4vdxKPjI9w0GnoV/LiIyxRT1qFJ1QJPr8sWl.mp3"),
                VoiceItemDetail(name = "Theo - Smart, warm, open", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NyxenPOqNyllHIzSoPbJ/JElmBasKEekIZFFmRpnJ.mp3"),
                VoiceItemDetail(name = "Anjali - Soothing Hindi Voice", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/514d94e9241c48e8b7905375729c436f/voices/gHu9GtaHOXcSqFTK06ux/pI2RQaKcueXaen7zO23a.mp3")
            )
        ),
        VoiceCategoryData(
            title = "男性",
            detail = listOf(
                VoiceItemDetail(name = "Russell - Dramatic British TV", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NYC9WEgkq1u4jiqBseQ9/cK07157YMomRml8se448.mp3"),
                VoiceItemDetail(name = "Amrut Deshmukh - Booklet Guy", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/vO7hjeAjmsdlGgUdvPpe/OjCTOjpdxyFPInvFYrxB.mp3"),
                VoiceItemDetail(name = "Adam Stone - late night radio", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NFG5qt843uXKj4pFvR7C/BgPFcmyMBm88O9O05Myn.mp3"),
                VoiceItemDetail(name = "Dan Dan", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/oDQDaQK0SGT9AtdG8VDe17L4KjX2/voices/9F4C8ztpNUmXkdDDbz3J/4d3e2bc3-b9f8-4bf0-84b2-22e0c15102f6.mp3"),
                VoiceItemDetail(name = "Mark - Natural Conversations", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/y7Wmp2r8nCeGkLiXiwMOVWktro13/voices/UgBBYS2sOqTuMpoF3BR0/0Oc7jiXwWN9kRTXfQsmw.mp3"),
                VoiceItemDetail(name = "Nassim - Corporate Narration", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/NZbzesqA6CUDRU2CXJenhD5MeIu1/voices/repzAAjoKlgcT2oOAIWt/ZDAfM8XUJ6GRuoiuV1PQ.mp3"),
                VoiceItemDetail(name = "Theo - Smart, warm, open", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/NyxenPOqNyllHIzSoPbJ/JElmBasKEekIZFFmRpnJ.mp3"),
                VoiceItemDetail(name = "Alex - Young American Male", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/yl2ZDV1MzN4HbQJbMihG/TxJIOexqYqCv1Dzexs6Y.mp3"),
                VoiceItemDetail(name = "Frederick Surrey", gender = "male", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/j9jfwdrw7BRfcR43Qohk/Vyj86dr4NJ1Tr82nEPdw.mp3"),
                VoiceItemDetail(name = "Miguel", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/vAxdfYVShGAQEwKYqDZR/YwcWTSQET4NMsgbJHwe0.mp3")
            )
        ),
        VoiceCategoryData(
            title = "女性",
            detail = listOf(
                VoiceItemDetail(name = "Brittney - Social Media Voice - Fun, Youthful & Informative", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/kPzsL2i3teMYv0FxEYQ6/4sLh92VdgT3Hppimhb4W.mp3"),
                VoiceItemDetail(name = "Hope - upbeat and clear", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/tnSpp4vdxKPjI9w0GnoV/LiIyxRT1qFJ1QJPr8sWl.mp3"),
                VoiceItemDetail(name = "Anjali - Soothing Hindi Voice", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/514d94e9241c48e8b7905375729c436f/voices/gHu9GtaHOXcSqFTK06ux/pI2RQaKcueXaen7zO23a.mp3"),
                VoiceItemDetail(name = "Hope - natural conversations", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/sD92HnMHS9WZLXKNTKxmnC8XmJ32/voices/OYTbf65OHHFELVut7v2H/kTLS0DfvlR1QTyjUzOiT.mp3"),
                VoiceItemDetail(name = "Allison - inviting and velvety British accent", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/Se2Vw1WbHmGbBbyWTuu4/3criMPMqBy1hVGIVhW2Q.mp3"),
                VoiceItemDetail(name = "Amelia", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/ZF6FPAbjXT4488VcRRnw/PXswrdJcgsGr8VdoVA43.mp3"),
                VoiceItemDetail(name = "Andrea", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/AUiOudcyrBgldqnAJhuGI9X0Ktn2/voices/qHkrJuifPpn95wK3rm2A/C0HIbK1Fqc9zwzlyvuTI.mp3"),
                VoiceItemDetail(name = "Keren - Young Brazilian Female", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/33B4UnXyTNbgLmdEDh5P/ArnzDsFaz6KDoDcDD8V2.mp3"),
                VoiceItemDetail(name = "Dakota H", gender = "female", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/6l8UG4tggVaDsLClGvbvAcDf7ap1/voices/P7x743VjyZEOihNNygQ9/LZlYGXR9ACWHJ9pSXs0a.mp3"),
                VoiceItemDetail(name = "Valeria", gender = "female", age = "middle_aged", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/9oPKasc15pfAbMr7N6Gs/in9QWOsJ1UDpVIraOQ4Z.mp3")
            )
        ),
        VoiceCategoryData(
            title = "青年",
            detail = listOf(
                VoiceItemDetail(name = "Brittney - Social Media Voice - Fun, Youthful & Informative", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/kPzsL2i3teMYv0FxEYQ6/4sLh92VdgT3Hppimhb4W.mp3"),
                VoiceItemDetail(name = "Dan Dan", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/oDQDaQK0SGT9AtdG8VDe17L4KjX2/voices/9F4C8ztpNUmXkdDDbz3J/4d3e2bc3-b9f8-4bf0-84b2-22e0c15102f6.mp3"),
                VoiceItemDetail(name = "Mark - Natural Conversations", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/y7Wmp2r8nCeGkLiXiwMOVWktro13/voices/UgBBYS2sOqTuMpoF3BR0/0Oc7jiXwWN9kRTXfQsmw.mp3"),
                VoiceItemDetail(name = "Hope - upbeat and clear", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/tnSpp4vdxKPjI9w0GnoV/LiIyxRT1qFJ1QJPr8sWl.mp3"),
                VoiceItemDetail(name = "Anjali - Soothing Hindi Voice", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/514d94e9241c48e8b7905375729c436f/voices/gHu9GtaHOXcSqFTK06ux/pI2RQaKcueXaen7zO23a.mp3"),
                VoiceItemDetail(name = "Hope - natural conversations", gender = "female", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/sD92HnMHS9WZLXKNTKxmnC8XmJ32/voices/OYTbf65OHHFELVut7v2H/kTLS0DfvlR1QTyjUzOiT.mp3"),
                VoiceItemDetail(name = "Alex - Young American Male", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/yl2ZDV1MzN4HbQJbMihG/TxJIOexqYqCv1Dzexs6Y.mp3"),
                VoiceItemDetail(name = "Miguel", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/vAxdfYVShGAQEwKYqDZR/YwcWTSQET4NMsgbJHwe0.mp3"),
                VoiceItemDetail(name = "Mark", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/y7Wmp2r8nCeGkLiXiwMOVWktro13/voices/WTUK291rZZ9CLPCiFTfh/bnnCGcGCSiY3m9CS9dGc.mp3"),
                VoiceItemDetail(name = "Finn", gender = "male", age = "young", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/workspace/1da06ea679a54975ad96a2221fe6530d/voices/vBKc2FfBKJfcZNyEt1n6/o4o4mqfdaIsxzmOipiS9.mp3"),

            )
        ),
        VoiceCategoryData(
            title = "老年",
            detail = listOf(
                VoiceItemDetail(name = "Grandpa Spuds Oxley", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/Bi4YhYxPTDRSUfiEpED4qyJ0biq2/voices/NOpBlnGInO9m6vDvFkFC/M4xySW4rr1SbAKKwMAtI.mp3"),
                VoiceItemDetail(name = "Julian - deep rich mature British voice", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/7p1Ofvcwsv7UBPoFNcpI/f86HRr1i6v8ZOCi6Dt4a.mp3"),
                VoiceItemDetail(name = "David - British Storyteller", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/BNgbHR0DNeZixGQVzloa/40VSLvxrLAkmQgyrEA4t.mp3"),
                VoiceItemDetail(name = "Benjamin - Criovozia", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/XvV3q41X6CYxk1srcwdBrHoEp682/voices/80lPKtzJMPh1vjYMUgwe/OdLhfMeDosUS2KjiTnPS.mp3"),
                VoiceItemDetail(name = "Oliver Haddington", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/T7bhqxLyENgRaSgfnqhPa6vPSID2/voices/L1aJrPa7pLJEyYlh3Ilq/AwAwPn2G1wujoh4o4RGM.mp3"),
                VoiceItemDetail(name = "Wyatt- Wise Rustic Cowboy", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/YXpFCvM1S3JbWEJhoskW/CLHVUhxcz93hum03aUwc.mp3"),
                VoiceItemDetail(name = "Sully", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/Bo6DtogKR9MFeVzUzR8msFIzsz92/voices/wAGzRVkxKEs8La0lmdrE/694524f9-fac6-4c16-a9de-8bb69e178312.mp3"),
                VoiceItemDetail(name = "Peter Hartlapp - Voiceactor (Werbesprecher und Moderator)", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/database/user/vvuCxlCys4W2EZtcqEctILTsG0X2/voices/oWJ0GSUjVyxG4cvdzY5t/FSGLd29ETdMMS0vPrxtk.mp3"),
                VoiceItemDetail(name = "Raed", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/2IWzWT3G3sgilYJywwA1YhbUxU73/voices/IK7YYZcSpmlkjKrQxbSn/23430e54-d41e-4b40-b321-9e5a9f3018b4.mp3"),
                VoiceItemDetail(name = "Richard", gender = "male", age = "old", preViewUrl = "https://storage.googleapis.com/eleven-public-prod/custom/voices/MltcMkX8tlDeUdYq1uCd/MBFbENdg1XcIYFuVowfc.mp3"),
            )
        )
    )
)
