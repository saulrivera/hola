package com.emr.tracking.model

import org.springframework.data.annotation.Id

data class Gateway(
    @Id
    val uniqueId: String,
    val mac: String,
    val position: Pair<Double, Double> = Pair(0.0, 10.0)
)