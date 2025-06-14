package com.ethan.android.notepad.ui.composite.model

data class RemoveItemData(
    val category: List<RemoveCategoryData>
)

data class RemoveCategoryData(
    val title: String,
    val detail: List<RemoveDetail>
)

data class RemoveDetail(
    val title: String,
    val isFree: Int,
    val previewUrl: String
)

val removeDataSource = RemoveItemData(
    category = listOf(
        RemoveCategoryData(
            title = "颜色",
            detail = listOf(
                RemoveDetail(
                    title = "Color1",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/454d0d40944c6a4838939dcf328b0f61/upload/b30a718c98b4d6fdc4fbf6769cfa581c6.png"
                ),
                RemoveDetail(
                    title = "Color2",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/337c99d07467a648fd1c4e332fa4b19e/upload/5fd851690a8e4978fe68ed11e39a10b75.png"
                ),
                RemoveDetail(
                    title = "Color3",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/ab8efee64898d744ab3d51db21cb9d21/upload/7fb03c483db4cc9def695f8d499eb4149.png"
                ),
                RemoveDetail(
                    title = "Color4",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/1bb816096c27e05395213f6437a952a1/upload/ef5085d1d63930ef40968fd2294d82d58.png"
                ),
                RemoveDetail(
                    title = "Color5",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/ad3a872a7baaf136fa0f93b846adccde/upload/a657ad5614728d8b8b972a08c67905d57.png"
                ),
                RemoveDetail(
                    title = "Color6",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/0e27b77eea8268987e56622e71846b69/upload/0d550bb455ed7f7a35f67d760bc4990d11.png"
                ),
                RemoveDetail(
                    title = "Color7",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/a9b760bce7a89f12c06181ee3f9d73bd/upload/4db49e1cec120417d92b3dc5f8f4f0a310.png"
                ),
                RemoveDetail(
                    title = "Color8",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/19e8d0cfccfedb108d5c27d01865e5d4/upload/12cfa316b60f11e2a2f9809c3b5ee34f4.png"
                ),
                RemoveDetail(
                    title = "Color9",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/17b5c08143c066c147c2489ecd8870a1/upload/5bfc0195414e0e3157a83c8bbed20ea53.png"
                ),
                RemoveDetail(
                    title = "Color10",
                    isFree = 1,
                    previewUrl = "https://material.hitpaw.com/static/4fe65028122d6831e3758de950eddf1b/upload/0b674700ba727bc96610d962e10db76c2.png"
                ),
            )
        ),

        RemoveCategoryData(
            title = "旅行",
            detail = listOf(
                RemoveDetail(
                    title = "Travel1",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/5deb7ac911e0946fe07442ce08467b02/upload/5e54eed933461d92b479e3a38d33d1f08.png"
                ),
                RemoveDetail(
                    title = "Travel2",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/9a2c3afecc219253c5616abc867484d8/upload/0d95edfeead1a7fd569dfc73dd603f5a11.png"
                ),
                RemoveDetail(
                    title = "Travel3",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/1f4b5bff7ba9f63eee379031ece1b308/upload/930ff6be95f4d6c6ffa3a21a8f5b8fff10.png"
                ),
                RemoveDetail(
                    title = "Travel4",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/c014552d18c446eb818cccfd95742a06/upload/285bf8b202b7d5e2ec28e2bddc5426ea9.png"
                ),
                RemoveDetail(
                    title = "Travel5",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/fc2f8b4b6bf45f9e15fb97ee56b1575f/upload/3092cc3ad560274ad3bb90e50f53a6da7.png"
                ),
                RemoveDetail(
                    title = "Travel6",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/2e7b1e3af08853701b5df4704d8e2098/upload/cd3c313a1e036cc9a5db1ea215ff33cd6.png"
                ),
                RemoveDetail(
                    title = "Travel7",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/dd2bf86b0ff0a5467b148f95e5512513/upload/f7a94809bcc0d825ae5a63b4b76cfb0f5.png"
                ),
                RemoveDetail(
                    title = "Travel8",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/c3aad5bd9d6bdd0e6bb0c8376e190848/upload/83625724bcc45f44d15a5986160da4d01.png"
                ),
            )
        ),

        RemoveCategoryData(
            title = "生活",
            detail = listOf(
                RemoveDetail(
                    title = "Life Style 1",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/7c606ea54bce9e7204941b15787a4998/upload/f7a62b2b2918bc289ee8954c6ca3549c12.png"
                ),
                RemoveDetail(
                    title = "Life Style 2",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/5330365154ff019ec06b5095ac948188/upload/66f04d2199789c0bd2f33bb85e0340d311.png"
                ),
                RemoveDetail(
                    title = "Life Style 3",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/6ace44fb956799de7a998a8694b7b34b/upload/f017670d81fe0d4ff354ab11963a6db310.png"
                ),
                RemoveDetail(
                    title = "Life Style 4",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/6838fe80dd8140c16c10037a26310636/upload/9fa745d2eb1d100824fddac9e5bc01d19.png"
                ),
                RemoveDetail(
                    title = "Life Style 5",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/243a41060e243cc31fee1126b30e631b/upload/bf2e34757a2e4d5ef189f92c45305f357.png"
                ),
                RemoveDetail(
                    title = "Life Style 6",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/036ca9b8e24ecbf26fdc6f46afdd1978/upload/7f4791bb76ec33ae0655d3b468342c3a6.png"
                ),
                RemoveDetail(
                    title = "Life Style 7",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/d58bb12a1059fb8c80d06a2426e0311f/upload/22279b5d86f2d69b0f7814a21e6000785.png"
                ),
                RemoveDetail(
                    title = "Life Style 8",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/683869a7292518accc5356a29ab5c105/upload/3932e5f4334d187b2ae467f72763577c4.png"
                ),
            )
        ),

        RemoveCategoryData(
            title = "节日",
            detail = listOf(
                RemoveDetail(
                    title = "Festival1",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/90687c340367c36e2d81871be53f67dc/upload/f97d3e341a1ed168502c4fa0c3ad83ec11.png"
                ),
                RemoveDetail(
                    title = "Festival2",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/2b3c945069f76d51bf3dff9c078fcd91/upload/b84ea9b05f6fff85583dbf2d76d3f17810.png"
                ),
                RemoveDetail(
                    title = "Festival3",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/933d17281b1321d7ec74c11a107374b3/upload/e0875fa5bc51334670306c5f88902b3c9.png"
                ),
                RemoveDetail(
                    title = "Festival4",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/1098081a08e69beb8e592f0bdf510e2c/upload/0ddc62e1cba6ad0b4cb83100f43869bc8.png"
                ),
                RemoveDetail(
                    title = "Festival5",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/96846643428ce26bab99ebbddf228198/upload/03feb7c14b3379676b5391f6b3ba3bd67.png"
                ),
                RemoveDetail(
                    title = "Festival6",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/3feb2dd995ff57de55b832307bb4aa8d/upload/78c5e21f84ad2b01002a6ae9616496cd6.png"
                ),
                RemoveDetail(
                    title = "Festival7",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/d152ac76ec0fcfb07e565380df2f67c5/upload/3458ccb64a70895ca7c1d17279924a935.png"
                ),
                RemoveDetail(
                    title = "Festival8",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/15e042df2e5ff5b660f8fd98d8526d94/upload/dc0d71a0c38b40a60d03912c96b75bcf4.png"
                ),
                RemoveDetail(
                    title = "Festival9",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/36748753080dffae4cbd5c0427442830/upload/f2ed05c74bc4cdd85b9a65450807ed893.png"
                ),
            )
        ),

        RemoveCategoryData(
            title = "季节",
            detail = listOf(
                RemoveDetail(
                    title = "Season1",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/db20770a8b02dd110bcd9460a4a316d3/upload/abbee7ce901b1a0987f9a5b69483d9d711.png"
                ),
                RemoveDetail(
                    title = "Season2",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/c4dd03063731f80de334f5ca7ea7813b/upload/cf6388cefa334ab5096afb9ed10bc8e310.png"
                ),
                RemoveDetail(
                    title = "Season3",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/36aabb64b844fc0f808b453010a30eb9/upload/742e6c3e4e20109b26af5646fd17c7f09.png"
                ),
                RemoveDetail(
                    title = "Season4",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/490cf03b56ce4c05cd046908bfc8bd9f/upload/c9e16734d914c079d6982baaa75fdb6f8.png"
                ),
                RemoveDetail(
                    title = "Season5",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/066b2b9e7ad3d1b49c85f0c8ddf354db/upload/e1525e7bafb71ca67f7f8e1d7c7449807.png"
                ),
                RemoveDetail(
                    title = "Season6",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/4cdba00f1abdd7d4864a29a238ffa209/upload/17fc8a6ecf7c56f118bede22f143adf16.png"
                ),
                RemoveDetail(
                    title = "Season7",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/6662711b0140b76f16a323b2cc22c93a/upload/388f7de8635ed1b8f1b2491e503af06d5.png"
                ),
                RemoveDetail(
                    title = "Season8",
                    isFree = 2,
                    previewUrl = "https://material.hitpaw.com/static/4f82c8d2bde83b06844a840ad694b02b/upload/8484a07e323c4156d34c7eff843321564.png"
                ),
            )
        ),
    )
)
