package ro.ase.dam.yeapauctions


import KtorHttpClient
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Token
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.components.EditTextFieldPassword
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme
import java.util.*


@Composable
fun ForgotPasswordScreen(modifier: Modifier = Modifier,
                         email: String,
                         onEmailChanged: (String) -> Unit,
                         onBackClicked: () -> Unit
){

    val focusManager = LocalFocusManager.current
    var sent by rememberSaveable { mutableStateOf(false) }
    var inputToken by rememberSaveable { mutableStateOf("")}
    var newPassword by rememberSaveable { mutableStateOf("")}
    var confirmedPassword by rememberSaveable { mutableStateOf("")}

    Column(modifier = Modifier.fillMaxSize()){
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {


            val back = Icons.Filled.ArrowBack

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                contentAlignment = Alignment.CenterStart){

                IconButton(onClick = {
                    onBackClicked()
                    sent= false
                    }
                ){
                    Icon(imageVector  = back, "Back", tint = MaterialTheme.colorScheme.tertiary)
                }

            }


            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd){
                Image(
                    painter = painterResource(id = R.drawable.background_element),
                    contentDescription = "null",
                    modifier = Modifier.size(64.dp)

                )
            }

        }

        Column(   modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start){

            Text(
                text = stringResource(id = R.string.message_forgot_password),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ElevatedCard(
                shape = MaterialTheme.shapes.small
            ) {
                    if(sent){
                        Column(verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.Start) {
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
                                modifier = modifier.padding(8.dp),
                                leadingicon = Icons.Outlined.Email
                            )

                            EditTextField(
                                label = R.string.token_label,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                value = inputToken,
                                onValueChange = { inputToken = it },
                                textStyle = MaterialTheme.typography.headlineMedium,
                                modifier = modifier.padding(8.dp),
                                leadingicon = Icons.Outlined.Token
                            )

                            EditTextFieldPassword(
                                label = R.string.new_password_label,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                value = newPassword,
                                onValueChange = { newPassword = it},
                                textStyle = MaterialTheme.typography.headlineMedium,
                                modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                            )
                            EditTextFieldPassword(
                                label = R.string.confirm_password_label,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                value = confirmedPassword,
                                onValueChange = { confirmedPassword = it},
                                textStyle = MaterialTheme.typography.headlineMedium,
                                modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                    else{
                        Box(contentAlignment = Alignment.TopCenter)
                        {
                            EditTextField(
                                label = R.string.user_label,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                value = email,
                                onValueChange = onEmailChanged,
                                textStyle = MaterialTheme.typography.headlineMedium,
                                modifier = modifier.padding(8.dp),
                                leadingicon = Icons.Outlined.Email
                            )
                        }
                    }
            }
            val coroutineScope = rememberCoroutineScope()
            val token = UUID.randomUUID().toString().replace("-", "").substring(0,10)
            if(!sent){
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response: HttpResponse = KtorHttpClient.post("/api/sendResetToken"){
                                    contentType(ContentType.Application.Json)
                                    setBody("{\n" +
                                            "    \"email\":\"$email\",\n" +
                                            "    \"token\":\"$token\"\n" +
                                            "}")
                                }
                                if(response.status.value == 200)
                                    sent = true
                            }catch (e: Exception) {
                                Log.e("sendEmail", "Failed to send email: ${e.message}", e)
                            }
                        }
                              },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            stringResource(R.string.submit_message),
                            style = MaterialTheme.typography.labelMedium,
                        )

                    }
                }
            }
            else{
                OutlinedButton(
                    onClick = { sent = true },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            stringResource(R.string.reset_message),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    YeapAuctionsTheme {
        //ForgotPasswordScreen()
    }
}