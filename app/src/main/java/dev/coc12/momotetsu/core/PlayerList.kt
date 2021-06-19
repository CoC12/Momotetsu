package dev.coc12.momotetsu.core

import dev.coc12.momotetsu.room.Player

class PlayerList {
    private var player: MutableList<Player> = mutableListOf()
    var turnIndex: Int = 0

    /**
     * プレイヤーを追加する。
     *
     * @param player Player
     */
    fun addPlayer(player: Player) {
        this.player.add(player)
    }

    /**
     * プレイヤーをセットする。
     *
     * @param players List<Player>
     */
    fun setPlayers(players: List<Player>) {
        this.player = players as MutableList<Player>
    }

    /**
     * ターンプレイヤーを取得する。
     *
     * @return Player
     */
    fun getTurnPlayer(): Player {
        return player[turnIndex]
    }

    /**
     * ターンプレイヤーを次のプレーヤーにする。
     *
     * @return Boolean 年目も変わったか
     */
    fun changeTurn(): Boolean {
        if (++turnIndex >= player.size) {
            turnIndex = 0
            return true
        }
        return false
    }
}
