package chat.stoat.core.model.data

// These four are runtime-mutable (not `const`) so a self-hosted instance can be swapped in at
// runtime via Settings > Self-hosted server, without rebuilding the app. Every call site reads
// the current value on each use (string templates and Ktor's defaultRequest{} both re-evaluate
// per-call, they don't get inlined the way `const val` would) - see CustomInstance.kt.
var STOAT_BASE = OFFICIAL_STOAT_BASE
var STOAT_FILES = OFFICIAL_STOAT_FILES
var STOAT_PROXY = OFFICIAL_STOAT_PROXY
var STOAT_WEB_APP = OFFICIAL_STOAT_WEB_APP
var STOAT_WEBSOCKET = OFFICIAL_STOAT_WEBSOCKET

const val OFFICIAL_STOAT_BASE = "https://api.stoat.chat/0.8"
const val OFFICIAL_STOAT_FILES = "https://cdn.stoatusercontent.com"
const val OFFICIAL_STOAT_PROXY = "https://proxy.stoatusercontent.com"
const val OFFICIAL_STOAT_WEB_APP = "https://stoat.chat"
const val OFFICIAL_STOAT_WEBSOCKET = "wss://events.stoat.chat"

const val STOAT_SUPPORT = "https://support.stoat.chat"
const val STOAT_MARKETING = "https://stoat.chat"
const val STOAT_BETA_WEB_APP = "https://beta.stoat.chat"
const val STOAT_INVITES = "https://stt.gg"
const val STOAT_CHANGELOG = "https://changelog.stoat.chat"
