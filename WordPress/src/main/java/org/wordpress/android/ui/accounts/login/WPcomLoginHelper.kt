package org.wordpress.android.ui.accounts.login

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.wordpress.android.fluxc.network.rest.wpapi.WPcomLoginClient
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AppSecrets
import org.wordpress.android.fluxc.store.AccountStore
import uniffi.wp_api.ParsedUrl.Companion.parse
import uniffi.wp_api.WpApiApplicationPasswordDetails
import uniffi.wp_api.extractLoginDetailsFromUrl
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WPcomLoginHelper @Inject constructor(
    private val loginClient: WPcomLoginClient,
    private val accountStore: AccountStore,
    private val appSecrets: AppSecrets
) {
    private val context: CoroutineContext = Dispatchers.IO

    fun loginUri(): Uri {
        return loginClient.loginUri(appSecrets.redirectUri)
    }

    fun tryLoginWithDataString(data: String?) {
        if (data == null) {
            return
        }

        val code = this.codeFromAuthorizationUri(data) ?: return

        runBlocking {
            val tokenResult = loginClient.exchangeAuthCodeForToken(code)
            accountStore.updateAccessToken(tokenResult.getOrThrow())
            Log.i("WPCOM_LOGIN", "Login Successful")
        }
    }

    fun isLoggedIn(): Boolean {
        return accountStore.hasAccessToken()
    }

    fun dispose() {
        context.cancel()
    }

    private fun codeFromAuthorizationUri(string: String): String? {
        return Uri.parse(string).getQueryParameter("code")
    }

    fun isAttemptingSelfHostedLogin(data: String?): Boolean {
        if (data == null) {
            return false
        }

        return parseLoginDetailsFromUrl(data) != null && parseXmlRpcEndpointFromUrl(data) != null
    }

    fun loginDetails(data: String?): WpApiApplicationPasswordDetails? {
        if (data == null) {
            return null
        }

        return parseLoginDetailsFromUrl(data)
    }

    fun parseXmlRpcEndpointFromUrl(string: String?): String? {
        return Uri.parse(string).getQueryParameter("xmlrpcEndpoint")
    }

    private fun parseLoginDetailsFromUrl(string: String): WpApiApplicationPasswordDetails? {
        try {
            val parsedUrl = parse(string)
            return extractLoginDetailsFromUrl(parsedUrl)
        } catch (ex: Exception) {
            val message = ex.message
            if (message != null) {
                Log.e("WP_RS", message)
            } else {
                Log.e("WP_RS", "Unknown parsing error")
            }

            // TODO: More error handling
        }

        return null
    }
}
