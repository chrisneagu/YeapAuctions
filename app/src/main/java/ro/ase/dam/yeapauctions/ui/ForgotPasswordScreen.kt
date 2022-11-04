package ro.ase.dam.yeapauctions


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ro.ase.dam.yeapauctions.ui.components.EditTextField
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme


@Composable
fun ForgotPasswordScreen(modifier: Modifier = Modifier,
                         email: String,
                         onEmailChanged: (String) -> Unit,
                         onBackClicked: () -> Unit
){

    val focusManager = LocalFocusManager.current

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

                IconButton(onClick = onBackClicked){
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

                Box(contentAlignment = Alignment.CenterStart)
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
                        modifier = modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                        leadingicon = R.drawable.favicon_user
                    )

                }

            }

            Button(
                onClick = {  }, //TODO ON CLICK PENTRU LOGIN
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
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
fun ForgotPasswordScreenPreview() {
    YeapAuctionsTheme {
        //ForgotPasswordScreen()
    }
}