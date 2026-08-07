<div align="center">
    <h1>Stoat for Android</h1>
    <p>Official <a href="https://stoat.chat">Stoat</a> Android app.</p>
    <br/><br/>
    <div>
        <a href="https://play.google.com/store/apps/details?id=chat.revolt"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" width="200"></a>
        <br/>
    </div>
    <small>Google Play is a trademark of Google LLC.</small>
    <br/><br/><br/>
</div>

> **This is a fork.** The official app only connects to the official stoat.chat cloud service —
> self-hosted instances aren't supported yet (tracked upstream at
> [stoatchat/self-hosted#141](https://github.com/stoatchat/self-hosted/issues/141)). This fork
> adds a **"Connect to a self-hosted server"** option on the login screen: any user can type in
> their own instance's domain, and the app discovers everything else (API, websocket, file/proxy
> URLs) at runtime via the standard `.well-known/stoat` discovery convention — no server is
> hardcoded, no rebuild needed. See
> [`CustomInstance.kt`](app/src/main/java/chat/stoat/persistence/CustomInstance.kt) for the
> implementation. Defaults to the official instance until a user opts in. Not affiliated with or
> endorsed by the Stoat project.

## Setting up push notifications for your own instance

Push notifications work end-to-end in the release build, but they're tied to
the Firebase project that build was compiled against. If you're running your
own self-hosted Stoat server, everything else (messaging, voice, etc.) works
immediately - push just won't be delivered until you set up your own
Firebase project and point both this app and your server's `pushd` at it.
None of this costs anything (Firebase's free tier covers this comfortably).

1. **Create a Firebase project** at [console.firebase.google.com](https://console.firebase.google.com) -
   name doesn't matter, disable Google Analytics (not needed).
2. **Register the Android app** in it with package name `chat.revolt` (or
   `chat.revolt.debug` if you only want push on debug builds), download the
   resulting `google-services.json`, and drop it into `app/google-services.json`
   before building.
3. **Enable the Firebase Cloud Messaging API** for the project in
   [Google Cloud Console](https://console.cloud.google.com) (APIs & Services
   → Library) - this isn't always on by default and sends will fail with a
   403 until it is.
4. **Generate a service-account key**: Project settings → Service accounts →
   Generate new private key. Treat this file as a secret - it's admin-level
   credentials for sending push through your project, not something to
   commit or share.
5. **Configure your server's `pushd`**: add a `[pushd.fcm]` section to your
   self-hosted instance's `Revolt.toml` with the fields from that
   service-account JSON:
   ```toml
   [pushd.fcm]
   queue = "notifications.outbound.fcm"
   key_type = "service_account"
   project_id = "<from the JSON>"
   private_key_id = "<from the JSON>"
   private_key = "<from the JSON>"
   client_email = "<from the JSON>"
   client_id = "<from the JSON>"
   auth_uri = "https://accounts.google.com/o/oauth2/auth"
   token_uri = "https://oauth2.googleapis.com/token"
   auth_provider_x509_cert_url = "https://www.googleapis.com/oauth2/v1/certs"
   client_x509_cert_url = "<from the JSON>"
   ```
   Restart `pushd` after adding this. Nothing else in your stack needs to
   change.
6. **Build your own release APK** with the real `google-services.json` in
   place (see Quick Start below) and install it in place of this repo's
   prebuilt one.

## Description

The codebase includes the app itself, as well as an internal library for interacting with the Stoat
API. The app is written in Kotlin, and wholly
uses [Jetpack Compose](https://developer.android.com/jetpack/compose).

## Stack

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
    - For some Material components, the View-based
      [Material Components Android](https://github.com/material-components/material-components-android)
      (MDC-Android) library is used.
- [Ktor](https://ktor.io/)
- [Dagger](https://dagger.dev/) with [Hilt](https://dagger.dev/hilt/)

## Resources

### Stoat for Android

- [Roadmap](https://op.revolt.wtf/projects/revolt-for-android/work_packages)
- [Stoat for Android Technical Documentation](https://revoltchat.github.io/android/)
- [Android-specific Contribution Guide](https://revoltchat.github.io/android/contributing/guidelines/)
  &mdash;**read carefully before contributing!**

### Stoat

- [Stoat Project Board](https://github.com/revoltchat/revolt/discussions) (Submit feature requests
  here)
- [Stoat Development Server](https://app.revolt.chat/invite/API)
- [Stoat Server](https://app.revolt.chat/invite/Testers)
- [General Stoat Contribution Guide](https://developers.revolt.chat/contrib.html)

## Quick Start

Open the project in Android Studio. You can then run the app on an emulator or a physical device by
running the `app` module.

In-depth setup instructions can be found
at [Setting up your Development Environment](https://revoltchat.github.io/android/contributing/setup/)
