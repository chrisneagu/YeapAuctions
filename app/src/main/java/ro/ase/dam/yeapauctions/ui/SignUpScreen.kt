package ro.ase.dam.yeapauctions


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.components.EditTextFieldPassword
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme


@Composable
fun SignUpScreen(modifier: Modifier = Modifier,
                 email: String,
                 phone: String,
                 password: String,
                 confirmed_password: String,
                 name: String,
                 onEmailChanged: (String) -> Unit,
                 onPasswordChanged: (String) -> Unit,
                 onNameChanged: (String) -> Unit,
                 onPhoneChanged: (String) -> Unit,
                 onConfirmedPasswordChanged: (String) -> Unit,
                 onBackClicked: () -> Unit
) {

    val localContext = LocalContext.current
    val focusManager = LocalFocusManager.current
    var errorCode by remember { mutableStateOf(0) }
    when(errorCode) {
        1 -> Toast.makeText(localContext,"Name is empty", Toast.LENGTH_LONG).show()
        2 -> Toast.makeText(localContext,"Invalid format for name", Toast.LENGTH_LONG).show()
        3 -> Toast.makeText(localContext,"Phone is empty", Toast.LENGTH_LONG).show()
        4 -> Toast.makeText(localContext,"Invalid phone format, should be 9 digits", Toast.LENGTH_LONG).show()
        5 -> Toast.makeText(localContext,"Email is empty", Toast.LENGTH_LONG).show()
        6 -> Toast.makeText(localContext,"Invalid email format", Toast.LENGTH_LONG).show()
        7 -> Toast.makeText(localContext,"Password is empty", Toast.LENGTH_LONG).show()
        8 -> Toast.makeText(localContext,"Password must be minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character", Toast.LENGTH_LONG).show()
        9 -> Toast.makeText(localContext,"Passwords do not match", Toast.LENGTH_LONG).show()
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

                IconButton(onClick =  onBackClicked ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.tertiary)
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
                        horizontalAlignment = Alignment.Start){
                        EditTextField(
                            label = R.string.person_name,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            value = name,
                            onValueChange = onNameChanged,
                            textStyle = MaterialTheme.typography.headlineMedium,
                            modifier = modifier.padding(8.dp),
                            leadingicon = R.drawable.favicon_user
                        )

                        EditTextField(
                            label = R.string.phone,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            value = phone,
                            onValueChange = onPhoneChanged,
                            textStyle = MaterialTheme.typography.headlineMedium,
                            modifier = modifier.padding(8.dp),
                            leadingicon = R.drawable.favicon_phone
                        )

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
                            leadingicon = R.drawable.favicon_user
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
                            modifier = modifier.padding(8.dp)
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
                            modifier = modifier.padding(8.dp)
                        )
                    }

            }

            Button( //todo validation on all text fields + push in data base 
                onClick = { errorCode = ValidSignUp(email,phone,password,confirmed_password,name)}, //TODO ON CLICK PENTRU LOGIN
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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


    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    YeapAuctionsTheme {
        //SignUpScreen()
    }
}


fun ValidSignUp( email: String,
                 phone: String,
                 password: String,
                 confirmed_password: String,
                 name: String) : Int{
    if(name.isEmpty())
        return 1
    if (!Regex("\\w\\s\\w").containsMatchIn(name)) {
        return 2 //Invalid name format
    }
    if(phone.isEmpty())
        return 3
    if (!Regex("""\d{9}""").containsMatchIn(phone)) {
        return 4 //Invalid phone format, should be 9 digits
    }

    if(email.isEmpty())
        return 5
    if (!Regex("""[A-Za-z]{1,}[a-zA-Z0-9_.-]+@[a-zA-Z]+\.[a-zA-Z]{1,}""").containsMatchIn(email)) {
        return 6 //Invalid email format
    }

    if(password.isEmpty())
        return 7
    if (!Regex( """(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@${'$'}!%*?&])[A-Za-z\d@${'$'}!%*?&]{8,}""").containsMatchIn(password)) {
        return 8 //Password must be minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    }

    if(!password.equals(confirmed_password))
        return 9


    return 200

}