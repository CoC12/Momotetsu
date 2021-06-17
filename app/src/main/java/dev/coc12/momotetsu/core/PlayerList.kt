package dev.coc12.momotetsu.core

import dev.coc12.momotetsu.room.Player

class PlayerList {
    var player: MutableList<Player> = mutableListOf()
    var turnIndex: Int = 0

    fun addPlayer(player: Player) {
        this.player.add(player)
    }

    fun setPlayers(players: List<Player>) {
        this.player = players as MutableList<Player>
    }

    fun getTurnPlayer(): Player {
        return player[turnIndex]
    }

    fun changeTurn() {
        if (++turnIndex >= player.size) {
            turnIndex = 0
        }
    }
}
