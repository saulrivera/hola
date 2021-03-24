package com.emr.tracking.utils

class KalmanFilter {
    var A = 1.0
    var B = 0.0
    var C = 1.0
    private var R: Double
    private var Q: Double
    var cov = Double.NaN
    var x = Double.NaN

    /**
     * Constructor
     *
     * @param R Process noise
     * @param Q Measurement noise
     * @param A State vector
     * @param B Control vector
     * @param C Measurement vector
     */
    constructor(R: Double, Q: Double, A: Double, B: Double, C: Double, cov: Double = Double.NaN, x: Double = Double.NaN) {
        this.R = R
        this.Q = Q
        this.A = A
        this.B = B
        this.C = C
        this.cov = cov
        this.x = x // estimated signal without noise
    }

    /**
     * Constructor
     *
     * @param R Process noise
     * @param Q Measurement noise
     */
    constructor(R: Double, Q: Double) {
        this.R = R
        this.Q = Q
    }

    /**
     * Filters a measurement
     *
     * @param measurement The measurement value to be filtered
     * @param u The controlled input value
     * @return The filtered value
     */
    fun filter(measurement: Double, u: Double): Double {
        if (java.lang.Double.isNaN(x)) {
            x = 1 / C * measurement
            cov = 1 / C * Q * (1 / C)
        } else {
            val predX = A * x + B * u
            val predCov = A * cov * A + R

            // Kalman gain
            val K = predCov * C * (1 / (C * predCov * C + Q))

            // Correction
            x = predX + K * (measurement - C * predX)
            cov = predCov - K * C * predCov
        }
        return x
    }

    /**
     * Filters a measurement
     *
     * @param measurement The measurement value to be filtered
     * @return The filtered value
     */
    fun filter(measurement: Double): Double {
        val u = 0.0
        if (java.lang.Double.isNaN(x)) {
            x = 1 / C * measurement
            cov = 1 / C * Q * (1 / C)
        } else {
            val predX = A * x + B * u
            val predCov = A * cov * A + R

            // Kalman gain
            val K = predCov * C * (1 / (C * predCov * C + Q))

            // Correction
            x = predX + K * (measurement - C * predX)
            cov = predCov - K * C * predCov
        }
        return x
    }

    /**
     * Set the last measurement.
     * @return The last measurement fed into the filter
     */
    fun lastMeasurement(): Double {
        return x
    }

    /**
     * Sets measurement noise
     *
     * @param noise The new measurement noise
     */
    fun setMeasurementNoise(noise: Double) {
        Q = noise
    }

    /**
     * Sets process noise
     *
     * @param noise The new process noise
     */
    fun setProcessNoise(noise: Double) {
        R = noise
    }
}