package com.mihan.movie.library.presentation.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AuthorizationDialog(
    showDialog: Boolean,
    onButtonConfirm: (Pair<String, String>) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            containerColor = dialogBgColor,
            confirmButton = {
                DialogButton(
                    title = stringResource(id = R.string.login_title),
                    onButtonClick = {
                        if (login.isEmpty() || password.isEmpty()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.empty_fields_error_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@DialogButton
                        }
                        onButtonConfirm(login to password)
                    },
                    isEnabled = login.isNotEmpty() && password.isNotEmpty()
                )
            },
            dismissButton = {
                DialogButton(
                    title = stringResource(id = R.string.cancel_title),
                    onButtonClick = onDismissRequest,
                    isEnabled = true
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.authorization_title),
                        fontSize = size18sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextField(
                        value = login,
                        onValueChange = { login = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.email_title),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        textStyle = TextStyle(fontSize = size18sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.DarkGray,
                            focusedContainerColor = Color.DarkGray,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = size18sp),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.password_title),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.DarkGray,
                            focusedContainerColor = Color.DarkGray,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = modifier.fillMaxWidth()
                    )
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier.width(300.dp)
        )
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}
