package tech.alexib.yaba.kmm.android.ui.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

//@Composable
//fun Password(
//    label: String,
//    passwordState: MutableState<TextFieldValue>,
//    modifier: Modifier = Modifier,
//    imeAction: ImeAction = ImeAction.Done,
//    onImeAction: () -> Unit = {},
//) {
//    val showPassword = remember { mutableStateOf(false) }
//    OutlinedTextField(
//        value = passwordState.value,
//        onValueChange = {
//            passwordState.value = it
//        },
//        modifier = modifier
//            .fillMaxWidth(),
//        textStyle = MaterialTheme.typography.body2,
//        label = {
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//                Text(
//                    text = label,
//                    style = MaterialTheme.typography.body2
//                )
//            }
//        },
//        trailingIcon = {
//            if (showPassword.value) {
//                IconButton(onClick = { showPassword.value = false }) {
//                    Icon(
//                        imageVector = Icons.Filled.Visibility,
//                        contentDescription = "hide password"
//                    )
//                }
//            } else {
//                IconButton(onClick = { showPassword.value = true }) {
//                    Icon(
//                        imageVector = Icons.Filled.VisibilityOff,
//                        contentDescription = "show password"
//                    )
//                }
//            }
//        },
//        visualTransformation = if (showPassword.value) {
//            VisualTransformation.None
//        } else {
//            PasswordVisualTransformation()
//        },
//        keyboardOptions = KeyboardOptions.Default.copy(
//            imeAction = imeAction,
//            capitalization = KeyboardCapitalization.None,
//            autoCorrect = false,
//
//        ),
//        keyboardActions = KeyboardActions(
//            onDone = {
//                onImeAction()
//            }
//        )
//    )
//
//}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "hide password"
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = "show password"
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            autoCorrect = false,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

}