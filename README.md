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
