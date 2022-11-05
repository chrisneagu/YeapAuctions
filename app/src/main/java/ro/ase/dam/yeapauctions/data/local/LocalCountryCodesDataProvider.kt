package ro.ase.dam.yeapauctions.data.local

import androidx.compose.ui.res.stringResource
import ro.ase.dam.yeapauctions.R
import ro.ase.dam.yeapauctions.data.CountryCode

object LocalCountryCodesDataProvider {
    private val allCountryContactCodes = listOf(
        CountryCode(
            R.string.Romania,
            R.drawable.ro
        ),
        CountryCode(
            R.string.UnitedKingdom,
            R.drawable.gb
        ),
        CountryCode(
            R.string.France,
            R.drawable.fr
        ),
        CountryCode(
            R.string.Spain,
            R.drawable.es
        )
    )

  fun getList() : List<CountryCode>{
      return allCountryContactCodes
  }

}