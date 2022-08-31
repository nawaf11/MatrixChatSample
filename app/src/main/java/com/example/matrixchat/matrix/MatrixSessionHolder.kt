package com.example.matrixchat.matrix

import org.matrix.android.sdk.api.session.Session

object MatrixSessionHolder {

    private var _currentSession : Session? = null

    val currentSession : Session? get() = _currentSession

    fun setSession(session: Session) {
        _currentSession = session
    }

}