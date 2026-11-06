package com.travlixto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.ui.theme.TravCyan
import com.travlixto.app.viewmodel.AuthUiState
import com.travlixto.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoToSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is AuthUiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Sign in now", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Please sign in to continue our app", fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        TextButton(onClick = onForgotPassword, modifier = Modifier.align(Alignment.End)) { Text("Forgot Password?") }

        if (viewModel.uiState is AuthUiState.Error) {
            Text((viewModel.uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { viewModel.signIn(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan),
            enabled = viewModel.uiState !is AuthUiState.Loading
        ) {
            if (viewModel.uiState is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), color = androidx.compose.ui.graphics.Color.White)
            } else {
                Text("Sign In")
            }
        }
        GoogleSignInSection(viewModel)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? ")
            Text("Sign up", color = TravCyan, modifier = Modifier.clickableText(onGoToSignUp))
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onGoToSignIn: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is AuthUiState.Success) {
            viewModel.resetState()
            onSignUpSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Sign up now", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Please fill the details and create account", fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Text("Password must be 8 characters", fontSize = 11.sp)

        if (viewModel.uiState is AuthUiState.Error) {
            Text((viewModel.uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { viewModel.signUp(firstName, lastName, email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan),
            enabled = viewModel.uiState !is AuthUiState.Loading
        ) {
            if (viewModel.uiState is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), color = androidx.compose.ui.graphics.Color.White)
            } else {
                Text("Sign Up")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? ")
            Text("Sign in", color = TravCyan, modifier = Modifier.clickableText(onGoToSignIn))
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: AuthViewModel, onEmailSent: () -> Unit) {
    var email by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is AuthUiState.Success) {
            viewModel.resetState()
            onEmailSent()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Forgot password", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Enter your email account to reset your password", fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

        if (viewModel.uiState is AuthUiState.Error) {
            Text((viewModel.uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.sendPasswordReset(email) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan)
        ) {
            Text("Reset Password")
        }
    }
}

/** "or" divider + "Sign in with Google" button, shown on the Sign In screen. */
@Composable
private fun GoogleSignInSection(viewModel: AuthViewModel) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE0E0E0)))
        Text("  or  ", fontSize = 12.sp, color = Color.Gray)
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE0E0E0)))
    }
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedButton(
        onClick = { viewModel.signInWithGoogle(context) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF1F1F1F)),
        enabled = viewModel.uiState !is AuthUiState.Loading
    ) {
        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Sign in with Google")
    }
}

// Small helper to make a Text clickable without pulling in extra imports everywhere.
private fun Modifier.clickableText(onClick: () -> Unit): Modifier =
    this.clickable { onClick() }
