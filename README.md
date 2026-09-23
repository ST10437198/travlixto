# Travlixto — Android (Kotlin + Jetpack Compose + Firebase)

This project implements the screens from your mockups: Splash, Onboarding,
Sign In / Sign Up / Forgot Password / OTP, Home, Details, Schedule, Search,
Popular Places, Profile and Edit Profile — wired to a real backend.

**Important — read this first:** this project was written in a sandbox with no
Android SDK, no emulator, and no internet access, so it has **not been
compiled or run**. You'll need to open it in Android Studio, let Gradle sync
(which downloads all the libraries), fix any small version mismatches Android
Studio flags, and run it on a device/emulator yourself. The code follows
standard, current APIs (Jetpack Compose, Firebase BoM 33.1.2, Retrofit 2.11),
so it should sync cleanly, but I can't guarantee a zero-error first build the
way I could if I'd actually compiled it.

## How the backend is wired (and why not "Retrofit to Firebase")

Firebase isn't a REST API you call through Retrofit — you talk to it with the
official Firebase SDK. So this project uses the correct, standard split:

- **Firebase Authentication** — email/password sign in, sign up, forgot
  password (`data/repository/AuthRepository.kt`).
- **Cloud Firestore** — user profile documents and destinations/places
  (`data/repository/UserRepository.kt`, `DestinationRepository.kt`).
- **Retrofit** — set up and ready (`data/remote/RetrofitClient.kt`,
  `ApiService.kt`) for any *other* REST API you want to call later (e.g. a
  weather API for the destination page, or your own custom backend/Cloud
  Function). It's a stub right now pointing at `https://api.example.com/` —
  swap in a real base URL when you have one.

## 1. Create your Firebase project (you must do this — I can't do it for you)

1. Go to https://console.firebase.google.com → **Add project**.
2. Inside the project, click **Add app → Android**, and register package name
   `com.travlixto.app`.
3. Download the generated **`google-services.json`**.
4. Delete `app/google-services.json.EXAMPLE` and put your real file at
   `app/google-services.json` (exact name, no `.EXAMPLE`).
5. In the Firebase console, enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database** (start in test mode while
     developing, then lock it down — see security rules below)

## 2. Seed some data (optional but needed for Home/Search/Popular Places to show anything)

In Firestore, create a collection called `destinations` and add a few
documents with fields matching `Destination` in
`data/model/Destination.kt` (`id`, `name`, `city`, `rating`, `pricePerPerson`,
`description`, etc).

## 3. Open and run in Android Studio

1. Open the `Travlixto` folder as a project in Android Studio (Koala or
   newer recommended).
2. Let Gradle sync — it will download Compose, Firebase, Retrofit, etc.
3. Run on an emulator or physical device (minSdk 24 / Android 7.0+).

## 4. Recommended Firestore security rules (before you ship)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, update: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null && request.auth.uid == userId;
    }
    match /destinations/{docId} {
      allow read: if true;
      allow write: if false; // manage destinations from the Firebase console or an admin tool
    }
  }
}
```

## 5. "Hosting" this app

An Android app isn't "hosted" the way a website is — it's distributed as an
APK/AAB. Once it runs correctly for you locally:

- **Internal testing**: Build → Generate Signed Bundle/APK in Android Studio,
  share the APK directly, or
- **Play Store**: upload the signed `.aab` to Google Play Console (internal
  testing track is fastest to get a shareable link).

Firebase itself is already "hosted" for you — Auth and Firestore are managed
cloud services, no server to deploy.

## Project structure

```
app/src/main/java/com/travlixto/app/
  MainActivity.kt              — Compose entry point
  TravlixtoApp.kt               — Application class, initializes Firebase
  navigation/NavGraph.kt        — all screen routing + bottom nav
  data/model/                   — User, Destination, ScheduleItem
  data/remote/                  — Retrofit ApiService + client
  data/repository/               — AuthRepository, UserRepository, DestinationRepository
  viewmodel/AuthViewModel.kt    — sign in/up/reset state machine
  ui/screens/                   — one file per screen group, matching your mockups
  ui/theme/                     — colors/typography matching the cyan brand color
```

## Known gaps / things to finish

- **OTP screen** is UI-only. Firebase email/password auth doesn't use OTP;
  if you want a real OTP step, switch to Firebase **Phone Auth**
  (`PhoneAuthProvider`), which actually sends an SMS code.
- **Social sign-in buttons** (Facebook/Instagram/Twitter/Google) are shown in
  your mockups but not wired up — each needs its own SDK/console setup
  (Google Sign-In is easiest to add via Firebase; Instagram/Twitter need
  their own developer app registration).
- **Search** uses a basic Firestore prefix query; fine for a small dataset,
  but swap in Algolia or similar if your places list grows large.
