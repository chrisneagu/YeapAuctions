package ro.ase.dam.yeapauctions


import KtorHttpClient
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import ro.ase.dam.yeapauctions.classes.Address
import ro.ase.dam.yeapauctions.classes.User
import ro.ase.dam.yeapauctions.data.CountryCode
import ro.ase.dam.yeapauctions.data.local.LocalCountryCodesDataProvider
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.components.EditTextFieldPassword
import java.util.*


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    email: String,
    phone: String,
    password: String,
    confirmed_password: String,
    name: String,
    last_name: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onConfirmedPasswordChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
    onUserRegistered: (ObjectId) -> Unit,
    isCompany: Boolean,
    onIsCompanyChanged: (Boolean) -> Unit,
    companyName: String,
    onCompanyNameChanged: (String) -> Unit,
    gender: Boolean,
    onGenderChanged: (Boolean) -> Unit,
    industry: String,
    onIndustryChanged: (String) -> Unit,
    vatCode: String,
    onVatCodeChanged: (String) -> Unit,
) {
    var selectedCountry by remember { mutableStateOf(CountryCode(R.string.Romania, R.drawable.ro, R.string.codeRomania)) }
    val localContext = LocalContext.current
    var errorCode by remember { mutableStateOf(0) }
    var stage by remember {mutableStateOf(1)}
    var city by remember {mutableStateOf("")}
    var country by remember {mutableStateOf("")}
    var zipCode by remember {mutableStateOf("")}
    var street by remember {mutableStateOf("")}
    var streetNumber by remember {mutableStateOf("")}


    when(errorCode) {
        1 -> Toast.makeText(localContext,stringResource(R.string.error_code1_message), Toast.LENGTH_LONG).show()
        2 -> Toast.makeText(localContext,stringResource(R.string.error_code2_message), Toast.LENGTH_LONG).show()
        3 -> Toast.makeText(localContext,stringResource(R.string.error_code13_message), Toast.LENGTH_LONG).show()
        4 -> Toast.makeText(localContext,stringResource(R.string.error_code14_message), Toast.LENGTH_LONG).show()
        5 -> Toast.makeText(localContext,stringResource(R.string.error_code3_message), Toast.LENGTH_LONG).show()
        6 -> Toast.makeText(localContext,stringResource(R.string.error_code4_message), Toast.LENGTH_LONG).show()
        7 -> Toast.makeText(localContext,stringResource(R.string.error_code5_message), Toast.LENGTH_LONG).show()
        8 -> Toast.makeText(localContext,stringResource(R.string.error_code6_message), Toast.LENGTH_LONG).show()
        9 -> Toast.makeText(localContext,stringResource(R.string.error_code7_message), Toast.LENGTH_LONG).show()
        10 -> Toast.makeText(localContext,stringResource(R.string.error_code8_message), Toast.LENGTH_LONG).show()
        11 -> Toast.makeText(localContext,stringResource(R.string.error_code9_message), Toast.LENGTH_LONG).show()
        12 -> Toast.makeText(localContext,stringResource(R.string.error_code12_message), Toast.LENGTH_LONG).show()
        13 -> Toast.makeText(localContext,stringResource(R.string.error_code10_message), Toast.LENGTH_LONG).show()
        14 -> Toast.makeText(localContext,stringResource(R.string.error_code11_message), Toast.LENGTH_LONG).show()
        15 -> Toast.makeText(localContext,stringResource(R.string.error_code15_message), Toast.LENGTH_LONG).show()
        16 -> Toast.makeText(localContext,stringResource(R.string.error_code16_message), Toast.LENGTH_LONG).show()
        17 -> Toast.makeText(localContext,stringResource(R.string.error_code17_message), Toast.LENGTH_LONG).show()
        18 -> Toast.makeText(localContext,stringResource(R.string.error_code18_message), Toast.LENGTH_LONG).show()
        19 -> Toast.makeText(localContext,stringResource(R.string.error_code19_message), Toast.LENGTH_LONG).show()
        20 -> Toast.makeText(localContext,stringResource(R.string.error_code20_message), Toast.LENGTH_LONG).show()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {

                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        "Back",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

            }


            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_element),
                    contentDescription = "null",
                    modifier = Modifier.size(64.dp)

                )
            }
        }

        Column(   modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start){

            Text(
                text = stringResource(id = R.string.message_sign_up),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ElevatedCard(
                shape = MaterialTheme.shapes.small
            ) {
                Column(verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start) {
                    when (stage) {
                        1 -> newAccount(modifier= modifier, email, password, confirmed_password, onEmailChanged, onPasswordChanged,
                            onConfirmedPasswordChanged, onBackClicked = onBackClicked,
                            onNextClicked = { stage++ }, onErrorModify = { errorCode = it })
                        2 -> personalDetails(
                            name = name,
                            last_name = last_name,
                            onNameChanged = onNameChanged,
                            onLastNameChanged = onLastNameChanged,
                            onBackClicked = { stage-- },
                            onNextClicked = { stage++ },
                            isCompany = isCompany,
                            onIsCompanyChanged = onIsCompanyChanged,
                            companyName = companyName,
                            onCompanyNameChanged = onCompanyNameChanged,
                            gender = gender,
                            onGenderChanged = onGenderChanged,
                            industry = industry,
                            onIndustryChanged = onIndustryChanged,
                            vatCode = vatCode,
                            onVatCodeChanged = onVatCodeChanged,
                            onErrorModify = { errorCode = it }
                            )
                        3 -> phoneVerification(
                            modifier = modifier,
                            context = localContext,
                            selectedCountry = selectedCountry,
                            onCountrySelected = { country ->
                                selectedCountry = country
                            },
                            phone = phone,
                            onPhoneChanged = onPhoneChanged,
                            onBackClicked = { stage-- },
                            onNextClicked = { stage++ },
                            onErrorModify = { errorCode = it })
                        4 -> submitAdress(
                            modifier = modifier, name, last_name, email, stringResource(id = selectedCountry.countryCode) + phone, password, city, { city = it},
                            country, {country = it},street,
                            {street = it},zipCode, {zipCode = it},
                            streetNumber, {streetNumber = it}, {stage--},
                            onUserRegistered = onUserRegistered,
                            gender = gender,
                            isCompany = isCompany,
                            companyName = companyName,
                            vatCode = vatCode,
                            industry = industry,
                            onErrorModify = { errorCode = it }
                        )
                    }
                }
            }
        }


    }
}

@Composable
fun myRow(title: String, onBackClicked: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {

            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    "Back",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

        }

        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp))
        }


    }
}

suspend fun newAccountFieldsVerification(
    email: String,
    password: String,
    confirmed_password: String,
    onErrorModify: (Int) -> Unit
) :Boolean{
    if(email.isEmpty()){
        onErrorModify(7)
        return false
    }
    else if (!Regex("""[A-Za-z]{1,}[a-zA-Z0-9_.-]+@[a-zA-Z]+\.[a-zA-Z]{1,}""").containsMatchIn(email)) {
        onErrorModify(8)
        return false
    }else{
        try{
            val response: HttpResponse = KtorHttpClient.post("/api/users/check-existence"){
                contentType(ContentType.Application.Json)
                setBody("{\n" +
                        "    \"email\": \"$email\"\n" +
                        "}")
            }
            if(!response.status.isSuccess()){
                onErrorModify(12)
                return false;
            }
        }catch(e: Exception){
            Log.e("emailCheck", "Failed to check email db: ${e.message}", e)
            onErrorModify(12)
            return false;
        }
    }
    if(password.isEmpty()){
        onErrorModify(9)
        return false
    }
    else if (!Regex( """(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@${'$'}!%*?&])[A-Za-z\d@${'$'}!%*?&]{8,}""").containsMatchIn(password)) {
        onErrorModify(10)
        return false
    }
    if(password != confirmed_password){
        onErrorModify(11)
        return false
    }
    onErrorModify(0)
    return true;
}

@Composable
fun newAccount(modifier: Modifier = Modifier,
               email: String,
               password: String,
               confirmed_password: String,
               onEmailChanged: (String) -> Unit,
               onPasswordChanged: (String) -> Unit,
               onConfirmedPasswordChanged: (String) -> Unit,
               onBackClicked: () -> Unit,
               onNextClicked: () -> Unit,
               onErrorModify: (Int) -> Unit){
    val focusManager = LocalFocusManager.current
    var canContinue by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    myRow(title = stringResource(id = R.string.new_account), onBackClicked = onBackClicked)
    EditTextField(
        label = R.string.user_label,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        value = email,
        onValueChange = onEmailChanged,
        textStyle = MaterialTheme.typography.headlineMedium,
        modifier = modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        leadingicon = Icons.Outlined.Email
    )
    EditTextFieldPassword(
        label = R.string.password_label,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        value = password,
        onValueChange = onPasswordChanged,
        textStyle = MaterialTheme.typography.headlineMedium,
        modifier = modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
    )

    EditTextFieldPassword(
        label = R.string.password_label_confirm,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        value = confirmed_password,
        onValueChange = onConfirmedPasswordChanged,
        textStyle = MaterialTheme.typography.headlineMedium,
        modifier = modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
    )

    OutlinedButton(
        onClick = {
            coroutineScope.launch {
                canContinue  = newAccountFieldsVerification(email,password,confirmed_password,onErrorModify)
                if(canContinue)
                    onNextClicked()
            }
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                stringResource(R.string.to_personal_message),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }

}

fun personalDetailsVerification(
    isCompany: Boolean,
    name: String,
    last_name: String,
    companyName: String,
    vatCode: String,
    onErrorModify: (Int) -> Unit
): Boolean{
    if(name.isEmpty()){
        onErrorModify(1)
        return false
    }
    if(last_name.isEmpty()){
        onErrorModify(2)
        return false
    }
    if(isCompany){
        if(companyName.isEmpty()){
            onErrorModify(3)
            return false
        }
        if(vatCode.isEmpty()){
            onErrorModify(4)
            return false
        }
    }
    onErrorModify(0)
    return true
}

@Composable
fun personalDetails(modifier: Modifier = Modifier,
                    name: String,
                    last_name: String,
                    onNameChanged: (String) -> Unit,
                    onLastNameChanged : (String) -> Unit,
                    onBackClicked: () -> Unit,
                    onNextClicked: () -> Unit,
                    isCompany: Boolean,
                    onIsCompanyChanged: (Boolean) -> Unit,
                    companyName: String,
                    onCompanyNameChanged: (String) -> Unit,
                    gender: Boolean,
                    onGenderChanged: (Boolean) -> Unit,
                    industry: String,
                    onIndustryChanged: (String) -> Unit,
                    vatCode: String,
                    onVatCodeChanged: (String) -> Unit,
                    onErrorModify: (Int) -> Unit
){
    val focusManager = LocalFocusManager.current
    var canContinue by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    myRow(title = stringResource(id = R.string.personal_details), onBackClicked = onBackClicked)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            stringResource(id = R.string.account_type),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
            Checkbox(
                checked = isCompany,
                onCheckedChange = {isChecked ->
                    if (isChecked) {
                        onIsCompanyChanged(true)
                    }
                }
            )
            Text(
                stringResource(id = R.string.company_account),
                style = MaterialTheme.typography.labelSmall
            )
            Checkbox(
                checked = !isCompany,
                onCheckedChange = {isChecked ->
                    if (isChecked) {
                        onIsCompanyChanged(false)
                    }
                }
            )
            Text(
                stringResource(id = R.string.private_account),
                style = MaterialTheme.typography.labelSmall
            )
        }

        if(isCompany){
            EditTextField(
                label = R.string.company_name,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                ),
                value = companyName,
                onValueChange = onCompanyNameChanged,
                textStyle = MaterialTheme.typography.headlineMedium,
                leadingicon = Icons.Outlined.Work,
                modifier = modifier.padding(start = 8.dp, end = 16.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            Checkbox(
                checked = !gender,
                onCheckedChange = {isChecked ->
                    if (isChecked) {
                        onGenderChanged(false)
                    }
                }
            )
            Text(
                stringResource(id = R.string.mr),
                style = MaterialTheme.typography.labelSmall
            )
            Checkbox(
                checked = gender,
                onCheckedChange = {isChecked ->
                    if (isChecked) {
                        onGenderChanged(true)
                    }
                }
            )
            Text(
                stringResource(id = R.string.ms),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 16.dp, end = 16.dp)
    ) {
        EditTextField(
            label = R.string.person_name,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            value = name,
            onValueChange = onNameChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.Person,
            modifier = modifier
                .weight(1f)
                .padding(end = 6.dp),
        )

        EditTextField(
            label = R.string.person_last_name,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            value = last_name,
            onValueChange = onLastNameChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.Person,
            modifier = modifier
                .weight(1f)
                .padding(start = 6.dp),
        )
    }

    if(isCompany){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp)
        ) {
            EditTextField(
                label = R.string.industry,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                value = industry,
                onValueChange = onIndustryChanged,
                textStyle = MaterialTheme.typography.headlineMedium,
                leadingicon = Icons.Outlined.Work,
                modifier = modifier
                    .weight(1f)
                    .padding(end = 6.dp),
            )

            EditTextField(
                label = R.string.vat_code,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                value = vatCode,
                onValueChange = onVatCodeChanged,
                textStyle = MaterialTheme.typography.headlineMedium,
                leadingicon = ImageVector.vectorResource(id = R.drawable.percent),
                modifier = modifier
                    .weight(1f)
                    .padding(start = 6.dp),
            )
        }
    }

    OutlinedButton(
        onClick = {
            coroutineScope.launch {
                canContinue  = personalDetailsVerification(isCompany,name,last_name, companyName, vatCode, onErrorModify)
                if(canContinue)
                    onNextClicked()
            }
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                stringResource(R.string.to_phone_verification),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }

}

fun phoneFieldsVerification(
    phone: String,
    otpCode: String,
    receivedCode: String,
    onErrorModify: (Int) -> Unit
):Boolean{

    if(phone.isEmpty()){
        onErrorModify(5)
        return false
    }else if (!Regex("""\d{9}""").containsMatchIn(phone)) {
        onErrorModify(6)
        return false
    }

    if(receivedCode.isEmpty()){
        onErrorModify(13)
        return false
    }else if(receivedCode != otpCode){
        Log.d("Match" , "$receivedCode $otpCode")
        onErrorModify(14)
        return false
    }
    onErrorModify(0)
    return true
}


@Composable
fun phoneVerification(modifier: Modifier = Modifier,
                      context: Context,
                      selectedCountry: CountryCode?,
                      onCountrySelected: (CountryCode) -> Unit,
                      phone: String,
                      onPhoneChanged: (String) -> Unit,
                      onBackClicked: () -> Unit,
                      onNextClicked: () -> Unit,
                      onErrorModify: (Int) -> Unit){

    var sent by rememberSaveable { mutableStateOf(false) }
    var phoneInUse by rememberSaveable { mutableStateOf(false) }
    var receivedCode by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var canContinue by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("")}
    myRow(title = stringResource(id = R.string.new_account), onBackClicked = onBackClicked)
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            stringResource(R.string.attention_message),
            style = MaterialTheme.typography.labelMedium,
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        countryDropdown(selectedCountry = selectedCountry, context = context, onCountrySelected = onCountrySelected)
        EditTextField(
            label = R.string.phone,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            value = phone,
            onValueChange = onPhoneChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.Phone,
            modifier = modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        )
    }
    val coroutineScope = rememberCoroutineScope()


    if(!sent){
        var PhoneNumber : String = selectedCountry?.let { stringResource(id = it.countryCode) } + phone
        OutlinedButton(
            onClick = {
                if(phone.isEmpty()){
                    onErrorModify(5)
                }else if (!Regex("""\d{9}""").containsMatchIn(phone)) {
                    onErrorModify(6)
                }
                else{
                    coroutineScope.launch {
                        try{
                            val response: HttpResponse = KtorHttpClient.post("/api/users/check-existence"){
                                contentType(ContentType.Application.Json)
                                setBody("{\n" +
                                        "    \"phoneNumber\": \"$PhoneNumber\"\n" +
                                        "}")
                            }
                            if(!response.status.isSuccess()){
                                onErrorModify(15)
                                phoneInUse = true
                            }
                        }catch(e: Exception){
                            Log.e("phoneCheck", "Failed to check phone db: ${e.message}", e)
                            onErrorModify(15)
                            phoneInUse = true
                        }

                        if(!phoneInUse){
                            try{
                                otpCode = String.format("%06d", Random().nextInt(999999))
                                val response: HttpResponse = KtorHttpClient.post("/api/sendOTP"){
                                    contentType(ContentType.Application.Json)
                                    setBody("{\n" +
                                            "    \"phoneNumber\": \"$PhoneNumber\",\n" +
                                            "    \"otp\": \"Your otp is $otpCode\"\n" +
                                            "}")
                                }
                                if(response.status.value == 200)
                                    sent = true
                            }catch (e: Exception) {
                                Log.e("sendSms", "Failed to send message: ${e.message}" + PhoneNumber, e)
                            }
                        }
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    stringResource(R.string.send_code),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
    else{
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier.padding(8.dp)
        ) {
            if (selectedCountry != null) {
                Text(
                    text = stringResource(id = R.string.message_sent) + stringResource(id = selectedCountry.countryCode) + phone,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        EditTextField(
            label = R.string.sms_code,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            value = receivedCode,
            onValueChange = { receivedCode = it },
            textStyle = MaterialTheme.typography.headlineMedium,
            modifier = modifier.padding(8.dp),
            leadingicon = Icons.Outlined.Send
        )
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    canContinue  = phoneFieldsVerification(phone, otpCode, receivedCode, onErrorModify)
                    if(canContinue)
                        onNextClicked()
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    stringResource(R.string.to_adress),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun countryDropdown(selectedCountry: CountryCode?, context: Context, onCountrySelected: (CountryCode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val countryList = LocalCountryCodesDataProvider.getList()
    Box(modifier = Modifier.clickable(onClick = { expanded = true })
        , contentAlignment = Alignment.CenterStart) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            selectedCountry?.let { country ->
                Image(
                    painter = painterResource(id = country.flagResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = country.countryCode))
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_drop_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize()
        ) {
            countryList.forEach { country ->
                DropdownMenuItem(onClick = {
                    onCountrySelected(country)
                    expanded = false
                }, text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = country.flagResId),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = country.countryCode))
                    }
                })
            }
        }
    }
}


fun AddressFieldsVerification(
    city: String,
    country: String,
    street: String,
    zipCode: String,
    number: String,
    onErrorModify: (Int) -> Unit
): Boolean{
    if(city.isEmpty()){
        onErrorModify(16)
        return false
    }
    if(country.isEmpty()){
        onErrorModify(17)
        return false
    }
    if(street.isEmpty()){
        onErrorModify(18)
        return false
    }
    if(zipCode.isEmpty()){
        onErrorModify(19)
        return false
    }
    if(number.isEmpty()){
        onErrorModify(20)
        return false
    }
    onErrorModify(0)
    return true
}

@Composable
fun submitAdress(modifier: Modifier = Modifier,
                 firstName: String,
                 lastName: String,
                 email: String,
                 phoneNumber: String,
                 password: String,
                 city: String,
                 onCityChanged: (String) -> Unit,
                 country: String,
                 onCountryChanged: (String) -> Unit,
                 street: String,
                 onStreetChanged: (String) -> Unit,
                 zipCode: String,
                 onzipCodeChanged: (String) -> Unit,
                 number: String,
                 onNumberChanged: (String) -> Unit,
                 onBackClicked: () -> Unit,
                 onUserRegistered: (ObjectId) -> Unit,
                 gender: Boolean,
                 isCompany: Boolean,
                 companyName: String,
                 vatCode: String,
                 industry: String,
                 onErrorModify: (Int) -> Unit){
    val focusManager = LocalFocusManager.current
    var canContinue by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    myRow(title = stringResource(id = R.string.personal_details), onBackClicked = onBackClicked)
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            stringResource(R.string.adress),
            style = MaterialTheme.typography.labelMedium,
        )
    }

    Row(){
        EditTextField(
            label = R.string.city,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Right) }
            ),
            value = city,
            onValueChange = onCityChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.LocationCity,
            modifier = modifier
                .padding(4.dp)
                .weight(1f),
        )

        EditTextField(
            label = R.string.country,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            value = country,
            onValueChange = onCountryChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.Flag,
            modifier = modifier
                .padding(4.dp)
                .weight(1f),
        )
    }

    EditTextField(
        label = R.string.street,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        value = street,
        onValueChange = onStreetChanged,
        textStyle = MaterialTheme.typography.headlineMedium,
        leadingicon = Icons.Outlined.Streetview,
        modifier = modifier
            .padding(4.dp)
    )

    Row(){
        EditTextField(
            label = R.string.zipCode,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Right) }
            ),
            value = zipCode,
            onValueChange = onzipCodeChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.LocalPostOffice,
            modifier = modifier
                .padding(4.dp)
                .weight(1f),
        )

        EditTextField(
            label = R.string.number,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            value = number,
            onValueChange = onNumberChanged,
            textStyle = MaterialTheme.typography.headlineMedium,
            leadingicon = Icons.Outlined.Flag,
            modifier = modifier
                .padding(4.dp)
                .weight(1f),
        )
    }

    OutlinedButton(
        onClick = {
           canContinue  = AddressFieldsVerification(city,country,street,zipCode,number,onErrorModify)
            if(canContinue){
                coroutineScope.launch {
                    val address = Address(
                        city = city,
                        country = country,
                        zipCode = zipCode,
                        street = street,
                        number = number
                    )
                    val user = User(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phoneNumber = phoneNumber,
                        password = password,
                        addressId = address.id,
                        gender = gender,
                        isCompany = isCompany,
                        companyName = if (isCompany) companyName else null,
                        vatCode = if (isCompany) vatCode else null,
                        industry = if (isCompany) industry else null,
                    )
                    val registered : Boolean = registerUser(user, address)
                    Log.d("Inregistrat?", registered.toString())
                    if(registered){
                        onUserRegistered(user.id)
                    }
                }
            }
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                stringResource(R.string.finish),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

suspend fun registerUser(user: User,
                         address: Address
) : Boolean {
    try{
        val responseAddress: HttpResponse = KtorHttpClient.post("/api/newAddress"){
            contentType(ContentType.Application.Json)
            setBody(address)
        }
        val responseUser: HttpResponse = KtorHttpClient.post("/api/newUser"){
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return responseAddress.status.isSuccess() && responseUser.status.isSuccess()
    }catch (e: Exception) {
        Log.e("registerUser", "Failed to register user in DataBase: ${e.message}", e)
    }
    return false
}