package com.jimmy.ml_firebase.helpers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * hold the logic for getting the device’s orientation angles
 */
interface TiltListener {

    fun onTilt(pitchRollRad: Pair<Double, Double>)
}

interface TiltSensor {

    fun addListener(tiltListener: TiltListener)

    fun register()

    fun unregister()
}



class TiltRollSensor(context: Context) : SensorEventListener, TiltSensor {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accSensor: Sensor
    private val magneticSensor: Sensor
    private var listeners = mutableListOf<TiltListener>()

    private val rotationMatrix = FloatArray(9)
    private val accelerometerValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private val orientationAngles = FloatArray(3)

    init {
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //
    }


    /**
     *  implement the tilt detection logic in onSensorChanged.
     *  The sensor data you need is the angle the phone is on the x and y axis as it rotates.
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, magneticValues.size)
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        /*
         pitch (degrees of rotation about the x axis) and roll (degrees of rotation about the y axis) in radians.
         Since you’re translating the phone’s rotation to a 2D motion,
         the azimuth (degrees of rotation about the -z axis) doesn’t matter.

         */
        val pitchInRad = orientationAngles[1].toDouble()
        val rollInRad = orientationAngles[2].toDouble()

        val pair = Pair(pitchInRad, rollInRad)
        listeners.forEach {
            it.onTilt(pair)
        }
    }

    override fun addListener(tiltListener: TiltListener) {
        listeners.add(tiltListener)
    }

    override fun register() {
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun unregister() {
        listeners.clear()
        sensorManager.unregisterListener(this, accSensor)
        sensorManager.unregisterListener(this, magneticSensor)
    }


}