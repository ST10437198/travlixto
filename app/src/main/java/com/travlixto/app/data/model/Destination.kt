package com.travlixto.app.data.model

/**
 * A destination/place, stored in Firestore under collection "destinations".
 */
data class Destination(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val pricePerPerson: String = "Free",
    val description: String = ""
)

data class ScheduleItem(
    val id: String = "",
    val destinationName: String = "",
    val city: String = "",
    val date: String = "" // e.g. "2026-01-26"
)
