package com.example.a521lablearnandroid

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SensorViewModelTest {

    private lateinit var mockApplication: Application
    private lateinit var mockSensorManager: SensorManager
    private lateinit var mockLocationManager: LocationManager
    private lateinit var mockSensor: Sensor

    private lateinit var viewModel: SensorViewModel

    @Before
    fun setUp() {
        mockApplication = mockk()
        mockSensorManager = mockk(relaxed = true)
        mockLocationManager = mockk(relaxed = true)
        mockSensor = mockk(relaxed = true)

        every { mockApplication.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockApplication.getSystemService(Context.LOCATION_SERVICE) } returns mockLocationManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) } returns mockSensor

        viewModel = SensorViewModel(mockApplication)
    }

    @Test
    fun `test startSensors registers listener`() {
        viewModel.startSensors()

        verify { mockSensorManager.registerListener(viewModel, mockSensor, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    @Test
    fun `test stopSensors unregisters listener`() {
        viewModel.stopSensors()

        verify { mockSensorManager.unregisterListener(viewModel) }
    }

    @Test
    fun `test startLocationUpdates requests updates`() {
        viewModel.startLocationUpdates()

        verify { mockLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 0f, viewModel) }
        verify { mockLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 0f, viewModel) }
    }

    @Test
    fun `test stopLocationUpdates removes updates`() {
        viewModel.stopLocationUpdates()

        verify { mockLocationManager.removeUpdates(viewModel) }
    }

    @Test
    fun `test onLocationChanged updates state flow`() {
        val mockLocation = mockk<Location>()
        
        viewModel.onLocationChanged(mockLocation)

        assertEquals(mockLocation, viewModel.locationData.value)
    }

    @Test
    fun `test onSensorChanged updates accelerometer data`() {
        // ใช้ Reflection ในการสร้าง SensorEvent เพื่อนำมาทดสอบ
        val constructor = SensorEvent::class.java.declaredConstructors.firstOrNull { it.parameterTypes.size == 1 }
        constructor?.isAccessible = true
        val event = constructor?.newInstance(3) as? SensorEvent

        if (event != null) {
            val mockSensorEvent = mockk<Sensor>()
            every { mockSensorEvent.type } returns Sensor.TYPE_ACCELEROMETER
            
            // ใช้ Reflection กำหนดค่า sensor เนื่องจากเป็น public field
            val sensorField = SensorEvent::class.java.getField("sensor")
            sensorField.set(event, mockSensorEvent)
            
            event.values[0] = 1.0f
            event.values[1] = 2.0f
            event.values[2] = 3.0f

            viewModel.onSensorChanged(event)

            val updatedData = viewModel.accelerometerData.value
            assertEquals(1.0f, updatedData[0])
            assertEquals(2.0f, updatedData[1])
            assertEquals(3.0f, updatedData[2])
        }
    }
}
