package dev.coc12.momotetsu.core

import android.content.Context
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.service.Loader

class MapManager(context: Context) {
    private var mapData = Loader().loadMapData(context.resources, R.raw.map)

    /**
     * マップの幅を取得する。
     *
     * @return マップ幅
     */
    fun getWidth(): Int {
        return mapData[0].size
    }

    /**
     * マップの高さを取得する。
     *
     * @return マップ高さ
     */
    fun getHeight(): Int {
        return mapData.size
    }

    fun withIndex(): Iterable<IndexedValue<List<String>>> {
        return mapData.withIndex()
    }
}