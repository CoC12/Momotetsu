package dev.coc12.momotetsu.service

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class Loader {

    /**
     * Bitmapからチップセットを取得する。
     *
     * @param resource Resources
     * @param resourceId マップのリソースID
     * @param chipSize チップサイズの1辺のピクセル数
     * @return Bitmapチップセット
     */
    fun loadChipSet(resource: Resources, resourceId: Int, chipSize: Int): MutableList<Bitmap> {
        val chipSetList: MutableList<Bitmap> = mutableListOf()
        val options = BitmapFactory.Options()
        options.inScaled = false
        val src = BitmapFactory.decodeResource(resource, resourceId, options)

        for (i in 0 until src.width / chipSize) {
            chipSetList.add(
                Bitmap.createBitmap(
                    src,
                    i * chipSize,
                    0,
                    chipSize,
                    chipSize
                )
            )
        }
        return chipSetList
    }

    /**
     * CSVファイルからMap情報を取得する。
     *
     * @param resource Resources
     * @param resourceId マップのリソースID
     * @return Map情報
     */
    fun loadMapData(resource: Resources, resourceId: Int): MutableList<List<String>> {
        val map: MutableList<List<String>> = mutableListOf()
        try {
            val inputStream: InputStream = resource.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine {
                map.add(it.split(","))
            }
            inputStream.close()
        } catch (e: IOException) {
        }
        return map
    }
}