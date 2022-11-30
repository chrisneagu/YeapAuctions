package ro.ase.dam.yeapauctions.ui


import android.os.Bundle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.flow.*


data class LoginUiState(
    val name: String = "",
    val last_name: String = "",
    val loginEmail: String = "",
    val forgotpasswordEmail: String = "",
    val signupEmail: String = "",
    val loginPassword: String = "",
    val signupPassword: String = "",
    val confirmed_password: String = "",
    val phone: String = ""

)

class LoginViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() { //primim Saved State Handle ca sa pastram ViewModelul si dupa distrugerea aplicatiei
    private var _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()



    fun setLastName(lastName: String){
        _uiState.update { currentState ->
            currentState.copy(
                last_name = lastName
            )
        }

    }

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
        savedStateHandle["userEmail"] =  email
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
