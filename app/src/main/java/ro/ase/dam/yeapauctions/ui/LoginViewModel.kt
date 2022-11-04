package ro.ase.dam.yeapauctions.ui


import androidx.lifecycle.ViewModel
import ro.ase.dam.yeapauctions.data.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.sign


class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()





    fun setName(name: String){
        _uiState.update { currentState ->
            currentState.copy(
                name = name
            )
        }
    }

    fun setPhone(phone: String){
        _uiState.update { currentState ->
            currentState.copy(
                phone = phone
            )
        }
    }

    fun setSignUpEmail(email: String){
        _uiState.update { currentState ->
            currentState.copy(
                signupEmail = email
            )
        }
    }

    fun setLoginEmail(email: String){
        _uiState.update { currentState ->
            currentState.copy(
                loginEmail = email
            )
        }
    }

  fun setForgotpasswordEmail(email: String){
      _uiState.update { currentState ->
          currentState.copy(
              forgotpasswordEmail = email
          )
      }
  }

    fun setLoginPassword(password: String){
        _uiState.update { currentState ->
            currentState.copy(
                loginPassword = password
            )
        }
    }

    fun setSignUpPassword(password: String){
        _uiState.update { currentState ->
            currentState.copy(
                signupPassword = password
            )
        }
    }

    fun setConfirmedPassword(password: String){
        _uiState.update { currentState ->
            currentState.copy(
                confirmed_password = password
            )
        }
    }

    fun resetLogin() {
        _uiState.value = LoginUiState()
    }

}
