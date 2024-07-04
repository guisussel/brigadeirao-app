package com.sussel.brigadeirao.data

import com.sussel.brigadeirao.R

// TODO: fetch this data from database

object DataSource {
    val fillings = listOf(
        R.string.leite_condensado,
        R.string.baileys,
        R.string.whisky,
        R.string.doce_de_leite
    )

    val quantityOptions = listOf(
        Pair(R.string.one_brigadeiro, 1),
        Pair(R.string.four_brigadeiros, 4),
        Pair(R.string.ten_brigadeiros, 10)
    )
}