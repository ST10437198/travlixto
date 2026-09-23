package com.travlixto.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.ui.theme.TravCyan

/**
 * OTP verification screen. NOTE: Firebase's standard email/password auth
 * does not use an OTP step by default — this UI is wired to call onVerified()
 * directly. If you want real OTP, use Firebase Phone Auth (PhoneAuthProvider)
 * instead of email/password, which sends a real SMS code.
 */
@Composable
fun OtpScreen(onVerified: () -> Unit) {
    val digits = remember { mutableStateListOf("", "", "", "") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("OTP Verification", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Please check your email to see the verification code", fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            digits.forEachIndexed { i, d ->
                OutlinedTextField(
                    value = d,
                    onValueChange = { if (it.length <= 1) digits[i] = it },
                    modifier = Modifier.width(56.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onVerified,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan)
        ) { Text("Verify") }
    }
}
