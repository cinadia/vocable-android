package com.willowtree.vocable.room

import com.willowtree.vocable.presets.PresetPhrase

interface PresetPhrasesRepository {
    suspend fun populateDatabase()
    suspend fun getAllPresetPhrases(): List<PresetPhrase>
}