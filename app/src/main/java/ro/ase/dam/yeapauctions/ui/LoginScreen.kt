package ro.ase.dam.yeapauctions

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*


import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme

@Composable
fun LoginScreen(modifier: Modifier = Modifier
){

    val focusManager = LocalFocusManager.current

    Column() {

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
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
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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

            var currentUsername by rememberSaveable {mutableStateOf("")}
            var currentPassword by rememberSaveable {mutableStateOf("")}

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
                    value = currentUsername,
                    onValueChange = { currentUsername = it },
                    textStyle = MaterialTheme.typography.headlineMedium,
                    modifier = modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
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
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    textStyle = MaterialTheme.typography.headlineMedium,
                    modifier = modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Button(
                    onClick = {  } //TODO ON CLICK PENTRU LOGIN
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.submit),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.fillMaxHeight()
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "null",
                            modifier = Modifier.fillMaxHeight()
                        )
                    }


                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = stringResource(id = R.string.message_register_login),
                    style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextFieldPassword(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions, //optiuni tastatura ca sa se inchida cand apesi pe done
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle
) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {

            Text(stringResource(id = label),
                    style = MaterialTheme.typography.labelMedium)

        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(imageVector  = image, description)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions, //optiuni tastatura ca sa se inchida cand apesi pe done
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
                Text(stringResource(id = label),
                    style = MaterialTheme.typography.labelMedium)
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    YeapAuctionsTheme {
        LoginScreen()
    }
}