package com.ethan.file

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TestUtil {
    private var DATA_PATH: String? = null
    private var LOG_PATH: String? = null


    // todo 上线前检测测试设备广告ID是否变动
    var adIdList = mutableListOf(
        "393718df-8bb0-4fb2-92df-16842e6a3919",
        "5a4c5e48-2929-4131-9c9f-0d864eae6bla",
        "fb45a1b6-a9ab-479c-accd-8a8a969a1040",
        "5d1fbec8-039c-4d3d-9b46-3c5d0f28a396",
        "13229e0b-c85a-4d21-967e-b825cdb4d7f5",
        "edcb6859-5137-46bc-9df8-a0763755ddf4",
        "c8bfb666-1afa-4d2e-9a39-51456e1ebfda",
        "be11a242-499b-4b3c-89da-4e74be74d749",
        "b2a88084-3cce-4f28-9fba-eade82d04c49",
        "994e4a2e-dd00-4fb0-9e8e-771523a335d5",
        "D8BA9307-1AA5-4F0F-854C-5C72540F1B18",
        "8A00B2A0-A3A7-4762-BC21-447D7136825E",
        "B184C7FA-11F1-4C22-A7BE-77A11B49B35A",
        "17126E29-CDFE-4160-A476-C907BC4820C5",
        "dc63d1b7-77c1-4184-bc9c-71ecd5687474",
        "C6352DD2-9A0C-40D9-9895-431765877CAF",
        "BDF3B3A8-EA0F-4365-9B59-EDF1488EE7D4",
        "26ACDADA-DD1A-4102-AA53-48D6BEAE9EAA",
        "EAC24917-8D80-4560-91E3-E76FAB132714",
        "8AA33A8C-9562-4979-BA26-05B0F9B54C18",
        "8F79B5C6-BDED-4DCF-9BDB-3D8C15C9959E",
        "3ED96200-CD5E-4487-94C7-8F92BC5CD645",
        "9EAFEC2A-F27F-43BE-BF05-2F95B67A682B",
        "e902a8aa-9f16-42be-9ca9-f49b0c3f6c8a",
        "102f3d89-0d81-4c21-8ec1-256037f4e564",
        "edcb6859-5137-46bc-9df8-a0763755ddf4",
        "99a80b7e-366b-425d-8776-c9fb4a546a0c",
        "3764A525-FA00-4A28-A2AA-48BB2830B6D5",
        "23e82531-322d-4fd9-918e-940b330eece0",
        "7AD83AB3-190D-468D-B705-F366D6ECF693",
                                )

    fun append(context: Context, key: String, str: String) {
        // if (!version.contains(key)) {
        //     return
        // }
        if (TextUtils.isEmpty(str)) {
            return
        }
        try {
            if (!LogWriter.version_name.isNullOrEmpty()) {
                val count = LogWriter.version_name!!.count { it == '.' }
                if (count < 3) {
                    return
                }
            } else {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        CoroutineScope(Dispatchers.IO).launch {
            val string = StringBuffer()
            string.append(date(System.currentTimeMillis()) + "==")
            string.append("$key:")
            string.append(str)
            string.append("\n")
            if (DATA_PATH == null) {
                DATA_PATH = context.getExternalFilesDir(null)?.absolutePath
            }
            if (LOG_PATH == null) {
                LOG_PATH = "$DATA_PATH/LOG/"
            }
            val dir = File(LOG_PATH!!)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val log = File(dir, "event.txt")
            FileUtils.writeAppend(string.toString(), log.absolutePath)
        }
    }


    private val formatterSec = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private fun date(date: Long?): String? {
        if (date == null) {
            return ""
        }
        return formatterSec.format(Date(date))
    }

}