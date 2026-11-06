package com.travlixto.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.travlixto.app.data.model.Booking
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.model.calculateTotal
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class BookingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val bookings = firestore.collection("bookings")

    suspend fun createBooking(
        destination: Destination,
        date: String,
        travelers: Int,
        notes: String
    ): Result<Booking> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Please sign in to book a trip."))
        return try {
            val doc = bookings.document()
            val booking = Booking(
                id = doc.id,
                userId = uid,
                destinationId = destination.id,
                destinationName = destination.name.ifBlank { "Destination" },
                city = destination.city,
                date = date,
                travelers = travelers,
                pricePerPerson = destination.pricePerPerson,
                totalPrice = calculateTotal(destination.pricePerPerson, travelers),
                notes = notes.trim(),
                status = "Confirmed",
                createdAt = System.currentTimeMillis()
            )
            doc.set(booking).await()
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Live list of the user's bookings, soonest first. Updates the Calendar instantly. */
    fun observeMyBookings(): Flow<List<Booking>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        // No orderBy here on purpose: combining whereEqualTo + orderBy needs a
        // composite index in Firestore. We sort on the device instead.
        val registration = bookings.whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(Booking::class.java) }
                    .orEmpty()
                    .sortedBy { it.date }
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return try {
            bookings.document(bookingId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
