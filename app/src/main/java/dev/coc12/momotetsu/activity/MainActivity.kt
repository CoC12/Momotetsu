package dev.coc12.momotetsu.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.MapDrawer
import dev.coc12.momotetsu.core.MapManager
import dev.coc12.momotetsu.service.DiagonalScrollView

class MainActivity : AppCompatActivity() {
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapManager = MapManager(context)
        val mapDrawer = MapDrawer(context, mapManager = mapManager)
        val viewContainer: RelativeLayout = findViewById(R.id.view_container)
        val diagonalScrollView: DiagonalScrollView = findViewById(R.id.diagonal_scroll_view)
        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        scrollView.addView(mapDrawer)

        viewContainer.post {
            mapDrawer.setScroll(13, 11, diagonalScrollView, scrollView)
        }
    }
}