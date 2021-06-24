package dev.coc12.momotetsu.core

import android.content.Context
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.Loader

class MapManager(context: Context) {
    private val mapData = Loader(context).loadCsvData(R.raw.map)

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

    /**
     * マップの最大辺を返す。
     *
     * @return マップの最大辺
     */
    fun getMaxSize(): Int {
        return maxOf(mapData[0].size, mapData.size)
    }

    /**
     * マップの情報を返す。
     *
     * @param x Int
     * @param y Int
     * @return マップの情報
     */
    fun getSquareInfo(x: Int, y: Int): String {
        return try {
            mapData[y][x]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Constants.SQUARE_NONE
        }
    }

    /**
     * 該当マスの周囲の道路を返す。
     *
     * @param x Int
     * @param y Int
     * @return 道路の有無
     */
    fun getSquareDirections(x: Int, y: Int): List<Int> {
        val roads: MutableList<Int> = mutableListOf()
        val roadSquares = Constants.ROAD_SQUARES

        if (roadSquares.contains(getSquareInfo(x + 1, y))) {
            roads.add(1)
        }
        if (roadSquares.contains(getSquareInfo(x, y + 1))) {
            roads.add(2)
        }
        if (roadSquares.contains(getSquareInfo(x - 1, y))) {
            roads.add(3)
        }
        if (roadSquares.contains(getSquareInfo(x, y - 1))) {
            roads.add(4)
        }
        return roads
    }

    fun withIndex(): Iterable<IndexedValue<List<String>>> {
        return mapData.withIndex()
    }
}