package dev.coc12.momotetsu.core

class RealEstateListItem(
    name: String,
    val price: Int,
    val rate: Int,
    val backgroundColor: Int?,
) : ListItem(name)