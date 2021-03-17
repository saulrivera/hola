package com.emr.tracking.model

import org.springframework.data.annotation.Id

data class Gateway(
    @Id
    val uniqueId: String,
    val mac: String,
    val floor: Int,
    val position: Pair<Double, Double> = Pair(-10.0, 5.2)
)