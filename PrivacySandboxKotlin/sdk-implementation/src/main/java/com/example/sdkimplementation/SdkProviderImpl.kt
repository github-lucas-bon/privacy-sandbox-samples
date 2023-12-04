/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sdkimplementation;

import android.annotation.SuppressLint
import android.app.sdksandbox.SandboxedSdk
import android.app.sdksandbox.SandboxedSdkProvider
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/*
 * This class works as an entry point for the sandbox to interact with the SDK.
 *
 * This class should be populated inside the AndroidManifest file.
 */
@SuppressLint("NewApi")
class SdkProviderImpl : SandboxedSdkProvider() {

    @SuppressLint("Override")
    override fun onLoadSdk(params: Bundle): SandboxedSdk {
        initEngine()
        return SandboxedSdk(SdkApi(context!!))
    }

    @SuppressLint("Override")
    override fun getView(windowContext: Context, bundle: Bundle, width: Int, height: Int): View {
        val webView = WebView(windowContext)
        webView.loadUrl("https://www.teads.com/")
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initEngine() {
        val webView = WebView(context!!)
        Log.d("WebAppInterface", "instance: $webView")

        // Enable JavaScript
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Add the JavaScript interface to the WebView
        webView.addJavascriptInterface(WebAppInterface(), "JSInterface")

        // Set a WebViewClient to handle redirects and loading within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Page finished loading, trigger the JavaScript function
                Log.d("WebAppInterface", "onPageFinished")
                view.evaluateJavascript("loadScript();", null)
            }
        }

        // Load the JavaScript content directly
        webView.loadData(getJavaScriptContent(), "text/html", "UTF-8")
    }

    private fun getJavaScriptContent(): String {
        // Provide static JavaScript content with the function call at the top
        return """
            <html>
                <head>
                    <title>Dummy HTML</title>
                    <script>
                        function loadScript() {
                            JSInterface.receiveMessage('Hello from JavaScript');
                        }
                    </script>
                </head>
                <body>
                    <!-- Your HTML body content here -->
                </body>
            </html>
        """
    }

    // JavaScript interface class
    private inner class WebAppInterface {
        @JavascriptInterface
        fun receiveMessage(message: String) {
            Log.d("WebAppInterface", "Message from JavaScript: $message")
        }
    }
}