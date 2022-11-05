package ro.ase.dam.yeapauctions.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CountryCode (
    @StringRes val code: Int,
    @DrawableRes val flag: Int
    )

