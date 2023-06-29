package ro.ase.dam.yeapauctions


import KtorHttpClient
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import ro.ase.dam.yeapauctions.classes.User
import ro.ase.dam.yeapauctions.classes.UserResponse
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.components.EditTextFieldPassword
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme
import kotlin.math.log

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onForgotPasswordClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    email: String,
    onEmailChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onSuccesLoggedIn: (ObjectId?) -> Unit
){

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    Column() {

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Image(
                painter = painterResource(id = R.drawable.background_element),
                contentDescription = "null",
                modifier = Modifier.size(64.dp)
            )
        }

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.body_title_login),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(id = R.string.body_h1_login),
                    style = MaterialTheme.typography.headlineSmall
                )
            }


            ElevatedCard(
                shape = MaterialTheme.shapes.medium
            ) {
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

                EditTextFieldPassword(
                    label = R.string.password_label,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    value = password,
                    onValueChange = onPasswordChanged,
                    textStyle = MaterialTheme.typography.headlineMedium,
                    modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }

            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()){
                Text( text = stringResource(id = R.string.message_forgot_password) + "?",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .clickable { onForgotPasswordClicked() },
                    color = MaterialTheme.colorScheme.tertiary,
                    )
            }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val id: ObjectId? = logIn(email, password)
                            Log.d("Loggining", id.toString())
                            onSuccesLoggedIn(id)
                        } },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            stringResource(R.string.submit),
                            style = MaterialTheme.typography.labelMedium
                        )

                    }

                }



            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = stringResource(id = R.string.message_register_login),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start
                  )

                Text(
                    text = stringResource(id = R.string.message_sign_up),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable { onSignUpClicked() }
                )
            }
        }
    }
}

suspend fun logIn(
    email: String,
    password: String
) : ObjectId? {
    try{
        val user: User = KtorHttpClient.post("/api/auth"){
            contentType(ContentType.Application.Json)
            setBody("{\n" +
                    "    \"email\": \"$email\",\n" +
                    "    \"password\": \"$password\"\n" +
                    "}")
        }.body()
        return user.id
    }catch (e: Exception) {
        Log.e("logInUser", "Failed to log in user in DataBase: ${e.message}", e)
        Log.e("body", "{\n" +
                "    \"email\": \"$email\",\n" +
                "    \"password\": \"$password\"\n" +
                "}")
    }
    return null
}