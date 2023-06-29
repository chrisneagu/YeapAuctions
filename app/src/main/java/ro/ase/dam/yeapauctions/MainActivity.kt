package ro.ase.dam.yeapauctions

import KtorHttpClient
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.json.JSONObject
import ro.ase.dam.yeapauctions.classes.Payment
import ro.ase.dam.yeapauctions.data.AppSettings
import ro.ase.dam.yeapauctions.data.AppSettingsSerializer
import ro.ase.dam.yeapauctions.data.Datasource
import ro.ase.dam.yeapauctions.data.Language
import ro.ase.dam.yeapauctions.ktor.SocketHandler
import ro.ase.dam.yeapauctions.ui.HomeScreen
import ro.ase.dam.yeapauctions.ui.LoginViewModel
import ro.ase.dam.yeapauctions.ui.PaymentsContent
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)


enum class YeapAuctionsScreen(@StringRes val title: Int) {
    Start(title = R.string.body_title_login),  //login
    SignUp(title = R.string.message_sign_up),
    ForgotPassword(title = R.string.message_forgot_password)
}

class MainActivity : ComponentActivity() {

    private lateinit var viewModel : LoginViewModel
    lateinit var paymentSheet : PaymentSheet
    lateinit var customerConfig : PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret : String
    lateinit var payment : Payment

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        Datasource.initializeSocket()
        Datasource.clientSocket.on("offersRefresh"){
            runOnUiThread {
                lifecycleScope.launch {
                    val job1 = async { Datasource.loadOffers() }
                    val job2 = async { Datasource.loadLots() }
                    job1.await()
                    job2.await()
                }
            }
        }
        Datasource.clientSocket.on("linksRefresh"){
            runOnUiThread {
                lifecycleScope.launch {
                    val job1 = async { Datasource.loadLinks() }
                    job1.await()
                }
            }
        }
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        lifecycleScope.launch {
            val job1 = async { Datasource.loadLots() }
            val job2 = async { Datasource.loadAuctions() }
            val job3 = async { Datasource.loadAddresses() }
            val job4 = async { Datasource.loadDescriptions() }
            val job5 = async { Datasource.loadOffers()}
            val job6 = async { Datasource.loadLinks() }
            job1.await()
            job2.await()
            job3.await()
            job4.await()
            job5.await()
            job6.await()
        }
        setContent {
            val appSettings = dataStore.data.collectAsState(
                initial = AppSettings()
            ).value

            YeapAuctionsTheme(useDarkTheme = appSettings.useDarkTheme) {

                val scope = rememberCoroutineScope()
                val navController: NavHostController = rememberNavController()
                val uiState by viewModel.uiState.collectAsState()
                if (appSettings.userId != null) {
                    HomeScreen(
                        appSettings.userId.toString(),
                        {
                            payment = it
                           scope.launch {
                               getDetails()
                           }
                        }
                    ) { userId ->
                        scope.launch {
                            setUserId(userId)
                        }
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = YeapAuctionsScreen.Start.name
                    ) {
                        composable(route = YeapAuctionsScreen.Start.name) {
                            LoginScreen(
                                onSignUpClicked = { navController.navigate(YeapAuctionsScreen.SignUp.name) },
                                onForgotPasswordClicked = {
                                    navController.navigate(
                                        YeapAuctionsScreen.ForgotPassword.name
                                    )
                                },
                                email = uiState.loginEmail,
                                onEmailChanged = { viewModel.setLoginEmail(it) },
                                password = uiState.loginPassword,
                                onPasswordChanged = { viewModel.setLoginPassword(it) },
                                onSuccesLoggedIn = { newUserId: ObjectId? ->
                                    scope.launch {
                                        setUserId(newUserId)
                                        val appSettings2 = dataStore.data.first()
                                        Log.d("userId", appSettings2.userId.toString())
                                    }
                                }
                            )
                        }
                        composable(route = YeapAuctionsScreen.ForgotPassword.name) {

                            ForgotPasswordScreen(
                                email = uiState.forgotpasswordEmail,
                                onEmailChanged = { viewModel.setForgotpasswordEmail(it) },
                                onBackClicked = {
                                    navController.popBackStack(
                                        YeapAuctionsScreen.Start.name,
                                        inclusive = false
                                    )
                                }
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
                                onNameChanged = { viewModel.setName(it) },
                                onLastNameChanged = { viewModel.setLastName(it) },
                                onEmailChanged = { viewModel.setSignUpEmail(it) },
                                onPasswordChanged = { viewModel.setSignUpPassword(it) },
                                onPhoneChanged = { viewModel.setPhone(it) },
                                onConfirmedPasswordChanged = { viewModel.setConfirmedPassword(it) },
                                onBackClicked = {
                                    navController.popBackStack(
                                        YeapAuctionsScreen.Start.name,
                                        inclusive = false
                                    )
                                },
                                onUserRegistered = { newUserId: ObjectId ->
                                    scope.launch {
                                        setUserId(newUserId)
                                        val appSettings2 = dataStore.data.first()
                                        Log.d("userId", appSettings2.userId.toString())
                                    }
                                },
                                isCompany = uiState.isCompany,
                                onIsCompanyChanged = { viewModel.setIsCompany(it) },
                                companyName = uiState.companyName,
                                onCompanyNameChanged = { viewModel.setCompanyName(it)},
                                gender = uiState.gender,
                                onGenderChanged = { viewModel.setGender(it)},
                                industry = uiState.industry,
                                onIndustryChanged = {viewModel.setIndustry(it)},
                                vatCode = uiState.vatCode,
                                onVatCodeChanged = {viewModel.setVatCode(it)}

                            )
                        }
                    }
                }
            }

        }
    }

    suspend fun getDetails(){
        try{
            val response: HttpResponse = KtorHttpClient.post("/api/payment-sheet"){
                contentType(ContentType.Application.Json)
                setBody("{\n" +
                        "    \"amount\": \"${(payment.grandTotal * 100).toInt()}\"\n" +
                        "}")
            }
            val content = response.body<String>()
            val result = JSONObject(content)
            customerConfig = PaymentSheet.CustomerConfiguration(
                result.getString("customer"),
                result.getString("ephemeralKey")
            )
            paymentIntentClientSecret = result.getString("paymentIntent")
            PaymentConfiguration.init(this, result.getString("publishableKey"))
            runOnUiThread {
                showStripePaymentSheet()
            }
        }catch (e: Exception){
            Log.d("Payment-sheet", "Payment: " + e.message)
        }
    }

    fun showStripePaymentSheet(){
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "RBW Auctions",
                customer = customerConfig,
                allowsDelayedPaymentMethods = false
            )
        )
        Log.d("Intent-Client-Secret", paymentIntentClientSecret)
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("Payment-Result", "Canceled")
            }
            is PaymentSheetResult.Failed -> {
                Log.d("Payment-Result", "Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                Log.d("Payment-Result", "Completed")
                runOnUiThread {
                    lifecycleScope.launch {
                        try{
                            payment.paid = true
                            KtorHttpClient.put("/api/payments/${payment.id}/"){
                                contentType(ContentType.Application.Json)
                                setBody(payment)
                            }
                            Datasource.clientSocket.emit("paymentsUpdate")
                        }catch(e: Exception){
                            Log.d("Update - Payment", "Error while updating the state of the payment" + e.message)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setUserId(userId: ObjectId?){
        dataStore.updateData {
            it.copy(
                userId = userId
            )
        }
    }

    private suspend fun setLanguage(language: Language){
        dataStore.updateData {
            it.copy(
                language = language
            )
        }
    }
}



