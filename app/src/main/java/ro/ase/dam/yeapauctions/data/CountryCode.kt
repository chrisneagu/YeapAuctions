package ro.ase.dam.yeapauctions.data

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CountryCode(
    @StringRes val countryNameResId: Int,
    @DrawableRes val flagResId: Int,
    @StringRes val countryCode: Int
)
