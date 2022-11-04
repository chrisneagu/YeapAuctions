package ro.ase.dam.yeapauctions.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

data class LoginUiState(
    val name: String = "",
    val loginEmail: String = "",
    val forgotpasswordEmail: String = "",
    val signupEmail: String = "",
    val loginPassword: String = "",
    val signupPassword: String = "",
    val confirmed_password: String = "",
    val phone: String = ""

)
