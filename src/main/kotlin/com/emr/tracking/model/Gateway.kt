package com.emr.tracking.model

data class Gateway(
    val mac: String,
    val uniqueId: String,
    val floor: Long,
    val siblings: List<String> = listOf(),
    val position: Pair<Double, Double> = Pair(-10.0, 5.2)
)