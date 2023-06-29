package ro.ase.dam.yeapauctions.data.local


import ro.ase.dam.yeapauctions.R
import ro.ase.dam.yeapauctions.data.CountryCode


object LocalCountryCodesDataProvider {


    private val allCountryContactCodes = listOf(
        CountryCode(
            R.string.Romania,
            R.drawable.ro,
            R.string.codeRomania
        ),
        CountryCode(
            R.string.UnitedKingdom,
            R.drawable.gb,
            R.string.codeUnitedKingdom,
        ),
        CountryCode(
            R.string.France,
            R.drawable.fr,
            R.string.codeFrance
        )
    )

  fun getList() : List<CountryCode>{
      return allCountryContactCodes
  }

}