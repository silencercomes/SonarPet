package net.techcable.sonarpet.versioning

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class PluginVersionInfo(
        val pluginName: String,
        val version: String,
        val release: String? = null,
        val buildType: BuildType = BuildType.UNKNOWN,
        val commit: String? = null
) {
    init {
        if (isKnown) {
            require(isRelease || (commit != null && commit.matches(Regex("\\w+")))) {
                "Invalid commit for dev build: $commit"
            }
        }
    }
    enum class BuildType {
        RELEASE,
        DEV,
        DIRTY,
        UNKNOWN
    }
    val isRelease: Boolean
        get() = buildType == BuildType.RELEASE
    val isDevelopment: Boolean
        get() = isKnown && !isRelease
    val isDirty: Boolean
        get() = buildType == BuildType.DIRTY
    val isKnown: Boolean
        get() = buildType != BuildType.UNKNOWN

    override fun toString(): String {
        return "$pluginName $version"
    }

    @Throws(VersioningException::class, IOException::class)
    fun compareToRepo(repo: String, branch: String): VersionDifference {
        if (!isKnown) throw UnknownVersionException("Unknown version: $version")
        check(isDevelopment) { "Can't compare a release build against $repo/$branch!" }
        val connection = URL("https://api.github.com/repos/$repo/compare/$branch...$commit").openConnection() as HttpURLConnection
        connection.connect()
        if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw UnknownVersionException("Unknown commit $commit for $this in $repo")
        }
        connection.inputStream.bufferedReader().use {
            try {
                val obj = JsonParser().parse(it).asJsonObject
                val status = obj["status"].asJsonPrimitive.asString
                val aheadBy = obj["ahead_by"].asJsonPrimitive.asNumber.toInt()
                val behindBy = obj["behind_by"].asJsonPrimitive.asNumber.toInt()
                val difference = VersionDifference(aheadBy, behindBy)
                check(VersionDifference.Type.valueOf(status.toUpperCase()) == difference.type)
                return difference
            } catch (e: Throwable) {
                // NOTE: IllegalStateException is what 'asJsonObject' and friends throw on failure
                if (e is IllegalStateException || e is NumberFormatException || e is JsonParseException) {
                    throw VersioningException("Unable to compare version $commit to $repo/$branch", e)
                } else {
                    throw e // Propagate
                }
            }
        }
    }

    companion object {
        private val PATTERN = Regex("""([\w.]+(?:-\w+\d+)?)(?:-(dev|dirty)-(\w+))?""")
        @JvmStatic
        fun parse(pluginName: String, version: String): PluginVersionInfo {
            return PATTERN.matchEntire(version)?.let { match ->
                val (release, rawBuildType, rawCommit) = match.destructured
                val buildType = if (rawBuildType.isEmpty()) {
                    BuildType.RELEASE
                } else {
                    try {
                        BuildType.valueOf(rawBuildType.toUpperCase())
                    } catch (e: IllegalArgumentException) {
                        BuildType.UNKNOWN
                    }
                }
                val commit = when {
                    buildType == BuildType.UNKNOWN -> null
                    rawCommit.isNotEmpty() -> rawCommit
                    else -> null
                }
                require(release.isNotBlank()) { "Invalid release: $release" }
                PluginVersionInfo(
                        pluginName = pluginName,
                        version = version,
                        release = release,
                        buildType = buildType,
                        commit = commit
                )
            } ?: PluginVersionInfo(
                    pluginName = pluginName,
                    version = version,
                    buildType = BuildType.UNKNOWN
            )
        }
    }
}