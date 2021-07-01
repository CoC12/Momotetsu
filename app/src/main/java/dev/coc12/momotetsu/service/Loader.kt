package dev.coc12.momotetsu.service

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.sentry.Sentry
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class Loader(private val context: Context) {

    /**
     * Assetから画像を読み込みBitmapのListを返す。
     *
     * @param srcPaths List<String> 画像パスのList
     * @return List<Bitmap>
     */
    fun loadImageAssets(srcPaths: List<String>): List<Bitmap> {
        val assetManager: AssetManager = context.assets
        var inputStream: InputStream? = null
        val assetList = mutableListOf<Bitmap>()

        for (srcPath in srcPaths) {
            try {
                inputStream = assetManager.open(srcPath)
            } catch (e: IOException) {
                Sentry.captureException(e)
            }
            assetList.add(BitmapFactory.decodeStream(inputStream))
        }
        inputStream?.close()
        return assetList
    }

    /**
     * Bitmapからチップセットを取得する。
     *
     * @param resourceId マップのリソースID
     * @param chipSize チップサイズの1辺のピクセル数
     * @return Bitmapチップセット
     */
    fun loadChipSet(resourceId: Int, chipSize: Int): MutableList<Bitmap> {
        val chipSetList: MutableList<Bitmap> = mutableListOf()
        val options = BitmapFactory.Options()
        options.inScaled = false
        val src = BitmapFactory.decodeResource(context.resources, resourceId, options)

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
     * CSVファイルを読み込む。
     *
     * @param resourceId CSVのリソースID
     * @return CSVデータ List<List<String>>
     */
    fun loadCsvData(resourceId: Int): List<List<String>> {
        val csvData: MutableList<List<String>> = mutableListOf()
        try {
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine {
                csvData.add(it.split(","))
            }
            inputStream.close()
        } catch (e: IOException) {
            Sentry.captureException(e)
        }
        return csvData
    }

    /**
     * Jsonファイルを読み込む。
     *
     * @param resourceId JsonのリソースID
     * @return Jsonファイル JSONObject
     */
    fun loadJsonData(resourceId: Int): JSONObject {
        var jsonString = "{}"
        try {
            val inputStream: InputStream = context.resources.openRawResource(resourceId)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            jsonString = bufferedReader.readText()
            inputStream.close()
        } catch (e: IOException) {
            Sentry.captureException(e)
        }
        return JSONObject(jsonString)
    }
}