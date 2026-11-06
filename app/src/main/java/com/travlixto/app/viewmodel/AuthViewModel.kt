package com.travlixto.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travlixto.app.data.model.User
import com.travlixto.app.data.repository.AuthRepository
import com.travlixto.app.data.repository.UserRepository
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    val isLoggedIn: Boolean
        get() = authRepository.currentUser != null

    fun signIn(email: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            uiState = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            val authResult = authRepository.signUp(email, password)
            authResult.fold(
                onSuccess = { firebaseUser ->
                    val newUser = User(
                        uid = firebaseUser.uid,
                        firstName = firstName,
                        lastName = lastName,
                        email = email
                    )
                    val profileResult = userRepository.createUserProfile(newUser)
                    uiState = profileResult.fold(
                        onSuccess = { AuthUiState.Success },
                        onFailure = { AuthUiState.Error(it.message ?: "Could not save profile") }
                    )
                },
                onFailure = { uiState = AuthUiState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    fun signInWithGoogle(context: Context) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.signInWithGoogle(context).fold(
                onSuccess = { google ->
                    if (google.isNewUser) {
                        // First time with Google: create their profile document.
                        val name = google.user.displayName.orEmpty().trim()
                        val newUser = User(
                            uid = google.user.uid,
                            firstName = name.substringBefore(" "),
                            lastName = name.substringAfter(" ", ""),
                            email = google.user.email.orEmpty(),
                            profilePictureUrl = google.user.photoUrl?.toString().orEmpty()
                        )
                        userRepository.createUserProfile(newUser)
                    }
                    uiState = AuthUiState.Success
                },
                onFailure = { uiState = AuthUiState.Error(it.message ?: "Google sign in failed") }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            uiState = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Could not send reset email") }
            )
        }
    }

    fun resetState() {
        uiState = AuthUiState.Idle
    }

    fun signOut() {
        authRepository.signOut()
    }
}
