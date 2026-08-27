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

## Notify for all messages in a channel

By default, Stoat only pushes/notifies you for DMs and @mentions - a regular
message in a shared channel never triggers a notification, on the official
app or this fork alike. This fork adds an opt-in per-channel override: open
a channel's context menu and choose **"Notify for all messages"** to be
notified for every message there, or **"Use default notifications"** to go
back to mentions-only.

This needs a matching backend change to actually take effect - it won't do
anything against the official stoat.chat servers, or any self-hosted
instance that hasn't applied the same patch. See
[`PsychoViking46/stoatchat`](https://github.com/PsychoViking46/stoatchat),
branch `add-per-channel-notify-all` (based on the `v0.14.3` tag), which adds:

- A `notification_level` field on the per-user, per-channel unread record
- A new `PUT /channels/<id>/notifications` endpoint to set/clear it
  (`{"level": "all"}` or `{"level": null}`)
- The corresponding change to the message-send push logic, so a channel
  member with `"all"` set gets pushed for every message there, on top of
  the existing DM/mention/mass-mention triggers (which are unchanged)

## Setting up push notifications for your own instance

Push notifications work end-to-end in the release build, but they're tied to
the Firebase project that build was compiled against. If you're running your
own self-hosted Stoat server, everything else (messaging, voice, etc.) works
immediately - push just won't be delivered until you set up your own
Firebase project and point both this app and your server's `pushd` at it.
None of this costs anything (Firebase's free tier covers this comfortably).

1. **Create a Firebase project** at [console.firebase.google.com](https://console.firebase.google.com) -
   name doesn't matter, disable Google Analytics (not needed).
2. **Register the Android app** in it with package name `chat.revolt`,
   download the resulting `google-services.json`, and drop it into `app/google-services.json`
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

## Self-signed / internally-signed certificates

If your self-hosted instance uses a certificate from your own internal CA
(or a self-signed one) rather than one from a public CA like Let's Encrypt,
the app will refuse to connect out of the box - Android apps targeting API
24+ don't trust user-installed certificates by default. This fork adds a
`network_security_config.xml` `base-config` that trusts the device's
`system` **and** `user` certificate stores, so it'll work once you've
installed your CA (or the server's own cert) as a trusted certificate on
the device (Settings → Security → Encryption & credentials → Install a
certificate, wording varies by Android version/OEM).

**Worth understanding before you rely on this:** trusting `user` certs is a
device-wide, app-wide setting, not scoped to just your server. Android
disables it by default specifically to prevent MITM attacks - e.g. a
malicious "sign in for free WiFi" captive portal getting a user to install
a rogue cert, or a compromised MDM/work profile. Turning it back on here
means that if a rogue CA ever lands in your device's trusted store for any
reason, this app would trust connections through it too, not just for your
own server. This is scoped to `base-config` only, so it does *not* weaken
the pinned certificate trust used for `stoatusercontent.com` (the CDN
domain keeps its own separate, pinned trust-anchors). If you're not
deliberately running your own CA/self-signed cert, there's no need to
install anything and this doesn't change your risk at all.

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
