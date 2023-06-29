package ro.ase.dam.yeapauctions.ui.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock

import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ro.ase.dam.yeapauctions.R
import androidx.compose.foundation.Image
import com.vdurmont.emoji.Emoji
import com.vdurmont.emoji.EmojiLoader
import com.vdurmont.emoji.EmojiManager
import com.vdurmont.emoji.EmojiParser

@Composable
fun FlagEmoji(countryCode: String, modifier: Modifier) {
    val result : Emoji? = EmojiManager.getByUnicode(countryCodeToFlagEmojiAlias(countryCode))
    if (result != null) {
        Text(
            text = result.unicode,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier
        )
    }
}

private fun countryCodeToFlagEmojiAlias(countryCode: String): String {
    val codePoints = countryCode.toUpperCase()
        .map { code -> code.toInt() - 0x41 + 0x1F1E6 }
    return String(codePoints.toIntArray(), 0, codePoints.size)
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

            Text(
                stringResource(id = label),
                style = MaterialTheme.typography.labelSmall)

        },
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = MaterialTheme.typography.headlineSmall,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(imageVector  = image, description)
            }
        },

        leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "leadingIcon",  modifier= Modifier.size(20.dp)) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(
    @StringRes label: Int,
    leadingicon: ImageVector,
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
            Text(
                stringResource(id = label),
                style = MaterialTheme.typography.labelSmall)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = MaterialTheme.typography.headlineSmall,
        leadingIcon = { Icon(imageVector= leadingicon, contentDescription= "leadingIcon", modifier= Modifier.size(20.dp)) }
    )
}