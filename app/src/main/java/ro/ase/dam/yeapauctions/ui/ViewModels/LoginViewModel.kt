package ro.ase.dam.yeapauctions.ui

import android.os.Parcelable
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoginUiState(
    val name: String = "",
    val last_name: String = "",
    val loginEmail: String = "",
    val forgotpasswordEmail: String = "",
    val signupEmail: String = "",
    val loginPassword: String = "",
    val signupPassword: String = "",
    val confirmed_password: String = "",
    val phone: String = "",
    val isCompany: Boolean = true,
    val companyName: String = "",
    val vatCode: String = "",
    val industry: String = "",
    val gender: Boolean = false,
): Parcelable

class LoginViewModel( private val savedStateHandle: SavedStateHandle) : ViewModel() { //primim Saved State Handle ca sa pastram ViewModelul si dupa distrugerea aplicatiei
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {

    }


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

    fun setIsCompany(isCompany: Boolean){
        _uiState.update { currentState ->
            currentState.copy(
                isCompany = isCompany
            )
        }
    }

    fun setGender(gender: Boolean){
        _uiState.update { currentState ->
            currentState.copy(
                gender = gender
            )
        }
    }

    fun setCompanyName(companyName: String){
        _uiState.update { currentState ->
            currentState.copy(
                companyName = companyName
            )
        }
    }

    fun setVatCode(vatCode: String){
        _uiState.update { currentState ->
            currentState.copy(
                vatCode = vatCode
            )
        }
    }

    fun setIndustry(industry: String){
        _uiState.update { currentState ->
            currentState.copy(
                industry = industry
            )
        }
    }
}
