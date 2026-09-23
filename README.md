Report: How the Travlixto App Was Built

Travlixto was built as a native Android application using Kotlin and Jetpack Compose, Google's modern declarative UI toolkit. Starting from the provided mockups — Splash, Onboarding, Authentication, Home, Details, Schedule, Search, Popular Places, and Profile — each screen was implemented as an individual Compose function, keeping the visual structure and cyan brand color consistent throughout.

- Architecture
The project follows a simple MVVM-style structure. UI screens live under ui/screens, each one a stateless Composable that receives callbacks for navigation. A single AuthViewModel manages authentication state (idle, loading, success, error) and exposes it to the Login, Sign Up, and Forgot Password screens via Compose state. Navigation between all screens, including the bottom tab bar for Home, Schedule, Search, Popular Places, and Profile, is handled by Jetpack Navigation Compose in one central NavGraph file.

- Backend: Firebase
Rather than building a custom server, the app uses Firebase as its backend. Firebase Authentication handles account creation, sign-in, and password reset with email and password. Cloud Firestore, a NoSQL document database, stores two collections: users, holding each person's profile (name, location, mobile number, reward points, trip counts), and destinations, holding the travel places shown on Home, Popular Places, and Search. Repository classes (AuthRepository, UserRepository, DestinationRepository) wrap all Firebase calls behind clean suspend functions, so the UI layer never talks to Firebase directly.

- Retrofit Layer
A Retrofit client was added separately for any REST API outside Firebase's scope — for example, a future weather or third-party travel data integration. It is configured with a Gson converter and an OkHttp logging interceptor, ready to point at a real endpoint when one is chosen.

- Build & Delivery
The project is a standard Gradle-based Android Studio project (Kotlin DSL build files, Compose BOM 2024.06, Firebase BOM 33.1.2). It was authored and organized in a development sandbox without Android SDK or emulator access, so final compilation, testing, and signing must be completed in Android Studio. Once verified, the app is packaged as a signed APK or Android App Bundle for distribution via internal testing or the Google Play Store; Firebase itself requires no separate hosting, as Authentication and Firestore are fully managed cloud services.


- AI tools used
I used Claude AI to help with the firebase Authenticator set up and to understand Retrofit API since I’ve never used either properly. They gave a good understanding and how to implement and where I went wrong
I also used W3Schools for Kotlin reference and tutorials on new features to help make the app.

- Design
•	Persistent bottom navigation: Home, Schedule, Search, Popular Places, and Profile are always one tap away via a fixed bottom tab bar, rather than hidden inside a menu — reducing the number of steps needed to reach any core feature.
•	Visible system feedback: authentication screens show a loading spinner while a request is in progress and a clear inline error message if it fails, so the user is never left uncertain about whether an action succeeded.
•	Minimal, single-purpose screens: each onboarding and authentication screen asks for only the information needed at that step (for example, just email and password to sign in), lowering cognitive load and making each screen quick to complete.
•	Consistent visual language: a single accent color (cyan) is reused across every primary button, active tab, and highlighted text, training the user to recognize it as "the next action" throughout the app.
•	Graceful empty states: screens like Home and Schedule show a plain-language message (e.g. "No trips scheduled yet") instead of a blank layout, so new users understand the state of the app rather than assuming something is broken.

- Github
•	A GitHub repository was created to hold the project remotely.
•	The local project folder was initialized with git init, turning it into a tracked Git repository. (I did this with: https://git-scm.com/)
•	Changes are staged with git add . and saved as a commit using git commit -m "message", with each commit message describing what changed (e.g. "Add Firestore search query", "Fix profile edit save bug").
•	The local repository is linked to GitHub with git remote add origin <repo-url>, then pushed with git push -u origin main.
•	Ongoing development follows the same add → commit → push cycle in small, logical increments rather than large infrequent commits, which keeps the project history easy to read and makes it simple to identify exactly when and why a change was introduced.


- YOUTUBE
https://youtu.be/Vbiy9e2k5Kk
this the link to the video
