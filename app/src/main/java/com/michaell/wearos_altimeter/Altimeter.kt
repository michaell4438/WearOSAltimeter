package com.michaell.wearos_altimeter

import kotlin.math.floor
import kotlin.math.pow

class Altimeter {
    var setting: Double = 1013.25;
    var settingInHg: Double
        get() {
            return setting * 0.02953;
        }
        set(value) {
            setting = value / 0.02953;
        }

    fun incrementSetting() {
        settingInHg += 0.01;
    }

    fun decrementSetting() {
        settingInHg -= 0.01;
    }

    var pressure: Double = 1013.25
    var pressureInHg: Double
        get() {
            return pressure * 0.02953;
        }
        set(value) {
            pressure = value / 0.02953;
        }

    var altitude: Double = 0.0
        private set
        get() = computeAltitude()

    var altitudeString: String = ""
        private set
        get() = computeAltitudeRounded().toString()

    fun computeAltitudeRounded(press: Double = pressure, set: Double = setting): Int {
        return (floor(computeAltitude(press)/10)*10).toInt()
    }

    fun computeAltitude(press: Double = pressure, set: Double = setting): Double {
        val L = 0.0065;
        val TO = 288.15;
        val g = 9.80665;
        val M = 0.0289644;
        val R = 8.31432;

        val exponent = (g * M) / (R * L);
        val alt = ((set / press).pow(1 / exponent) - 1) * (TO / L)
        // Convert to feet
        return alt * 3.28084;
    }

    companion object {
        fun convertHpaToInHg(hpa: Double): Double {
            return hpa * 0.02953;
        }
    }
}