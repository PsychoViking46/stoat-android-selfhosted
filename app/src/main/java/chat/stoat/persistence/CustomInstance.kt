package chat.stoat.persistence

import chat.stoat.api.StoatHttp
import chat.stoat.api.routes.misc.getRootRoute
import chat.stoat.core.model.data.OFFICIAL_STOAT_BASE
import chat.stoat.core.model.data.OFFICIAL_STOAT_FILES
import chat.stoat.core.model.data.OFFICIAL_STOAT_PROXY
import chat.stoat.core.model.data.OFFICIAL_STOAT_WEBSOCKET
import chat.stoat.core.model.data.OFFICIAL_STOAT_WEB_APP
import chat.stoat.core.model.data.STOAT_BASE
import chat.stoat.core.model.data.STOAT_FILES
import chat.stoat.core.model.data.STOAT_PROXY
import chat.stoat.core.model.data.STOAT_WEBSOCKET
import chat.stoat.core.model.data.STOAT_WEB_APP
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

private const val KEY_BASE = "customInstance.base"
private const val KEY_FILES = "customInstance.files"
private const val KEY_PROXY = "customInstance.proxy"
private const val KEY_WEB_APP = "customInstance.webApp"
private const val KEY_WEBSOCKET = "customInstance.websocket"
private const val KEY_DOMAIN = "customInstance.domain" // display-only, what the user typed in

@Serializable
data class StoatWellKnown(val api: String)

/**
 * Loads a previously-saved self-hosted instance (if any) and applies it, overriding the
 * official-server defaults. Must be called once at app startup before any network request goes
 * out, since STOAT_BASE etc. are read fresh on every call but aren't reactive - nothing will
 * re-request in-flight work if this runs late.
 */
suspend fun loadCustomInstance(kvStorage: KVStorage) {
    val base = kvStorage.get(KEY_BASE) ?: return
    STOAT_BASE = base
    STOAT_FILES = kvStorage.get(KEY_FILES) ?: STOAT_FILES
    STOAT_PROXY = kvStorage.get(KEY_PROXY) ?: STOAT_PROXY
    STOAT_WEB_APP = kvStorage.get(KEY_WEB_APP) ?: STOAT_WEB_APP
    STOAT_WEBSOCKET = kvStorage.get(KEY_WEBSOCKET) ?: STOAT_WEBSOCKET
}

suspend fun currentCustomInstanceDomain(kvStorage: KVStorage): String? {
    return kvStorage.get(KEY_DOMAIN)
}

suspend fun resetToOfficialInstance(kvStorage: KVStorage) {
    kvStorage.remove(KEY_BASE)
    kvStorage.remove(KEY_FILES)
    kvStorage.remove(KEY_PROXY)
    kvStorage.remove(KEY_WEB_APP)
    kvStorage.remove(KEY_WEBSOCKET)
    kvStorage.remove(KEY_DOMAIN)

    STOAT_BASE = OFFICIAL_STOAT_BASE
    STOAT_FILES = OFFICIAL_STOAT_FILES
    STOAT_PROXY = OFFICIAL_STOAT_PROXY
    STOAT_WEB_APP = OFFICIAL_STOAT_WEB_APP
    STOAT_WEBSOCKET = OFFICIAL_STOAT_WEBSOCKET
}

/**
 * Points the client at a self-hosted Stoat instance given just its domain (e.g.
 * "stoat.example.com", with or without a scheme). Follows the same discovery convention the
 * self-hosted deployment scaffold documents: /.well-known/stoat for the real API base, then the
 * API's own root response for everything else (websocket, file/autumn, proxy/january URLs) -
 * nothing here is guessed or hardcoded to any particular self-hoster's path layout.
 *
 * On success, persists the discovered config so it survives app restarts, and leaves the client
 * pointed at the new instance. On failure, rolls back to whatever was active before the attempt
 * and rethrows so the caller can show an error.
 */
suspend fun connectToCustomInstance(kvStorage: KVStorage, domainInput: String) {
    val domain = domainInput
        .trim()
        .removePrefix("https://")
        .removePrefix("http://")
        .removeSuffix("/")

    require(domain.isNotEmpty()) { "Enter a server address" }

    val previousBase = STOAT_BASE
    val previousFiles = STOAT_FILES
    val previousProxy = STOAT_PROXY
    val previousWebApp = STOAT_WEB_APP
    val previousWebsocket = STOAT_WEBSOCKET

    try {
        val wellKnown: StoatWellKnown =
            StoatHttp.get("https://$domain/.well-known/stoat").body()

        STOAT_BASE = wellKnown.api

        val root = getRootRoute()

        STOAT_WEBSOCKET = root.ws
        STOAT_FILES = root.features.autumn.url
        STOAT_PROXY = root.features.january.url
        STOAT_WEB_APP = root.app

        kvStorage.set(KEY_BASE, STOAT_BASE)
        kvStorage.set(KEY_FILES, STOAT_FILES)
        kvStorage.set(KEY_PROXY, STOAT_PROXY)
        kvStorage.set(KEY_WEB_APP, STOAT_WEB_APP)
        kvStorage.set(KEY_WEBSOCKET, STOAT_WEBSOCKET)
        kvStorage.set(KEY_DOMAIN, domain)
    } catch (e: Exception) {
        STOAT_BASE = previousBase
        STOAT_FILES = previousFiles
        STOAT_PROXY = previousProxy
        STOAT_WEB_APP = previousWebApp
        STOAT_WEBSOCKET = previousWebsocket
        throw e
    }
}
