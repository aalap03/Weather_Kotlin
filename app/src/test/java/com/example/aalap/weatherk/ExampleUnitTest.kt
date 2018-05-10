package com.example.aalap.weatherk

import com.example.aalap.weatherk.View.MainView
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @get:Rule
    val rule = MockitoJUnit.rule()

    lateinit var presenter:Presenter

    lateinit var view:MainView


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun shouldPass() {
        assertSame(4,4)
    }

    val cityName = "Montreal"
    @Test
    fun shouldShowPlaceName(){

        view = mock(MainView::class.java)
        presenter = mock(Presenter::class.java)
        presenter.showPlaceName(cityName)
        verify(view).showPlaceName(cityName)

    }
}
