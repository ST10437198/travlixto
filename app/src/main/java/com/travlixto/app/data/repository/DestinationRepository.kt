package com.travlixto.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.travlixto.app.data.model.Destination
import kotlinx.coroutines.tasks.await

/**
 * Reads destinations/places from Firestore collection "destinations".
 * Powers Home, Popular Places, and Search screens.
 */
class DestinationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val destinationsCollection = firestore.collection("destinations")

    suspend fun getAllDestinations(): Result<List<Destination>> {
        return try {
            val snapshot = destinationsCollection.get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(Destination::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchDestinations(query: String): Result<List<Destination>> {
        return try {
            // Simple prefix search on "name". For production, use Algolia
            // or a dedicated search index — Firestore text search is limited.
            val snapshot = destinationsCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { it.toObject(Destination::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
