package com.example.a521lablearnandroid

import app.cash.turbine.test
import com.example.a521lablearnandroid.utils.PokedexResponse
import com.example.a521lablearnandroid.utils.PokemonEntry
import com.example.a521lablearnandroid.utils.PokemonNetwork
import com.example.a521lablearnandroid.utils.PokemonSpecies
import io.mockk.coEvery
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // จำลอง object PokemonNetwork เพื่อไม่ให้ยิง API จริง
        mockkObject(PokemonNetwork)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `test fetchPokemon updates state flow correctly`() = runTest {
        // จัดเตรียมข้อมูลจำลอง
        val mockEntries = listOf(
            PokemonEntry(1, PokemonSpecies("Bulbasaur", "url1")),
            PokemonEntry(2, PokemonSpecies("Ivysaur", "url2"))
        )
        val mockResponse = PokedexResponse(mockEntries)

        // กำหนดให้เมื่อเรียก api.getKantoPokedex() ให้คืนค่า mockResponse
        coEvery { PokemonNetwork.api.getKantoPokedex() } returns mockResponse

        // สร้าง ViewModel (พอมันถูกสร้าง โค้ดใน init { fetchPokemon() } จะทำงานทันที)
        val viewModel = PokemonViewModel()

        // ใช้ Turbine เพื่อดักจับข้อมูลที่ถูก emit ออกมาใน StateFlow
        viewModel.pokemonList.test {
            // ค่าแรกสุดตอนเริ่ม คือ emptyList
            val initialState = awaitItem()
            assertEquals(0, initialState.size)

            // สั่งให้ Coroutine ที่รันอยู่ทำงานต่อจนเสร็จ
            testDispatcher.scheduler.advanceUntilIdle()

            // ดักจับค่าถัดไปที่ถูก emit หลังจาก mockResponse ยิงเสร็จ
            val loadedState = awaitItem()
            assertEquals(2, loadedState.size)
            assertEquals("Bulbasaur", loadedState[0].pokemon_species.name)
            assertEquals(1, loadedState[0].entry_number)
        }
    }

    @Test
    fun `test fetchPokemon handles error gracefully`() = runTest {
        // เมื่อยิง API แล้วเกิด Error เช่น Network Exception
        coEvery { PokemonNetwork.api.getKantoPokedex() } throws Exception("Network Error")

        val viewModel = PokemonViewModel()

        viewModel.pokemonList.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.size)

            testDispatcher.scheduler.advanceUntilIdle()
            
            // รอรับ event ถ้าเกิดมัน emit ซ้ำ, แต่ในกรณี Error ในโค้ดไม่ได้ emit อะไรเพิ่ม
            // ดังนั้นเราไม่ควรคาดหวัง ExpectItem เพิ่ม ให้จบ flow ตรงนี้เลย
            expectNoEvents()
        }
    }
}
