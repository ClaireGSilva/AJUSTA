package com.example.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean
)

object FirebaseAuthService {
    private const val TAG = "FirebaseAuthService"

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _currentUserState = MutableStateFlow<UserProfile?>(null)
    val currentUserState: StateFlow<UserProfile?> = _currentUserState.asStateFlow()

    init {
        // Initialize listener
        try {
            updateUserState(auth.currentUser)
            auth.addAuthStateListener { firebaseAuth ->
                updateUserState(firebaseAuth.currentUser)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth initialization warning: ${e.message}")
        }
    }

    private fun updateUserState(user: FirebaseUser?) {
        _currentUserState.value = if (user != null) {
            UserProfile(
                uid = user.uid,
                displayName = user.displayName ?: if (user.isAnonymous) "Visitante do Atelier" else "Usuário Ajusta",
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
        } else {
            null
        }
    }

    fun getCurrentUser(): FirebaseUser? = try {
        auth.currentUser
    } catch (_: Exception) {
        null
    }

    suspend fun signInWithGoogle(context: Context): Result<UserProfile> {
        return try {
            val credentialManager = CredentialManager.create(context)

            // Web client ID for Google Sign-In (configured via .env / BuildConfig)
            val serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.ifBlank {
                "343905960327-google-signin.apps.googleusercontent.com"
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                updateUserState(user)

                if (user != null) {
                    Result.success(
                        UserProfile(
                            uid = user.uid,
                            displayName = user.displayName,
                            email = user.email,
                            photoUrl = user.photoUrl?.toString(),
                            isAnonymous = false
                        )
                    )
                } else {
                    Result.failure(IllegalStateException("Falha ao obter usuário após autenticação Google."))
                }
            } else {
                Result.failure(IllegalStateException("Credencial recebida não é do tipo Google ID Token."))
            }
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Google Sign In cancelado ou falhou: ${e.message}")
            // Fallback: signInAnonymously for continuous testing if Google Services unavailable
            signInAnonymously()
        } catch (e: Exception) {
            Log.e(TAG, "Erro na autenticação Google: ${e.message}", e)
            signInAnonymously()
        }
    }

    suspend fun signInAnonymously(): Result<UserProfile> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val user = authResult.user
            updateUserState(user)
            if (user != null) {
                Result.success(
                    UserProfile(
                        uid = user.uid,
                        displayName = "Visitante do Atelier",
                        email = null,
                        photoUrl = null,
                        isAnonymous = true
                    )
                )
            } else {
                Result.failure(IllegalStateException("Usuário nulo após login anônimo"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha no login anônimo do Firebase: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            _currentUserState.value = null
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao desconectar: ${e.message}", e)
        }
    }
}
