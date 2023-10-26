
package com.babyfilx.ui.screens.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyflix.mobileapp.R
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.FormEvent
import com.babyfilx.ui.screens.login.LoginViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*

@Composable
fun ForgotPassword(navController: NavController) {
    val viewModel: ForgotViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val state = viewModel.state
    val status = viewModel.response.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = dp20)
            .padding(bottom = dp20)
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dp50), contentAlignment = Alignment.Center
        ) {
            DrawableImage()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dp50), contentAlignment = Alignment.Center
        ) {
            DrawableImage(modifier = Modifier.size(187.dp,170.dp), R.drawable.cuate)
        }

        TextContent(
            text = R.string.reset_password,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = dp40),
            fontWeight = FontWeight.Bold,
            fontSize = sp24
        ) {}

        TextContent(
            text = R.string.reset_password_desc,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = dp16),
            fontSize = sp14
        ) {}

        EditTextView(
            value = state.email,
            hint = R.string.enter_email_address,
            errorMessage = state.emailError,
            modifier = Modifier.padding(top = dp30)
        ) {
            viewModel.formEvent(FormEvent.Email(it))
        }

        ButtonView(name = R.string.send_to_email, modifier = Modifier.padding(top = dp40)) {
            viewModel.formEvent(FormEvent.Submit)
        }
    }


    MessageAlert(name = viewModel.message) {
        viewModel.message = ""
        if (status.value?.data?.code == 200) {
            navController.navigateUp()
        }
    }

    when (status.value) {
        is Response.Loading -> ProgressBar()
        else -> {}
    }
}