package org.wordpress.android.ui.accounts

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runBlocking
import org.wordpress.android.BuildConfig
import org.wordpress.android.fluxc.store.AccountStore.AuthEmailPayloadScheme
import org.wordpress.android.fluxc.store.SiteStore.ConnectSiteInfoPayload
import org.wordpress.android.ui.accounts.LoginNavigationEvents.ShowNoJetpackSites
import org.wordpress.android.ui.accounts.LoginNavigationEvents.ShowSiteAddressError
import org.wordpress.android.util.BuildConfigWrapper
import org.wordpress.android.viewmodel.Event
import rs.wordpress.api.kotlin.WpLoginClient
import uniffi.wp_api.ParsedUrl
import uniffi.wp_api.WpUuid
import uniffi.wp_api.createApplicationPasswordAuthenticationUrl
import javax.inject.Inject
import kotlin.text.RegexOption.IGNORE_CASE

class LoginViewModel @Inject constructor(private val buildConfigWrapper: BuildConfigWrapper) : ViewModel() {
    private val _navigationEvents = MediatorLiveData<Event<LoginNavigationEvents>>()
    val navigationEvents: LiveData<Event<LoginNavigationEvents>> = _navigationEvents

    fun onHandleSiteAddressError(siteInfo: ConnectSiteInfoPayload) {
        val protocolRegex = Regex("^(http[s]?://)", IGNORE_CASE)
        val siteAddressClean = siteInfo.url.replaceFirst(protocolRegex.toString().toRegex(), "")
        _navigationEvents.postValue(Event(ShowSiteAddressError(siteAddressClean)))
    }

    fun onHandleNoJetpackSites() {
        _navigationEvents.postValue(Event(ShowNoJetpackSites))
    }

    fun getMagicLinkScheme() = if (buildConfigWrapper.isJetpackApp) {
        AuthEmailPayloadScheme.JETPACK
    } else {
        AuthEmailPayloadScheme.WORDPRESS
    }

    fun runApiDiscoveryTest(input: String) = runBlocking {
        val urlDiscovery = WpLoginClient().apiDiscovery(input)
        urlDiscovery.getOrThrow().apiDetails.findApplicationPasswordsAuthenticationUrl()
    }

    fun buildSelfHostedLoginUri(authenticationUrl: String, xmlrpcEndpoint: String): Uri {
        return Uri.parse(createApplicationPasswordAuthenticationUrl(
            ParsedUrl.parse(authenticationUrl),
            BuildConfig.APPLICATION_PASSWORD_APP_NAME,
            WpUuid.parse("00000000-0000-4000-8000-000000000000"),
            successUrl().buildUpon().appendQueryParameter("xmlrpcEndpoint", xmlrpcEndpoint).build().toString(),
            rejectUrl()
        ).url())
    }

    private fun successUrl(): Uri {
        return Uri.Builder()
            .scheme(BuildConfig.APPLICATION_PASSWORD_SCHEME)
            .authority("authorize-success")
            .build()
    }

    private fun rejectUrl(): String {
        return Uri.Builder()
            .scheme(BuildConfig.APPLICATION_PASSWORD_SCHEME)
            .authority("authorize-failure")
            .build()
            .toString()
    }
}
