package dev.coc12.momotetsu.core

open class ListItem(
    val name: String,
    var isSelected: Boolean = false
) {

    /**
     * リストの項目の選択状態を反転させます。
     */
    fun toggleIsSelected() {
        isSelected = !isSelected
    }
}