package dev.coc12.momotetsu.core.manager

import android.content.Context
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.Loader
import java.util.*

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
     * 駅間の距離を返す。
     *
     * @param from Pair<Int, Int> 開始駅の座標
     * @param to Pair<Int, Int> 目的駅の座標
     * @return 駅間の距離
     */
    fun getDistance(from: Pair<Int, Int>, to: Pair<Int, Int>): Int? {
        val points = LinkedList(listOf(from))
        val distances = mutableMapOf(from to 0)

        while (points.isNotEmpty()) {
            val point = points.poll()
            val neighborPointDistance = distances[point!!]!!.plus(1)

            for (neighborPoint in getNeighborPoints(point)) {
                if (neighborPoint == to) {
                    return neighborPointDistance
                }

                if (!distances.contains(neighborPoint)) {
                    distances[neighborPoint] = neighborPointDistance
                    points.add(neighborPoint)
                }
            }
        }
        return null
    }

    /**
     * 周囲の地点を返す。
     *
     * @return List<Pair<Int, Int>> 周囲の地点のリスト
     */
    private fun getNeighborPoints(point: Pair<Int, Int>): List<Pair<Int, Int>> {
        val directions = listOf(
            Pair(0, 1),
            Pair(0, -1),
            Pair(1, 0),
            Pair(-1, 0),
        )
        val landAndSeaSquares = Constants.LAND_AND_SEA_SQUARES
        val roadSquares = Constants.ROAD_SQUARES

        val neighbours: MutableList<Pair<Int, Int>> = mutableListOf()
        for (direction in directions) {
            for (i in 1..getMaxSize()) {
                val targetPosX = point.first + direction.first * i
                val targetPosY = point.second + direction.second * i
                val squareInfo = getSquareInfo(targetPosX, targetPosY)

                if (landAndSeaSquares.contains(squareInfo)) {
                    break
                }
                if (roadSquares.contains(squareInfo)) {
                    continue
                }
                neighbours.add(Pair(targetPosX, targetPosY))
                break
            }
        }
        return neighbours
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