package com.travlixto.app.data.model

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


data class Booking(
    val id: String = "",
    val userId: String = "",
    val destinationId: String = "",
    val destinationName: String = "",
    val city: String = "",
    val date: String = "",
    val travelers: Int = 1,
    val pricePerPerson: String = "Free",
    val totalPrice: String = "Free",
    val notes: String = "",
    val status: String = "Confirmed",
    val createdAt: Long = 0L
)

private val isoFormat get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
private val displayFormat get() = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }

/** Epoch millis (UTC midnight, as the Material date picker uses) -> "2026-01-26" */
fun millisToIsoDate(millis: Long): String = isoFormat.format(millis)

/** "2026-01-26" -> "Mon, 26 Jan 2026" (falls back to the raw string if it can't be parsed) */
fun isoToDisplayDate(iso: String): String =
    try { displayFormat.format(isoFormat.parse(iso)!!) } catch (e: Exception) { iso }

/**
 * Works out price x travelers from text like "R500", "$25" or "Free".
 * Anything without a number is treated as free.
 */
fun calculateTotal(pricePerPerson: String, travelers: Int): String {
    val cleaned = pricePerPerson.replace(",", "")
    val match = Regex("\\d+(\\.\\d+)?").find(cleaned) ?: return "Free"
    val amount = match.value.toDouble() * travelers
    if (amount == 0.0) return "Free"
    val prefix = cleaned.substring(0, match.range.first).trim()
    val number = if (amount % 1.0 == 0.0) amount.toLong().toString() else String.format(Locale.US, "%.2f", amount)
    return prefix + number
}
