package ro.ase.dam.yeapauctions


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.components.EditTextFieldPassword
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onForgotPasswordClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    email: String,
    onEmailChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
){

    val focusManager = LocalFocusManager.current

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
                    modifier = modifier.padding(16.dp),
                    leadingicon = R.drawable.favicon_user
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
                    modifier = modifier.padding(16.dp)
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



                Button(
                    onClick = { }, //TODO ON CLICK PENTRU LOGIN
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
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






@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    YeapAuctionsTheme {

    }
}