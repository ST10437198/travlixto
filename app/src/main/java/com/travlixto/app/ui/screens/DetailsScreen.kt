package com.travlixto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.Destination
import com.travlixto.app.ui.theme.TravCyan

@Composable
fun DetailsScreen(destination: Destination, onBookNow: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color(0xFF4A90D9))
        ) {
            Text(
                destination.name.ifBlank { "Destination" },
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }
        Column(modifier = Modifier.padding(20.dp).weight(1f)) {
            Text(destination.name.ifBlank { "Destination" }, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(destination.city.ifBlank { "Cape Town" }, fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text("About Destination", fontWeight = FontWeight.SemiBold)
            Text(
                destination.description.ifBlank {
                    "You will get a complete travel package on the beaches. Packages in the form of airline tickets, recommended hotel rooms, transportation. Have you ever been on holiday to South Africa? Read More"
                },
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
        Button(
            onClick = onBookNow,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan)
        ) { Text("Book Now") }
    }
}
