package com.babyfilx.utils.commonviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.babyfilx.ui.theme.*

@Composable
fun Alert(
    name: String,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    function: () -> Unit,
) {

    CustomizeDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dp20, end = dp20, top = dp8)
        ) {
            StringTextContent(
                message = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = sp18
            )
            Text(text = name, modifier = Modifier.padding(top = dp20))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dp20), horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("No")
                }
                TextButton(onClick = onConfirm) {
                    Text("Yes")
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomizeDialog(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Card(
                elevation = 8.dp,
                shape = RoundedCornerShape(dp8),
                backgroundColor = Color.White
            ) {
                content()
            }
        }
    }
}


@Composable
fun MessageAlert(
    name: String,
    onDismiss: () -> Unit,
) {
    if (name.isNotEmpty())
        CustomizeDialog(onDismiss = onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dp4)
            ) {
                StringTextContent(
                    message = stringResource(id = com.babyflix.mobileapp.R.string.error_message),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = sp18
                )
                Text(
                    text = name,
                    modifier = Modifier
                        .padding(vertical = dp15)
                        .fillMaxWidth().padding(horizontal = dp10),
                    textAlign = TextAlign.Center
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dp1)
                        .background(hint_color)
                )
                TextButton(onClick = onDismiss) {
                    Text("OK", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

        }
}