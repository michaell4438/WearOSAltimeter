/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.michaell.wearos_altimeter.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.Text
import com.michaell.wearos_altimeter.Altimeter
import com.michaell.wearos_altimeter.presentation.theme.WearOSAltimiterTheme
import kotlin.math.floor

val altimeter by mutableStateOf(Altimeter())

lateinit var sensorManager: SensorManager
var pressure by mutableDoubleStateOf(altimeter.pressureInHg)
var setting by mutableDoubleStateOf(altimeter.settingInHg)

class SensorListener : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            pressure = Altimeter.convertHpaToInHg(event.values[0].toDouble())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            WearApp()
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            SensorListener(),
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }
}

fun rotaryScrollEvent(pixels: Float): Boolean {
    setting += pixels / 13000
    return true
}

@Composable
fun WearApp() {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    WearOSAltimiterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .focusRequester(focusRequester)
                .onRotaryScrollEvent {
                    rotaryScrollEvent(it.verticalScrollPixels)
                }
                .focusable(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AltitudeDisplay()
                SettingStepper()
            }
        }
    }

    LaunchedEffect(Unit){
        focusRequester.requestFocus()
    }
}

@Composable
fun SettingStepper() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(vertical = 3.dp)) {
            Button(
                onClick = {
                    setting += 1.0
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "+1", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
            Button(
                onClick = {
                    setting += 0.1
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "+0.1", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
            Button(
                onClick = {
                    setting += 0.01
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "+0.01", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
        }
        Row(modifier = Modifier.padding(vertical = 3.dp)) {
            Button(
                onClick = {
                    setting -= 1.0
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "-1", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
            Button(
                onClick = {
                    setting -= 0.1
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "-0.1", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
            Button(
                onClick = {
                    setting -= 0.01
                },
                modifier = Modifier.size(50.dp, 20.dp).padding(horizontal = 2.dp)
            ) {
                Text(text = "-0.01", fontSize = TextUnit(10f, TextUnitType.Sp))
            }
        }
    }
}

@Composable
fun AltitudeDisplay() {
    altimeter.pressureInHg = pressure
    altimeter.settingInHg = setting

    Text(
        text = altimeter.computeAltitudeRounded().toString() + " ft",
        fontSize = TextUnit(30f, TextUnitType.Sp)
    )
    Text(
        text = "Current Pressure: " +(floor(altimeter.pressureInHg * 100) / 100).toString() + " inHg",
        fontSize = TextUnit(8f, TextUnitType.Sp)
    )
    Divider(modifier = Modifier.padding(5.dp))
    Text(
        text = (floor(altimeter.settingInHg * 100) / 100).toString() + " inHg",
        fontSize = TextUnit(12f, TextUnitType.Sp),
        modifier = Modifier.padding(bottom = 3.dp)
    )
}