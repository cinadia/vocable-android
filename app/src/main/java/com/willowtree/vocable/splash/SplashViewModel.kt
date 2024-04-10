package com.willowtree.vocable.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willowtree.vocable.room.RoomPresetPhrasesRepository
import com.willowtree.vocable.utils.VocableSharedPreferences
import kotlinx.coroutines.launch

class SplashViewModel(
    private val newPresetsRepository: RoomPresetPhrasesRepository,
    private val sharedPrefs: VocableSharedPreferences
) : ViewModel() {

    private val liveExitSplash = MutableLiveData(false)
    val exitSplash: LiveData<Boolean> = liveExitSplash

    init {
        populateDatabase()
    }

    private fun populateDatabase() {
        viewModelScope.launch {
            if (sharedPrefs.getFirstTime()) {
                newPresetsRepository.populateDatabase()
                sharedPrefs.setFirstTime()
            }

            liveExitSplash.postValue(true)
        }
    }
}
