package com.travlixto.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.ui.theme.TravCyan

private data class OnboardPage(val title: String, val highlight: String, val body: String)

private val pages = listOf(
    OnboardPage("Welcome to ", "South Africa, the land for the people.",
        "At Friends tours and travel, we customize reliable and trustworthy educational tours to destinations all over the world"),
    OnboardPage("It's a big country out there, go ", "Explore IT!",
        "To get the best of your adventure you just need to leave and go where you like, we are waiting for you"),
    OnboardPage("People don't take trips, trips take ", "People.",
        "To get the best of your adventure you just need to leave and go where you like, we are waiting for you")
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var pageIndex by remember { mutableStateOf(0) }
    val page = pages[pageIndex]

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Skip") }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(page.title + page.highlight, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(page.body, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            pages.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .height(4.dp)
                        .width(if (i == pageIndex) 24.dp else 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (pageIndex < pages.lastIndex) pageIndex++ else onFinished()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan)
        ) {
            Text(if (pageIndex == 0) "Get Started" else "Next")
        }
    }
}
