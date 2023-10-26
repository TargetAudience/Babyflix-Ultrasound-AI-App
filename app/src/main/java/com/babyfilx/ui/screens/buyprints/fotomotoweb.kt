package com.babyfilx.ui.screens.buyprints

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController


@Composable
fun FotomotoScreen(navController: NavController) {

    val text = "https://content4.babyflix.net/p/103/thumbnail/entry_id/0_8dxgia4e/width/200/height/300/type/1/quality/100"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        FotomotoWebView(imageUrl = text)
//        AndroidView(
//            factory = { context ->
//                WebView(context).apply {
//                    settings.javaScriptEnabled = true
//                    webViewClient = WebViewClient()
//                }
//            },
//            update = { webView ->
//                webView.loadDataWithBaseURL(
//                    null,
//                    "<script type=\"text/javascript\" src=\"//widget.fotomoto.com/stores/script/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30.js\"></script><img src=\"https://content4.babyflix.net/p/103/thumbnail/entry_id/0_8dxgia4e/width/200/height/300/type/1/quality/100\" class=\"fotomoto-item\"/><noscript>If Javascript is disabled in your browser, to place orders please visit the page where I <a href='https://my.fotomoto.com/store/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30'>sell my photos</a>, powered by <a href='https://my.fotomoto.com'>Fotomoto</a>.</noscript>",
//                    "text/html",
//                    "UTF-8",
//                    null
//                )
//
//            }
//        )
    }
    

}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FotomotoWebView(imageUrl: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                loadUrl("""
                <html>
                <head>
                    <script type="text/javascript">
                        function printImage() {
                            Fotomoto.Print();
                        }
                    </script>
                </head>
                <body>
                    <div id="fotomoto-widget" data-store-id="c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30"></div>
                    <img src="${imageUrl} alt="My Image">
                    <script type="text/javascript" src="//widget.fotomoto.com/stores/script/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30.js"></script>
                    <noscript>If Javascript is disabled in your browser, to place orders please visit the page where I <a href='https://my.fotomoto.com/store/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30'>sell my photos</a>, powered by <a href='https://my.fotomoto.com'>Fotomoto</a>.</noscript>
                </body>
                </html>
            """.trimIndent())
            }
        },
        update = { webView ->
            webView.loadUrl("""
            <html>
            <head>
                <script type="text/javascript">
                    function printImage() {
                        Fotomoto.Print();
                    }
                </script>
            </head>
            <body>
                <div id="fotomoto-widget" data-store-id="c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30"></div>
                <img src="${imageUrl} alt="My Image">
                <script type="text/javascript" src="//widget.fotomoto.com/stores/script/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30.js"></script>
                <noscript>If Javascript is disabled in your browser, to place orders please visit the page where I <a href='https://my.fotomoto.com/store/c00dfc1b8f839e9f6de03cfa6d4e3f396fabaa30'>sell my photos</a>, powered by <a href='https://my.fotomoto.com'>Fotomoto</a>.</noscript>
            </body>
            </html>
        """.trimIndent())
        }
    )

}
