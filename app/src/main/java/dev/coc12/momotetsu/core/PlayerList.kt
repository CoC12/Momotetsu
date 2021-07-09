package dev.coc12.momotetsu.core

import dev.coc12.momotetsu.room.Player

class PlayerList {
    private var players: MutableList<Player> = mutableListOf()
    var turnIndex: Int = 0

    /**
     * プレイヤーを追加する。
     *
     * @param player Player
     */
    fun addPlayer(player: Player) {
        this.players.add(player)
    }

    /**
     * プレイヤーをセットする。
     *
     * @param players List<Player>
     */
    fun setPlayers(players: List<Player>) {
        this.players = players as MutableList<Player>
    }

    /**
     * ターンプレイヤーを取得する。
     *
     * @return Player
     */
    fun getTurnPlayer(): Player {
        return players[turnIndex]
    }

    /**
     * プレイヤーIDリストを返す
     *
     * @return List<Long>
     */
    fun getPlayerIds(): List<Long> {
        return players.map { it.playerId }
    }

    /**
     * プレイヤーのインデックスを返す
     *
     * @return Int
     */
    fun getPlayerIndex(targetPlayer: Player): Int {
        for ((index, player) in players.withIndex()) {
            if (player.playerId == targetPlayer.playerId) {
                return index
            }
        }
        return -1
    }

    /**
     * ターンプレイヤーを次のプレーヤーにする。
     *
     * @return Boolean 年目も変わったか
     */
    fun changeTurn(): Boolean {
        if (++turnIndex >= players.size) {
            turnIndex = 0
            return true
        }
        return false
    }

    /**
     * インデックス付きのPlayerListを返す。
     */
    fun withIndex(): Iterable<IndexedValue<Player>> {
        return players.withIndex()
    }
}
