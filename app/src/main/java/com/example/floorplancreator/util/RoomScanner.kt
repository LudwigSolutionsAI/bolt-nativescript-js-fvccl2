package com.example.floorplancreator.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.floorplancreator.data.Room
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RoomScanner(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var isScanning = false

    fun startScanning() {
        isScanning = true
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopScanning() {
        isScanning = false
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isScanning) return

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    suspend fun scanRoom(): Room = suspendCancellableCoroutine { continuation ->
        startScanning()

        // Simulate room scanning process
        android.os.Handler().postDelayed({
            stopScanning()
            val scannedRoom = Room(
                projectId = "", // This should be set when adding the room to a project
                name = "Scanned Room",
                width = 5f, // Example values
                length = 4f,
                height = 2.5f
            )
            continuation.resume(scannedRoom)
        }, 5000) // Simulate 5 seconds of scanning

        continuation.invokeOnCancellation {
            stopScanning()
        }
    }
}