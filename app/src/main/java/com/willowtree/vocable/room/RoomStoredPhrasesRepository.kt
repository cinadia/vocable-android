package com.willowtree.vocable.room

import com.willowtree.vocable.presets.Phrase
import com.willowtree.vocable.presets.asPhrase
import com.willowtree.vocable.utils.DateProvider

class RoomStoredPhrasesRepository(
    private val database: VocableDatabase,
    private val dateProvider: DateProvider,
) : StoredPhrasesRepository {
    override suspend fun addPhrase(phrase: PhraseDto) {
        database.phraseDao().insertPhrase(phrase)
    }

    override suspend fun updatePhraseLastSpokenTime(phraseId: String) {
        database.phraseDao().updatePhraseSpokenDate(
            PhraseSpokenDate(
                phraseId = phraseId,
                lastSpokenDate = dateProvider.currentTimeMillis()
            )
        )
    }

    override suspend fun getRecentPhrases(): List<Phrase> {
        return database.phraseDao().getRecentPhrases().map { it.asPhrase() }
    }

    override suspend fun getPhrasesForCategory(categoryId: String): List<Phrase> {
        return database.phraseDao().getPhrasesForCategory(categoryId).map { it.asPhrase() }
    }
}