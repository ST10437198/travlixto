**THE TRAVLIXTO APK IS IN THE REPOSITORY UNDER (travlixto.apk) 

**YOUTUBE LINK: https://youtu.be/V05CFgzsGD4?si=HXs3nI5OhsiARVN9**

**The evidence for the signed APK in in the travlixto report PDF in the REPOSITORY**

- Core app: a Kotlin and Jetpack Compose Android project with the screens from your mockups: Splash, Onboarding, Sign In, Sign Up, Forgot Password, OTP, Home, Details, Schedule, Search, Popular Places, Profile and Edit Profile. It has bottom-tab navigation and the cyan Travlixto theme.
- **Firebase backend:** Firebase Authentication for sign in, sign up and password reset, and Cloud Firestore for user profiles and destinations. Retrofit is set up and ready for any REST API, but it isn't connected to anything yet.
- Booking feature:
  - The **Book Now** button opens a new booking screen with a date picker, a guests stepper, contact details and a price summary.
  - Bookings save to Firestore under `users/{uid}/schedule`, and duplicate bookings for the same place and day are blocked.
  - Bookings show live on the Calendar tab, where you can also cancel a trip.
  - The Profile "Travel Trips" count goes up and down with bookings.
  - Bookings made offline sync later.
- **Destination images:** I added code to show a destination's `imageUrl` photo on the Home cards, the Details header, and the booking and calendar screens. It falls back to a blue placeholder when there's no photo.
- **Languages:** English, Afrikaans and isiXhosa, with a language button on Onboarding, Sign In and Profile. Dates, buttons, labels and messages all switch together. The isiXhosa text needs a native speaker to check it.
- **App icon:** a white paper plane on the cyan brand colour. You applied it through Android Studio's Image Asset tool.
- **Smaller fixes:** Search results are tappable, onboarding progress dots are visible, and the Calendar has a week strip with dots on days that have trips.

- Creating the Firebase project and adding `google-services.json`.
- Publishing the Firestore rules, and adding the destination documents and their `imageUrl` values.
- **Google sign-in:** I set this up to sign in with Google accounts.
- Building the signed release APK, including creating your key. I also fixed the Java 25 problem by installing Temurin 21 and updating `gradle.properties` and `JAVA_HOME`.

 Notes
- Everything I wrote was checked by careful review, not by compiling it, so your own testing is what proves it works.
- Back up your `.jks` key and its passwords. Without them you can't release updates under the same key.

Claude AI helped with the Java 25 problem and the firebase authentication.
