package net.techcable.sonarpet.versioning

open class VersioningException: Exception {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}
