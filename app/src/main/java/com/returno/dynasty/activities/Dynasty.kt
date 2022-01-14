package com.returno.dynasty.activities

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import timber.log.Timber
import timber.log.Timber.DebugTree
import com.google.firebase.messaging.FirebaseMessaging
import com.returno.dynasty.BuildConfig
import com.returno.dynasty.utils.UserUtils
import com.returno.dynasty.utils.PostUtils
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.SSLContext

class Dynasty : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0, TlsVersion.SSL_3_0)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()

        val client: OkHttpClient = OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .build()
        AndroidNetworking.initialize(applicationContext,client)
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast")
        sendAnalytics()
    }

    private fun sendAnalytics() {
        if (UserUtils.getAuthStatus(applicationContext)) {
            PostUtils().addAnalytics(UserUtils.getUser(applicationContext).phoneNumber)
        }
    }
}