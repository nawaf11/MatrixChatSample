package com.example.matrixchat

import android.app.Application
import android.content.Context
import com.example.matrixchat.matrix.MatrixSessionHolder
import com.example.matrixchat.matrix.RoomDisplayNameFallbackProviderImpl
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration

class MyApp : Application() {

    private lateinit var matrix: Matrix

    override fun onCreate() {
        super.onCreate()

        createMatrix()

        // You can then grab the authentication service and search for a known session
        val lastSession = matrix.authenticationService().getLastAuthenticatedSession()

        if (lastSession != null) {
            MatrixSessionHolder.setSession(lastSession)

//            // Don't forget to open the session and start syncing.
            lastSession.open()
            lastSession.syncService().startSync(true)
        }
    }

    private fun createMatrix() {
        matrix = Matrix(
            context = this,
            matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl())
        )
    }

    companion object {
        fun getMatrix(context: Context): Matrix {
            return (context.applicationContext as MyApp).matrix
        }
    }
}