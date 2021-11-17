package techtown.org.repository.utillity

import android.content.Context
import androidx.preference.PreferenceManager

/*
Key 값을 기반으로 token값이 저장하게 할 수 있는 클래스 PreferenceManger를 활용함
 */
class AuthTokenProvider(private val context: Context) {

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }

    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_AUTH_TOKEN, null)
}