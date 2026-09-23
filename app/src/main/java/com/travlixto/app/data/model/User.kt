package com.travlixto.app.data.model

/**
 * User profile stored in Firestore under collection "users/{uid}".
 * Firebase Auth handles the actual email/password credential;
 * this document holds the extra profile fields shown in the Edit Profile screen.
 */
data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val location: String = "",
    val mobileNumber: String = "",
    val profilePictureUrl: String = "",
    val rewardPoints: Int = 0,
    val travelTrips: Int = 0,
    val bucketList: Int = 0
)
