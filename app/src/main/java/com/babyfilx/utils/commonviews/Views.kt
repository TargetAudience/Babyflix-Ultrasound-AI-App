package com.babyfilx.utils.commonviews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babyfilx.MainViewModel
import com.babyfilx.base.App
import com.babyfilx.data.enums.BottomBarEnum
import com.babyfilx.data.enums.BottomSheetEnum
import com.babyfilx.data.enums.DrawerContentEnum
import com.babyfilx.data.localdatabse.LocalDatabase
import com.babyfilx.data.models.*
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyfilx.ui.screens.home.HomeViewModel
import com.babyfilx.ui.screens.home.validFormat
import com.babyfilx.ui.screens.news.NewsViewModel
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.Constant.getDateFromLong
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.permissions.*
import com.babyfilx.utils.removeScreen
import com.babyfilx.utils.shareData
import com.babyfilx.utils.toast
import com.babyflix.mobileapp.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate



@Composable
fun DrawableImage(
    modifier: Modifier = Modifier.size(224.dp, 81.dp),
    image: Int = R.drawable.splash_logo
) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "Splash",
        modifier = modifier
    )
}


@Composable
fun TextContent(
    text: Int,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = sp15,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Black,
    onclick: () -> Unit
) {
    Text(
        modifier = modifier.clickable {
            onclick()
        },
        text = stringResource(id = text),
        textAlign = textAlign,
        fontSize = fontSize,
        fontFamily = fontsFamily,
        fontWeight = fontWeight,
        color = color
    )
}

@Composable
fun TextContentView(
    text: Int,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = sp15,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Black,
    onclick: () -> Unit
) {
    Text(
        modifier = modifier.clickable {
            onclick()
        },
        text = stringResource(id = text),
        textAlign = textAlign,
        fontSize = fontSize,
        fontFamily = fontsFamilyPoppins,
        fontWeight = fontWeight,
        color = color
    )
}


@Composable
fun EditTextView(
    label: Int = R.string.email,
    value: String,
    hint: Int = R.string.enter_email_address,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isVisible: Boolean = true,
    onValueChange: (String) -> Unit,
) {

    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    Column(modifier = modifier) {
        if (isVisible)
            Text(
                text = stringResource(id = label),
                fontWeight = FontWeight.Normal,
                fontFamily = fontsFamily,
                fontSize = sp15,
                color = Color.Black,
                modifier = Modifier.padding(top = dp15, start = dp4)
            )

        TextField(
            value = value, onValueChange = { onValueChange(it.trim()) },
            modifier = Modifier
                .padding(top = dp4)
                .border(width = dp1, color = border_color, RoundedCornerShape(dp8))
                .background(edit_text_color, RoundedCornerShape(dp8))
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = hint),
                    color = hint_color,
                    fontSize = sp14
                )
            },
            keyboardOptions = keyBoard(keyboardType = keyboardType),
            visualTransformation = if (passwordHidden && keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = sp14,
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            enabled = enabled,
            trailingIcon = {
                if (keyboardType == KeyboardType.Password)
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24
                        // Please provide localized description for accessibility services
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Image(
                            painter = painterResource(id = visibilityIcon),
                            contentDescription = description
                        )
                    }

            }
        )
        if (errorMessage != null)
            Text(
                modifier = Modifier,
                text = errorMessage,
                fontSize = sp14,
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Red
            )
    }


}


@Composable
fun NewEditTextView(
    label: Int = R.string.email,
    value: String,
    hint: Int = R.string.enter_email_address,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isVisible: Boolean = true,
    maxChar:Boolean = true,
    onValueChange: (String) -> Unit,
) {

    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    Column(modifier = modifier) {

        TextField(
            value = value,
            onValueChange = {
                if(maxChar){
                    onValueChange(it.trim())
                }else{
                    onValueChange(it.trim().take(10))
                }
                            },
            modifier = Modifier
                .border(width = dp1, color = create_account_grey, RoundedCornerShape(dp10))
                .background(white, RoundedCornerShape(dp10))
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = hint),
                    color = create_account_grey,
                    fontSize = sp16
                )
            },
            keyboardOptions = keyBoard(keyboardType = keyboardType),
            visualTransformation = if (passwordHidden && keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(
                color = new_black_text,
                fontSize = sp16,
                fontWeight = FontWeight.Normal,
                fontFamily = fontsFamilyPoppins
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            enabled = enabled,
            trailingIcon = {
                if (keyboardType == KeyboardType.Password)
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24
                        // Please provide localized description for accessibility services
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Image(
                            painter = painterResource(id = visibilityIcon),
                            contentDescription = description
                        )
                    }

            },
        )
        if (errorMessage != null)
            Text(
                modifier = Modifier,
                text = errorMessage,
                fontSize = sp14,
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                color = Color.Red
            )
    }


}

@Composable
fun SearchTextView(
    value: String,
    hint: Int = R.string.search,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier.padding(horizontal = dp10)) {
        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .border(width = dp1, color = border_color, RoundedCornerShape(dp30))
                        .background(Color.White, RoundedCornerShape(dp30))
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = dp10), // inner padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.search),
                        contentDescription = "Search Icon",
                        colorFilter = ColorFilter.tint(color = search_icon_color),
                        modifier = Modifier.weight(0.1f),
                    )
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Box(modifier = Modifier.weight(0.8f, fill = true)) {
                        if (value.isEmpty())
                            Text(
                                text = stringResource(id = hint),
                                color = hint_color,
                                fontSize = sp14
                            )
                        innerTextField()

                    }
                    Spacer(modifier = Modifier.width(width = 8.dp))
                    Trailing(value = value, modifier = Modifier.weight(0.1f)) {
                        onValueChange("")
                    }
                }
            }
        )
        if (errorMessage != null)
            Text(
                modifier = Modifier,
                text = errorMessage,
                fontSize = sp14,
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Red
            )
    }


}

@Composable
fun Trailing(value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    if (value.isNotEmpty())
        Image(
            imageVector = Icons.Filled.Clear,
            contentDescription = "Search Icon",
            colorFilter = ColorFilter.tint(color = search_icon_color),
            modifier = modifier.clickable {
                onValueChange("")
            }
        )
}

@Composable
fun CustomListView(
    viewModel: NewsViewModel
) {

    var update by remember {
        mutableStateOf(false)
    }

    if (update) {
        Spacer(modifier = Modifier.width(1.dp))
        update = false
    }

    Column(modifier = Modifier.padding(horizontal = dp10)) {
        LazyRow {
            itemsIndexed(viewModel.catergoryList) { index, item ->
                Column(
                    modifier = Modifier
                        .padding(top = dp20, end = dp5)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.categoryName,
                        modifier = Modifier
                            .background(
                                if (item.isSelected) pink_color else category_text_color,
                                RoundedCornerShape(dp20)
                            )
                            .padding(top = dp6, start = dp20, end = dp20, bottom = dp6)
                            .clickable {
                                viewModel.filterBlogsCategories(index)
                                viewModel.isCategory = index != 0
                                viewModel.id = item.categoryId
                                viewModel.reset()
                                update = true
                            },
                        color = if (item.isSelected) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = sp12,
                        fontFamily = fontsFamily,
                        fontWeight = FontWeight.Bold,
                    )

                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsCardView(
    viewModel: NewsViewModel,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.states




    if (state.items.isNotEmpty()){
        val randomIndex = remember { mutableStateOf(state.items.indices.random()) }


        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dp60)) {

            itemsIndexed(state.items) { index, item ->

                if(index == randomIndex.value){
//                    WebView1(url = "https://servedby.aqua-adserver.com/afr.php?zoneid=10180")
                    // on below line adding admob banner ads.
                    AdView(adId = "ca-app-pub-4273229953550397~976499825")
                }else {

                    if (index >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                        viewModel.getBlogApi()
                    }
                    Card(
                        modifier = Modifier
                            .padding(top = dp10, start = dp10, end = dp10, bottom = dp10)
                            .background(color = Color.White)
                            .clickable { onClick(index) },
                        shape = RoundedCornerShape(dp10),
                        backgroundColor = Color.White
                    )
                    {
                        var like by remember {
                            mutableStateOf(false)
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = dp12)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = dp20, top = dp10)
                            )
                            {
                                ImageUrlLoading(
                                    url = item.blogImagePath,
                                    shape = RoundedCornerShape(dp8),
                                    modifier = Modifier
                                        .padding(top = dp6),
                                    height = dp80,
                                    width = dp100
                                )
                                Column(modifier = Modifier.padding(start = dp20, top = dp5)) {
                                    Text(
                                        text = item.blogTitle,
                                        color = Color.Black,
                                        textAlign = TextAlign.Start,
                                        fontSize = sp16,
                                        fontFamily = fontsFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${item.createdAt.getDateFromLong()} | Working",
                                        modifier = Modifier
                                            .padding(top = dp4),
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        fontSize = sp12,
                                        fontFamily = fontsFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = dp8),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        loge("likescounts ${item.like}")
                                        Likes(count = item.like.toString(), like = item.isLike) {
                                            if (!item.isLike) {
                                                viewModel.likeAndUpdate(index)

                                            }
                                        }
                                        Shares(
                                            context = context, data = item.shareUrl
                                        )

                                    }
                                }
                            }
                        }


                    }
                }
            }

            if (state.isLoading && state.page > 1)
                item {
                    viewModel.states = viewModel.states.copy(error = null, endReached = false)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dp90)
                    ) {
                        ProgressBar()
                    }
                }

            val error = viewModel.states.error ?: ""
            if (error.isNotEmpty() && (viewModel.states.page > 1 && !viewModel.states.endReached)) {
                item {
                    ErrorMessage(error) {
                        viewModel.states = viewModel.states.copy(error = null, endReached = false)
                        viewModel.getBlogApi()
                    }
                }
            }

        }}



    if (viewModel.states.isLoading && viewModel.states.page == 1)
        ProgressBar(modifier = Modifier.statusBarsPadding())

    val error = viewModel.states.error ?: ""
    if (error.isNotEmpty() && viewModel.states.page == 1) {
        ErrorMessage(error) {
            viewModel.states = viewModel.states.copy(error = null)
            viewModel.getBlogApi()
            viewModel.likesStatusApi()
        }
    }


}

@Composable
fun Shares(context: Context, data: String) {
    Row(Modifier.clickable {
       /* val m = data.lowercase()
            .replace(" ", "")

        val d = "${BuildConfig.BASE_URL}blog/${m}"
        loge("dnasfjksnjkjgf $d")*/
        context.shareData(data)

    }, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            ImageVector.vectorResource(id = R.drawable.share_grey),
            "",
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = dp12)

        )
        Text(
            text = stringResource(id = R.string.share),
            modifier = Modifier.padding(start = dp5),
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = sp12,
            fontFamily = fontsFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Likes(count: String = "10", like: Boolean, onClick: () -> Unit) {
    LikeAndUnlike(isLive = like) {
        Row(modifier = Modifier.clickable {

            onClick()
        }, verticalAlignment = Alignment.CenterVertically) {

            Icon(
                ImageVector.vectorResource(id = R.drawable.like),
                "",
                tint = if (like) pink_color else Color.Gray,
            )
            Text(
                text = count,
                modifier = Modifier.padding(start = dp5),
                color = if (like) pink_color else Color.Gray,
                textAlign = TextAlign.Center,
                fontSize = sp12,
                fontFamily = fontsFamily,
                fontWeight = FontWeight.Medium
            )
        }

    }
}


@Composable
fun CheckBoxWithCondition(
    isCheck: Boolean,
    errorMessage: String?,
    navController: NavController,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = (-12).dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCheck,
            onCheckedChange = { onClick(isCheck) },
            colors = CheckboxDefaults.colors(
                checkedColor = button_color,
                uncheckedColor = hint_color,
                disabledColor = hint_color
            )
        )
        /*  StringTextContent(
              message = stringResource(id = R.string.accept_the),
              modifier = Modifier.weight(0.4f),
              fontWeight = FontWeight.Normal,
          )*/
        StringTextContent(
            message = stringResource(id = R.string.terms_and_conditions),
            modifier = Modifier.clickable {
                navController.navigate("${Screens.Help.root}/${true}")
            },
            fontWeight = FontWeight.SemiBold,
            color = button_color,
            isUnderline = true
        )
    }
    if (errorMessage != null)
        Text(
            modifier = Modifier,
            text = errorMessage,
            fontSize = sp14,
            fontFamily = fontsFamily,
            fontWeight = FontWeight.Normal,
            color = Color.Red
        )

}

/**
 * this is for keyboard type
 */
fun keyBoard(keyboardType: KeyboardType): KeyboardOptions {
    return KeyboardOptions(keyboardType = keyboardType)
}


@Composable
fun ButtonView(name: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    //hideKeyboard()
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = button_color,
        ),
        contentPadding = PaddingValues(vertical = dp15)
    ) {
        Text(
            text = stringResource(id = name),
            fontSize = sp15,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun hideKeyboard() {
    val focusManager = LocalSoftwareKeyboardController.current
    focusManager?.hide()
}



@Composable
fun ProgressBar(modifier: Modifier = Modifier, withText: Boolean = false) {
    if(withText)
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { }
                .background(Color.Transparent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dp40, end = dp40)
                    .clip(RoundedCornerShape(dp20))
                    .background(white),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = pink_color,
                    modifier = Modifier.padding(top = dp20, bottom = dp10)
                )
                Text(
                    text = "Please wait....",
                    color = Color.Black,
                    fontSize = sp16,
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = dp10, bottom = dp10)
                )
            }
        }
    else
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable { }
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = pink_color)
    }
}

@Composable
fun ProgressDialog(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                modifier = modifier
                    .size(200.dp)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ProgressBar(modifier = Modifier.size(dp48))
                    Spacer(modifier = Modifier.height(dp16))
                    Text(
                        text = "Upgrading, please wait!!!",
                        color = Color.Black,
                        fontSize = sp16,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



@Composable
fun StringTextContent(
    modifier: Modifier = Modifier
        .padding(top = dp10)
        //  .height(31.dp)
        .fillMaxWidth(),
    message: String?,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Left,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = sp14,
    isUnderline: Boolean = false,
) {
    if (message?.isEmpty() == false)
        Text(
            modifier = modifier,
            text = message,

            fontSize = fontSize,
            fontFamily = fontsFamilyPoppins,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None

        )
}

@Composable
fun TopAppBarsWithTextOnly(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.gallery),
    rightTitle: String = "Enhance",
    leftTitle: String = stringResource(id = R.string.cancel),
    isCancel: Boolean = false,
    isEnhance: Boolean = true,
    isContinue: Boolean = false,
    onClick: (Int) -> Unit,
) {
    if(title.equals(stringResource(id = R.string.processing_image)) || title.equals(stringResource(id = R.string.enhancement_complete)) || title.equals(stringResource(id = R.string.my_account))){
        TopAppBar(
            modifier = modifier,
            backgroundColor = white,
            contentColor = new_black_text,
            elevation = AppBarDefaults.TopAppBarElevation
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(), // Take up available space
                fontFamily = fontsFamilyPoppins,
                fontWeight = FontWeight.Normal,
                fontSize = sp16,
                color = new_black_text
            )
        }
    }else {
        TopAppBar(
            modifier = modifier,
            backgroundColor = white,
            contentColor = new_black_text,
            elevation = AppBarDefaults.TopAppBarElevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dp14_5, end = dp20),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isCancel) {
                    Text(
                        text = leftTitle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable {
                                onClick(2)
                            }
                            .width(dp70), // Add padding to the start and end
                        color = pink_color,
                        fontFamily = fontsFamilyPoppins,
                        fontWeight = FontWeight.Normal,
                        fontSize = sp16
                    )
                } else {
                    // Spacer to occupy the space where leftTitle would be
                    Spacer(modifier = Modifier.width(dp70))
                }

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = dp40), // Take up available space
                    fontFamily = fontsFamilyPoppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = sp16,
                    color = new_black_text
                )

                if (isEnhance) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = pink_button,
                                shape = RoundedCornerShape(dp30)
                            )
                            .width(dp104)
                            .height(dp30), // Add padding to the end
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.magic_wand_black),
                            contentDescription = "magic",
                            modifier = Modifier
                                .height(dp28)
                                .width(dp28)
                                .padding(dp2)
                        )
                        Text(
                            text = rightTitle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(start = dp6)
                                .background(
                                    color = pink_button,
                                    shape = RoundedCornerShape(dp30)
                                )
                                .clickable {
                                    onClick(1)
                                },
                            color = white,
                            fontFamily = fontsFamilyPoppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = sp12
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .width(dp104) // Set a fixed width
                            .height(dp30) // Set a fixed height
                            .background(
                                color = if (isContinue) pink_button else pink_button.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(dp30)
                            )
                            .alpha(if (isContinue) 1f else 0.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.continue_text),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable {
                                    if (isContinue)
                                        onClick(3)
                                },
                            color = white,
                            fontFamily = fontsFamilyPoppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = sp12
                        )
                    }

                }
            }
        }
    }
}


@Composable
fun TopAppBars(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.gallery),
    rightTitle: String = stringResource(id = R.string.done),
    isMenu: Boolean = true,
    isDone: Boolean = true,
    isEdit: Boolean = true,
    isIcons: ImageVector = Icons.Filled.Menu,
    onClick: (Int) -> Unit
) {

    TopAppBar(modifier = modifier, title = {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(if (isDone) 0.93f else 0.8f),
            fontFamily = fontsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = sp18
        )
    }, navigationIcon = {
        if (isMenu)
            IconButton(onClick = { onClick(1) }) {
                Icon(isIcons, "")
            }
    },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        actions = {
            if (isDone) Text(
                text = rightTitle,
                modifier = Modifier
                    .padding(end = dp16)
                    .clickable {
                        onClick(2)
                    },
                color = pink_color,
                fontSize = sp15
            ) else {
//                Divider(Modifier.fillMaxWidth())
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Context.BottomSheetOpen(
    scope: CoroutineScope,
    viewModel: HomeViewModel,
    bottomSheetScaffoldState: ModalBottomSheetState,
    upload: (Boolean) -> Unit
) {
    val permissionRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap -> }
    val isPicker = isPhotoPickerAvailable()

    LaunchedEffect(Unit) {
        val permission = pushNotificationPermission()
        if (permission.isNotEmpty()) {
            permissionRequest.launch(permission.toTypedArray())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            loge("gjfh ${viewModel.uri}")
            if (success)
                upload(true)
        }
    )

    loge("IsPicker  $isPicker")
    val gallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.uri = uri.toString()
            //viewModel.callUploadApi()
            if (uri != null) {
                validFormat(viewModel.isVideo, viewModel) {
                    upload(true)
                }
            }
        }
    )

    val galleryNotPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.uri = uri.toString()
            //viewModel.callUploadApi()
            if (uri != null) {
                validFormat(viewModel.isVideo, viewModel) {
                    upload(true)
                }
            }
        }
    )
    val pickVideoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    viewModel.uri = it.data.toString()
                    //viewModel.callUploadApi()
                    upload(true)
                } ?: toast("Something went going wrong")
            }
        }

    if (viewModel.bottomSheet)
        BottomSheetForImagePicker(bottomSheetScaffoldState, viewModel) { it ->
            loge("messagesss ")
            val permission = updateOrRequestPermissions()
            if (permission.isNotEmpty() && (it != BottomSheetEnum.Any && it != BottomSheetEnum.Nothing)) {
                loge("messagesss $permission")
                permissionRequest.launch(permission.toTypedArray())
            } else {
                when (it) {
                    BottomSheetEnum.Image_Camera -> {
                        myClickHandler { uri ->
                            viewModel.uri = uri.toString()
                            cameraLauncher.launch(uri)
                        }
                    }
                    BottomSheetEnum.Image_Gallery -> {
                        viewModel.isVideo = false
                        if (isPicker)
                            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        else
                            galleryNotPicker.launch("image/*")
                    }
                    BottomSheetEnum.Video_Camera -> {
                        cameraIntent { intent ->
                            pickVideoLauncher.launch(intent)
                        }
                    }
                    BottomSheetEnum.Video_Gallery -> {
                        viewModel.isVideo = true
                        if (isPicker)
                            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        else
                            galleryNotPicker.launch("video/*")
                    }
                    else -> {}
                }

                scope.launch {
                    if (it != BottomSheetEnum.Any) {
                        bottomSheetScaffoldState.hide()
                        viewModel.bottomSheet = false
                    }

                }
            }
        }

}




@Composable
fun HeaderLayout(localDatabase: LocalDatabase, onClick: (Int) -> Unit) {

    val data = localDatabase.data.collectAsState(initial = LocalDataModel())
    Column(Modifier.fillMaxWidth()) {/*
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = {
                onClick(1)
            }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription ="Close" )
            }
        }*/

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp20)
                .clickable {
                    onClick(2)
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            //ImageUrlLoading(url = R.drawable.logo)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(dp48)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = dp16),
                verticalArrangement = Arrangement.Center
            ) {
                StringTextContent(
                    modifier = Modifier,
                    message = if (data.value.isLogin) "Hi, ${data.value.name}" else "Login",
                    fontSize = sp16,
                    fontWeight = FontWeight.Bold
                )
                if (data.value.isLogin)
                    StringTextContent(
                        modifier = Modifier,
                        message = "Welcome Back!",
                        fontSize = sp16
                    )
            }
        }
    }
}

@Composable
fun DrawerBodyLayout(
    navController: NavController,
    viewModel: MainViewModel,
    scope: CoroutineScope,
    onClick: (DrawerContentModel) -> Unit
) {
    val list = mutableListOf(
        DrawerContentModel("Gallery", DrawerContentEnum.Gallery),
        DrawerContentModel("News", DrawerContentEnum.News),
        DrawerContentModel("Setting", DrawerContentEnum.Setting),
        DrawerContentModel("Flix10K", DrawerContentEnum.Flix10K),
    )

    LazyColumn {
        itemsIndexed(list) { _, item ->
            RowItems(model = item) {
                onClick(it)
                when (it.contentModel) {
                    DrawerContentEnum.Setting -> {
                        if (navController.currentDestination!!.route != Screens.Dashboard.root)
                            navController.navigate(Screens.Dashboard.root)
                    }
                    DrawerContentEnum.Gallery -> {
                        if (navController.currentDestination!!.route != Screens.Home.root)
                            navController.navigate(Screens.Home.root)
                    }
                    DrawerContentEnum.News -> {
                        if (navController.currentDestination!!.route != Screens.News.root)
                            navController.navigate(Screens.News.root)
                    }
                    DrawerContentEnum.Flix10K -> {
                        if(App.data.userType == "premium"){
                            if (navController.currentDestination!!.route != Screens.ImageSelection.root)
                                navController.navigate(Screens.ImageSelection.root)
                        }else {
                            if (navController.currentDestination!!.route != Screens.Subscription.root)
                                navController.navigate(Screens.Subscription.root)
                        }
                    }
                    else -> {}
                }

            }
        }
        item {
            ButtonView(name = R.string.logout, modifier = Modifier.padding(dp20)) {
                onClick(DrawerContentModel("", DrawerContentEnum.Logout))
                viewModel.showDialog = true
            }
        }
    }

    if (viewModel.showDialog)
        Alert(name = stringResource(id = R.string.logout_message),
            title = stringResource(id = R.string.logout),
            onDismiss = { viewModel.showDialog = !viewModel.showDialog },
            {
                scope.launch {
                    viewModel.localDatabase.clear()

                }
                navController.removeScreen(Screens.Login.root)
                navController.navigate(Screens.Login.root)
                viewModel.showDialog = !viewModel.showDialog
            }
        ) {

        }

}


@Composable
fun RowItems(model: DrawerContentModel, onClick: (DrawerContentModel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dp10)
            .clickable {
                onClick(model)
            }, horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StringTextContent(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = dp20),
            message = model.title,
            fontSize = sp16,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(onClick = { }) {
            Icon(Icons.Filled.KeyboardArrowRight, "")
        }

    }
}

@Composable
fun SettingRowItems(model: SettingContentModel, onClick: (SettingContentModel) -> Unit) {
    HorizontalLine(horizline_profile)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dp10)
            .clickable {
                onClick(model)
            }, horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StringTextContent(
            modifier = Modifier
                .wrapContentWidth(),
            message = model.title,
            fontSize = sp16,
            fontWeight = FontWeight.Normal
        )
        IconButton(onClick = { onClick(model)}) {
            Icon(Icons.Filled.KeyboardArrowRight, "")
        }

    }
}


@Composable
fun ImageUrlLoading(
    modifier: Modifier = Modifier,
    url: String = "https://images.unsplash.com/photo-1459262838948-3e2de6c1ec80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=869&q=80",
    shape: Shape = CircleShape,
    height: Dp = dp48,
    width: Dp = dp48,
    isModifier: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop
) {

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),

        placeholder = painterResource(R.drawable.no_media),
        contentDescription = stringResource(R.string.gallery),
        contentScale = contentScale,
        modifier = if (isModifier) {
            modifier
                .clip(shape)
                .size(width, height)
        } else modifier,
        onLoading = {

        },
        error = painterResource(R.drawable.no_thumbnail)
    )
}


@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    isIcon: Boolean = true,
    isShare: Boolean = true,
    paddingBottom: Dp = 0.dp,
    onClick: (BottomBarEnum) -> Unit
) {
    Card(
        backgroundColor = Color.White, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = paddingBottom)
            .height(dp60), elevation = dp15
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = if (isIcon) 0.dp else 30.dp)
                .height(dp60),
            horizontalArrangement = if (isIcon) Arrangement.SpaceAround else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            IconButton(onClick = {
                onClick(BottomBarEnum.Upload)
            }) {
                Image(
                    painter = if (isShare) painterResource(id = R.drawable.upload) else painterResource(
                        id = R.drawable.share
                    ), contentDescription = null
                )
            }
            if (isIcon)
                IconButton(onClick = {
                    onClick(BottomBarEnum.Download)
                }) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.download),
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier.padding(top = dp10),
                            painter = painterResource(id = R.drawable.download1),
                            contentDescription = null
                        )
                    }
                }
            IconButton(onClick = {
                onClick(BottomBarEnum.Delete)
            }) {
                Image(
                    painter = painterResource(id = if (isIcon) R.drawable.delete else R.drawable.deletes),
                    contentDescription = null
                )
            }
        }
    }
}


@Composable
fun ErrorMessage(message: String, onClick: () -> Unit) {

    if (message.isNotEmpty())
        Column(
            modifier = Modifier
                .clickable(enabled = true) {}
                .focusable(true)
                .fillMaxSize()
                .background(Color.White)
                .padding(dp20),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            StringTextContent(
                message = message,
                fontSize = sp18,
                textAlign = TextAlign.Center
            )
            ButtonView(
                name = R.string.retry, modifier = Modifier
                    .width(120.dp)
                    .padding(top = dp20)
            ) {
                onClick()
            }
        }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView1(url: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let { urlString ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                        context.startActivity(intent)
                    }
                    return true
                }
            }
            loadUrl(url)
        }
    }, modifier = Modifier.fillMaxWidth())
//    val context = LocalContext.current
//    AndroidView(
//        modifier = Modifier.fillMaxWidth(),
//        factory = { WebView(it)}) { webview ->
//        webview.loadUrl(url)
//    }
}

@Composable
fun AdView(adId: String){
    AndroidView(
        // on below line specifying width for ads.
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            // on below line specifying ad view.
            com.google.android.gms.ads.AdView(context).apply {
                // on below line specifying ad size
                adSize = AdSize.BANNER
                // on below line specifying ad unit id
                // currently added a test ad unit id.
                adUnitId = adId
                // calling load ad to load our ad.
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url: String, modifier: Modifier = Modifier.fillMaxSize()) {


    var backEnabled by remember { mutableStateOf(false) }
    var load by remember { mutableStateOf(true) }
    var webView: WebView? = null
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        backEnabled = view.canGoBack()
                        load = true
                    }

                    override fun onLoadResource(view: WebView?, url: String?) {
                        super.onLoadResource(view, url)
//                        load = false
                    }


                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        load = false
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        context.toast(error?.description.toString())
                        load = false
                        super.onReceivedError(view, request, error)
                    }


                }
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true;
                settings.loadWithOverviewMode = true;
                settings.domStorageEnabled = true;
                loadUrl(url)
                webView = this
            }
        }, update = {
            webView = it
        })

    BackHandler(enabled = backEnabled) {
        webView?.goBack()
    }

    if (load)
        ProgressBar()
}


@Composable
fun PasswordStrength(
    password: String, modifier: Modifier = Modifier
        .fillMaxWidth()
) {

    Row(
        modifier = modifier
            .padding(top = dp10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StringTextContent(
            message = stringResource(id = R.string.password_strength),
            modifier = Modifier.weight(0.4f),
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth(),
            progress = if (password.isEmpty()) 0f
            else if (password.length < 6) 1f else if (!password.any { it.isLetter() }) 0.7f else if (!password.any { it.isDigit() }) 0.7f else if (!password.any { it.isUpperCase() }) 0.7F else 1f,
            backgroundColor = edit_text_color,
            color = if (password.isEmpty()) edit_text_color else if (password.length < 6) Color.Red
            else if (!password.any { it.isLetter() }) Color.Yellow else if (!password.any { it.isDigit() }) Color.Yellow else if (!password.any { it.isUpperCase() }) Color.Yellow else Color.Green,
        )
    }
}


@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@Composable
fun ShareAndCopy(
    image: Int = R.drawable.video_copy_url,
    title: String = stringResource(id = R.string.copy_link),
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(
                dp18
            )
        )

        StringTextContent(message = title, modifier = Modifier.wrapContentWidth())
    }
}


@Composable
fun VideoDetailList(
    l: List<HomeEntriesModel>,
    url: String,
    onClick: (HomeEntriesModel) -> Unit
) {

    val list = l.filter { it.download_url != url }
    LazyVerticalGrid(
        modifier = Modifier.padding(start = dp16, end = dp10),
        columns = GridCells.Fixed(2)
    ) {

        itemsIndexed(list) { _, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(item)
                    }
                    .padding(end = dp6, bottom = dp6),
                backgroundColor = Color.White,
                elevation = dp4
            ) {
                ImageUrlLoading(
                    url = item.thumb_url,
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    height = dp92
                )

            }

        }
    }
}


@Composable
fun DateAndTime() {

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker() {
    val date = remember {
        LocalDate.now()
    }
}

@Composable
fun NotificationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onOkClicked: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onOkClicked() // Execute the callback when the button is clicked
                    onDismiss()
                }) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Composable
fun PleaseWaitDialog(mainActivityScope: CoroutineScope) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "Please Wait",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp) // Customize the size of the progress indicator
                )
            }
        },
        buttons = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HeaderRowDashboard(initial: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dp4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StringTextContent(
            modifier = Modifier,
            message = initial,
            fontSize = sp22,
            fontWeight = FontWeight.ExtraBold,
        )
        StringTextContent(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .height(24.dp),
            message = stringResource(id = R.string.see_all),
            color = pink_button,
            fontSize = sp17,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun HorizontalLine(horizline_profile: Color = horizline_color) {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = horizline_profile,
        thickness = dp1
    )
}

@Composable
fun TopAppBarsWithButton(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.gallery),
    rightTitle: String = stringResource(id = R.string.done),
    isMenu: Boolean = true,
    isDone: Boolean = true,
    isIcons: ImageVector = Icons.Filled.Menu,
    onClick: () -> Unit
) {

    TopAppBar(modifier = modifier, title = {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(23.dp)
                .padding(start = dp60),
            fontFamily = fontsFamilyPoppins,
            fontWeight = FontWeight.Normal,
            fontSize = sp16
        )
    },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        actions = {
            Text(
                text = "Upgrade",
                color = Color.White,
                fontSize = sp12,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = pink_button,
                        shape = RoundedCornerShape(dp30) // Rounded corners
                    )
                    .padding(top = dp5, bottom = dp5, start = dp13, end = dp13)
                    .clickable {
                        onClick()
                    }
            )
        }
    )
}


@Composable
fun NewButton(
    modifier: Modifier = Modifier,
    label: Int,
    onClick: () -> Unit
){
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = pink_button),
        shape = RoundedCornerShape(dp30)
    ) {
        Text(
            text = stringResource(id = label),
            fontFamily = fontsFamilyPoppins,
            fontWeight = FontWeight.Bold,
            fontSize = sp20,
            color = Color.White
        )
    }
}


