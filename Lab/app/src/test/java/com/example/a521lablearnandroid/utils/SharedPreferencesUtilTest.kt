package com.example.a521lablearnandroid.utils

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SharedPreferencesUtilTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        // ใช้ Reflection เพื่อ clear ตัวแปร singleton ที่อาจค้างอยู่ก่อนทุกการรันแต่ละเทส
        val field = SharedPreferencesUtil::class.java.getDeclaredField("sharedPreferences")
        field.isAccessible = true
        field.set(SharedPreferencesUtil, null)

        mockContext = mockk()
        mockSharedPreferences = mockk()
        mockEditor = mockk(relaxed = true)

        // จำลองการคืนค่าให้ Context และ SharedPreferences
        every { mockContext.getSharedPreferences(any(), Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor

        SharedPreferencesUtil.init(mockContext)
    }

    @After
    fun tearDown() {
        val field = SharedPreferencesUtil::class.java.getDeclaredField("sharedPreferences")
        field.isAccessible = true
        field.set(SharedPreferencesUtil, null)
    }

    @Test
    fun `test saveString calls edit and putString correctly`() {
        SharedPreferencesUtil.saveString("test_key", "test_value")

        verify { mockEditor.putString("test_key", "test_value") }
        verify { mockEditor.apply() }
    }

    @Test
    fun `test getString retrieves data correctly`() {
        every { mockSharedPreferences.getString("test_key", "") } returns "saved_value"

        val result = SharedPreferencesUtil.getString("test_key")

        assertEquals("saved_value", result)
    }

    @Test
    fun `test saveInt calls edit and putInt correctly`() {
        SharedPreferencesUtil.saveInt("int_key", 100)

        verify { mockEditor.putInt("int_key", 100) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `test getInt retrieves default value when empty`() {
        every { mockSharedPreferences.getInt("int_key", 0) } returns 0

        val result = SharedPreferencesUtil.getInt("int_key")

        assertEquals(0, result)
    }

    @Test
    fun `test saveBoolean calls edit and putBoolean correctly`() {
        SharedPreferencesUtil.saveBoolean("bool_key", true)

        verify { mockEditor.putBoolean("bool_key", true) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `test getBoolean retrieves data correctly`() {
        every { mockSharedPreferences.getBoolean("bool_key", false) } returns true

        val result = SharedPreferencesUtil.getBoolean("bool_key")

        assertTrue(result)
    }

    @Test
    fun `test remove deletes speficic key correctly`() {
        SharedPreferencesUtil.remove("some_key")

        verify { mockEditor.remove("some_key") }
        verify { mockEditor.apply() }
    }

    @Test
    fun `test clearAll clears all data`() {
        SharedPreferencesUtil.clearAll()

        verify { mockEditor.clear() }
        verify { mockEditor.apply() }
    }
}
