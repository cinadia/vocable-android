package com.willowtree.vocable.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(vararg categories: CategoryDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryDto)

    @Query("SELECT * FROM Category WHERE category_id != 'preset_user_favorites' ORDER BY sort_order ASC")
    suspend fun getAllCategories(): List<CategoryDto>

    @Query("SELECT * FROM Category WHERE category_id != 'preset_user_favorites' ORDER BY sort_order ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryDto>>

    @Query("SELECT * FROM Category WHERE category_id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryDto

    @Delete
    suspend fun deleteCategory(category: CategoryDto)

    @Update
    suspend fun updateCategory(category: CategoryDto)

    @Update
    suspend fun updateCategories(vararg categories: CategoryDto)

    @Query("SELECT COUNT(*) FROM Category WHERE NOT hidden")
    suspend fun getNumberOfShownCategories(): Int

    @Transaction
    @Query("SELECT * FROM Category WHERE category_id == :categoryId")
    suspend fun getCategoryWithPhrases(categoryId: String): CategoryWithPhrases?
}