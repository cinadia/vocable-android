package com.willowtree.vocable

import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.FakeLegacyCategoriesAndPhrasesRepository
import com.willowtree.vocable.presets.FakePresetCategoriesRepository
import com.willowtree.vocable.room.CategoryDto
import com.willowtree.vocable.room.CategorySortOrder
import com.willowtree.vocable.room.FakeStoredCategoriesRepository
import com.willowtree.vocable.utils.ConstantUUIDProvider
import com.willowtree.vocable.utils.FakeLocaleProvider
import com.willowtree.vocable.utils.locale.LocalesWithText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoriesUseCaseTest {

    private val fakePresetsRepository = FakeLegacyCategoriesAndPhrasesRepository()
    private val fakeStoredCategoriesRepository = FakeStoredCategoriesRepository()
    private val fakePresetCategoriesRepository = FakePresetCategoriesRepository()

    private fun createUseCase(): CategoriesUseCase {
        return CategoriesUseCase(
            fakePresetsRepository,
            ConstantUUIDProvider(),
            FakeLocaleProvider(),
            fakeStoredCategoriesRepository,
            fakePresetCategoriesRepository
        )
    }

    @Test
    fun `preset and stored categories returned`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update {
            listOf(
                createCategoryDto(
                    "customCategory1",
                    localizedName = LocalesWithText(mapOf("en_US" to "Custom")),
                    resourceId = 1
                ),
                createCategoryDto(
                    "customCategory2",
                    localizedName = LocalesWithText(mapOf("en_US" to "Other")),
                    resourceId = 2
                )
            )
        }

        fakePresetCategoriesRepository._presetCategories = listOf(
            Category.PresetCategory(
                categoryId = "presetCategory",
                sortOrder = 3,
                hidden = false,
                resourceId = 0
            )
        )

        val useCase = createUseCase()

        assertEquals(
            listOf(
                Category.StoredCategory(
                    categoryId = "customCategory1",
                    localizedName = LocalesWithText(mapOf("en_US" to "Custom")),
                    hidden = false,
                    sortOrder = 0
                ),
                Category.StoredCategory(
                    categoryId = "customCategory2",
                    localizedName = LocalesWithText(mapOf("en_US" to "Other")),
                    hidden = false,
                    sortOrder = 0
                ),
                Category.PresetCategory(
                    categoryId = "presetCategory",
                    sortOrder = 3,
                    hidden = false,
                    resourceId = 0
                )
            ),
            useCase.categories().first()
        )
    }

    @Test
    fun `category added to stored repository`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update { emptyList() }

        fakePresetCategoriesRepository._presetCategories = listOf(
            Category.PresetCategory(
                categoryId = "presetCategory",
                sortOrder = 3,
                hidden = false,
                resourceId = 0
            )
        )

        val useCase = createUseCase()

        useCase.addCategory("My Category", 0)

        assertEquals(
            listOf(
                Category.StoredCategory(
                    categoryId = "1",
                    localizedName = LocalesWithText(mapOf("en_US" to "My Category")),
                    hidden = false,
                    sortOrder = 0
                ),
                Category.PresetCategory(
                    categoryId = "presetCategory",
                    sortOrder = 3,
                    hidden = false,
                    resourceId = 0
                ),
            ),
            useCase.categories().first()
        )
        assertEquals(
            listOf(
                CategoryDto(
                    categoryId = "1",
                    creationDate = 0L,
                    resourceId = null,
                    localizedName = LocalesWithText(mapOf("en_US" to "My Category")),
                    hidden = false,
                    sortOrder = 0
                )
            ),
            fakeStoredCategoriesRepository._allCategories.first()
        )
    }

    @Test
    fun `sort order updated`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update {
            listOf(
                createCategoryDto(
                    categoryId = "storedCategory",
                    localizedName = null,
                    sortOrder = 0
                )
            )
        }

        fakePresetCategoriesRepository._presetCategories = listOf(
            Category.PresetCategory(
                categoryId = "presetCategory",
                sortOrder = 1,
                hidden = false,
                resourceId = 0
            )
        )

        val useCase = createUseCase()

        useCase.updateCategorySortOrders(
            listOf(
                CategorySortOrder("storedCategory", 1),
                CategorySortOrder("presetCategory", 0)
            )
        )

        assertEquals(
            listOf(
                Category.StoredCategory(
                    "storedCategory",
                    localizedName = null,
                    hidden = false,
                    sortOrder = 1
                ),
                Category.PresetCategory(
                    "presetCategory",
                    sortOrder = 0,
                    hidden = false,
                    resourceId = 0
                )
            ),
            useCase.categories().first()
        )
    }

    @Test
    fun `get category by id returns presets and stored`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update {
            listOf(
                createCategoryDto(
                    categoryId = "storedCategory",
                    localizedName = null,
                    sortOrder = 0
                )
            )
        }

        fakePresetCategoriesRepository._presetCategories = listOf(
            Category.PresetCategory(
                categoryId = "presetCategory",
                sortOrder = 1,
                hidden = false,
                resourceId = 0
            )
        )

        val useCase = createUseCase()

        assertEquals(
            Category.StoredCategory(
                "storedCategory",
                localizedName = null,
                hidden = false,
                sortOrder = 0
            ),
            useCase.getCategoryById("storedCategory")
        )
        assertEquals(
            Category.PresetCategory(
                "presetCategory",
                sortOrder = 1,
                hidden = false,
                resourceId = 0
            ),
            useCase.getCategoryById("presetCategory")
        )
    }

    @Test
    fun `update category name updates stored category`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update {
            listOf(
                createCategoryDto(
                    categoryId = "storedCategory1",
                    localizedName = LocalesWithText(mapOf("en_US" to "storedCategory1"))
                )
            )
        }

        val useCase = createUseCase()

        useCase.updateCategoryName("storedCategory1", LocalesWithText(mapOf("en_US" to "newStoredCategory1")))

        assertEquals(
            Category.StoredCategory(
                "storedCategory1",
                localizedName = LocalesWithText(mapOf("en_US" to "newStoredCategory1")),
                hidden = false,
                sortOrder = 0
            ),
            useCase.getCategoryById("storedCategory1")
        )
    }

    @Test
    fun `update category name hides preset category and creates stored category with new name`() = runTest {
        fakePresetCategoriesRepository._presetCategories = listOf(
            Category.PresetCategory(
                categoryId = "presetCategory1",
                sortOrder = 1,
                hidden = false,
                resourceId = 0
            )
        )

        val useCase = createUseCase()

        useCase.updateCategoryName("presetCategory1", LocalesWithText(mapOf("en_US" to "newPresetCategory1")))

        assertEquals(
            Category.StoredCategory(
                "presetCategory1",
                sortOrder = 1,
                hidden = false,
                localizedName = LocalesWithText(mapOf("en_US" to "newPresetCategory1")),
            ),
            useCase.getCategoryById("presetCategory1")
        )
    }

    @Test
    fun `update category hidden updates stored category`() = runTest {
        fakeStoredCategoriesRepository._allCategories.update {
            listOf(
                createCategoryDto(
                    categoryId = "storedCategory1",
                    localizedName = LocalesWithText(mapOf("en_US" to "storedCategory1")),
                    hidden = false
                )
            )
        }

        val useCase = createUseCase()

        useCase.updateCategoryHidden("storedCategory1", true)

        assertEquals(
            Category.StoredCategory(
                "storedCategory1",
                localizedName = LocalesWithText(mapOf("en_US" to "storedCategory1")),
                hidden = true,
                sortOrder = 0
            ),
            useCase.getCategoryById("storedCategory1")
        )
    }

    private fun createCategoryDto(
        categoryId: String,
        creationDate: Long = 0L,
        resourceId: Int? = null,
        localizedName: LocalesWithText? = LocalesWithText(mapOf("en_US" to "category")),
        hidden: Boolean = false,
        sortOrder: Int = 0
    ): CategoryDto = CategoryDto(
        categoryId = categoryId,
        creationDate = creationDate,
        resourceId = resourceId,
        localizedName = localizedName,
        hidden = hidden,
        sortOrder = sortOrder,
    )

}