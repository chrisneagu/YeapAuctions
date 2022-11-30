package ro.ase.dam.yeapauctions

import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ro.ase.dam.yeapauctions.ui.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.lifecycle.ViewModelProvider
import ro.ase.dam.yeapauctions.ui.utils.AuctionContentType
import ro.ase.dam.yeapauctions.ui.utils.AuctionNavigationType


enum class YeapAuctionsScreen(@StringRes val title: Int) {
    Start(title = R.string.body_title_login),  //login
    SignUp(title = R.string.message_sign_up),
    ForgotPassword(title = R.string.message_forgot_password)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeapAuctionsApp(
    navController: NavHostController = rememberNavController(),
    windowSize: WindowWidthSizeClass,
    viewModel: LoginViewModel //= viewModel()
) {

        val navigationType: AuctionNavigationType
        val contentType: AuctionContentType
        val uiState by viewModel.uiState.collectAsState()


        //asta trb pusa in aia cu 4 activitati
        when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                navigationType = AuctionNavigationType.BOTTOM_NAVIGATION
                contentType = AuctionContentType.LIST_ONLY
            }
            WindowWidthSizeClass.Medium -> {
                navigationType = AuctionNavigationType.NAVIGATION_RAIL
                contentType = AuctionContentType.LIST_ONLY
            }
            WindowWidthSizeClass.Expanded -> {
                navigationType = AuctionNavigationType.PERMANENT_NAVIGATION_DRAWER
                contentType = AuctionContentType.LIST_AND_DETAIL
            }
            else -> {
                navigationType = AuctionNavigationType.BOTTOM_NAVIGATION
                contentType = AuctionContentType.LIST_ONLY
            }
        }

        NavHost(
            navController = navController,
            startDestination = YeapAuctionsScreen.Start.name
        ) {
            composable(route = YeapAuctionsScreen.Start.name) {
                LoginScreen(
                    onSignUpClicked = { navController.navigate(YeapAuctionsScreen.SignUp.name) },
                    onForgotPasswordClicked = { navController.navigate(YeapAuctionsScreen.ForgotPassword.name)},
                    email = uiState.loginEmail,
                    onEmailChanged =  { viewModel.setLoginEmail(it)},
                    password = uiState.loginPassword,
                    onPasswordChanged = { viewModel.setLoginPassword(it)}
                )
            }
            composable(route = YeapAuctionsScreen.ForgotPassword.name) {

                ForgotPasswordScreen(
                    email = uiState.forgotpasswordEmail,
                    onEmailChanged =  { viewModel.setForgotpasswordEmail(it)},
                    onBackClicked = { navController.popBackStack(YeapAuctionsScreen.Start.name, inclusive = false) }
                )
            }
            composable(route = YeapAuctionsScreen.SignUp.name) {
                SignUpScreen(
                    name = uiState.name,
                    last_name = uiState.last_name,
                    email = uiState.signupEmail,
                    phone = uiState.phone,
                    password = uiState.signupPassword,
                    confirmed_password = uiState.confirmed_password,
                    onNameChanged = {viewModel.setName(it)},
                    onLastNameChanged = {viewModel.setLastName(it)},
                    onEmailChanged = {viewModel.setSignUpEmail(it)},
                    onPasswordChanged = { viewModel.setSignUpPassword(it)},
                    onPhoneChanged = {viewModel.setPhone(it)},
                    onConfirmedPasswordChanged = { viewModel.setConfirmedPassword(it)},
                    onBackClicked = { navController.popBackStack(YeapAuctionsScreen.Start.name, inclusive = false) }

                )
            }
        }
}


