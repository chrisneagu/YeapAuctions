package ro.ase.dam.yeapauctions.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.saveable.listSaver

enum class CategoryName {
    AGRICULTURE,
    CONSTRUCTION_EARTH,
    FOOD_MACHINERY,
    METAL_WORKING,
    MORE_INDUSTRIAL_CATEGORIES,
    REAL_ESTATE,
    RETAIL_OFFICE,
    TRANSPORT_LOGISTICS,
    WOODWORKING
}

data class Category (
    @StringRes
    val nameResId: Int = 0,
    @DrawableRes
    val iconResId: Int = 0,
    val categoryName: CategoryName = CategoryName.AGRICULTURE
    )

val CategorySaver = listSaver<Category, Any>(
    save = { listOf(it.nameResId, it.iconResId, it.categoryName) },
    restore = { Category(it[0] as Int, it[1] as Int, it[2] as CategoryName) }
)