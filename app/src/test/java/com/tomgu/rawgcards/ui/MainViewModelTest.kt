package com.tomgu.rawgcards.ui

import android.content.Context
import android.content.SharedPreferences
import com.tomgu.rawgcards.main.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    private lateinit var pagePreferences: SharedPreferences
    lateinit var mockEditor : SharedPreferences.Editor

    lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        this.mainViewModel = MainViewModel()
        this.mainViewModel.pagePreferences = pagePreferences

        mockEditor = mock(SharedPreferences.Editor::class.java)

        val context: Context = mock<Context>(Context::class.java)

        `when`(pagePreferences.edit()).thenReturn(mockEditor)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(pagePreferences)


    }

    @Test
    fun `should set category`() {
        mainViewModel.setCategorieToApi("Racing")
        mainViewModel.setCategorieToApi("Action")
        assertEquals(mainViewModel.categorie, "4")
    }

    @Test
    fun `update hashmap and save in preferences`() {
        mainViewModel.incrementCurrentPage()
        verify(mockEditor, atLeastOnce()).putInt(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(2))
    }


    @Test
    fun `see if pages reset`() {
        mainViewModel.incrementCurrentPage()
        mainViewModel.resetAllPages()
        assertEquals(5, mainViewModel.getHashMap().values.size)
        assertTrue(mainViewModel.getHashMap().values.all { it == 1 })
    }

    @Test
    fun `get pages from preferences`(){
        `when`(pagePreferences.getInt(anyString(), anyInt())).thenReturn(1)
        mainViewModel.getHashMapFromPreferences()
        verify(pagePreferences, times(5)).getInt(anyString(), anyInt())
        assertEquals(mainViewModel.getActionKey(), pagePreferences.getInt(anyString(), anyInt()))

    }
}