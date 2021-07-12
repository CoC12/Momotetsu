package dev.coc12.momotetsu.core

import kotlin.math.abs

class Point(val positionX: Int, val positionY: Int) {

    /**
     * 地点間の距離を返す。
     *
     * @return 地点間の距離
     */
    fun getDistance(point: Point): Int {
        return (abs(positionX - point.positionX) + abs(positionY - point.positionY)) / 3
    }

    override fun equals(other: Any?) =
        (other is Point) && positionX == other.positionX && positionY == other.positionY
}