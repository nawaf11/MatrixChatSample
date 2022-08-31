package com.example.matrixchat.matrix

import android.content.Context
import android.net.Uri
import android.util.Log
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

object MatrixUtils {

    fun homServerConfig() : HomeServerConnectionConfig? {
            val homeServer = "https://matrix.org"

            return try {
                HomeServerConnectionConfig
                    .Builder()
                    .withHomeServerUri(Uri.parse(homeServer))
                    .build()

            }
            catch (e : Throwable) {
                Log.d("MatrixUtils",
                    "homServerConfig Home server is not valid $homeServer")
                null
            }
    }

}