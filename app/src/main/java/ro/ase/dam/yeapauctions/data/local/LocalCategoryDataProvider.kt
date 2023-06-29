package ro.ase.dam.yeapauctions.data.local

import ro.ase.dam.yeapauctions.R
import ro.ase.dam.yeapauctions.data.Category
import ro.ase.dam.yeapauctions.data.CategoryName

object LocalCategoryDataProvider {
    private val allCategories = listOf(
        Category(
            nameResId = R.string.agriculture,
            iconResId = R.drawable.ic_agriculture,
            categoryName = CategoryName.AGRICULTURE
        ),
        Category(
            nameResId = R.string.construction_earth,
            iconResId = R.drawable.ic_construction,
            categoryName = CategoryName.CONSTRUCTION_EARTH
        ),
        Category(
            nameResId = R.string.food,
            iconResId = R.drawable.ic_food,
            categoryName = CategoryName.FOOD_MACHINERY
        ),
        Category(
            nameResId = R.string.metal,
            iconResId = R.drawable.ic_settings,
            categoryName = CategoryName.METAL_WORKING
        ),
        Category(
            nameResId = R.string.more,
            iconResId = R.drawable.ic_factory,
            categoryName = CategoryName.MORE_INDUSTRIAL_CATEGORIES
        ),
        Category(
            nameResId = R.string.real_estate,
            iconResId = R.drawable.ic_real_estate,
            categoryName = CategoryName.REAL_ESTATE
        ),
        Category(
            nameResId = R.string.retail_office,
            iconResId = R.drawable.ic_office,
            categoryName = CategoryName.RETAIL_OFFICE
        ),
        Category(
            nameResId = R.string.transport_logistics,
            iconResId = R.drawable.ic_transport,
            categoryName = CategoryName.TRANSPORT_LOGISTICS
        ),
        Category(
            nameResId = R.string.woodworking,
            iconResId = R.drawable.ic_wood,
            categoryName = CategoryName.WOODWORKING
        )
    )

    fun getList() : List<Category>{
        return allCategories
    }
}