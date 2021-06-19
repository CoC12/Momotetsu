package dev.coc12.momotetsu.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.GameManager
import dev.coc12.momotetsu.room.Player
import dev.coc12.momotetsu.service.DiagonalScrollView

class MainActivity : AppCompatActivity() {
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val containerView: RelativeLayout = findViewById(R.id.view_container)
        val diagonalScrollView: DiagonalScrollView = findViewById(R.id.diagonal_scroll_view)
        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        val gameManager = GameManager(context, containerView, diagonalScrollView, scrollView)

        val player1 = Player(0, "test1")
        val player2 = Player(0, "test2")
        gameManager.addPlayer(player1)
        gameManager.addPlayer(player2)
        gameManager.startGame()
    }
}