package com.willowtree.vocable.room

import androidx.room.*

@Dao
interface CategoryPhraseCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryPhraseCrossRef(categoryPhraseCrossRef: CategoryPhraseCrossRef)

    @Delete
    suspend fun deleteCategoryPhraseCrossRef(categoryPhraseCrossRef: CategoryPhraseCrossRef)

    @Delete
    suspend fun deleteCategoryPhraseCrossRefs(vararg crossRefs: CategoryPhraseCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryPhraseCrossRefs(vararg crossRefs: CategoryPhraseCrossRef)

    @Query("SELECT * FROM CategoryPhraseCrossRef WHERE phrase_id IN (:phraseIds)")
    suspend fun getCategoryPhraseCrossRefsForPhraseIds(phraseIds: List<String>): List<CategoryPhraseCrossRef>
}