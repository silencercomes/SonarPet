package net.techcable.sonarpet.versioning

import net.techcable.sonarpet.test.*
import net.techcable.sonarpet.utils.mapToArray
import net.techcable.sonarpet.versioning.PluginVersionInfo.BuildType.*
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PluginVersioningTest(private val version: PluginVersionInfo, private val expected: PluginVersionInfo?) {

    @Test
    fun testValidity() {
        assertThat(version.version.isNotBlank())
        if (expected != null) {
            assertEqual(expected.release, version.release) { "Expected release ${expected.release}, but got $it" }
            assertEqual(expected.buildType, version.buildType) { "Expected buildType ${expected.buildType}, but got $it" }
            assertEqual(expected.commit, version.commit) { "Expected commit ${expected.commit}, but got $it" }
            assertEqual(expected, version) { "Expected version $expected, but got $it" }
        }
        if (version.isKnown) {
            val release = assertNotNull(version.release) { "Null release for $version" }
            assertMatches(Regex("""[\w.]+(-(?:alpha|beta)\d+)?"""), release) { "Invalid release: ${version.release}" }
        }
    }

    @Test
    fun testDevBuild() {
        assumeThat(version.isDevelopment)
        assertNotNull(version.commit) { "Null commit for dev build $version" }
    }

    @Test
    @Category(SlowTest::class) // Don't worry, gradle disables this even though we don't use the Category runner
    fun testVersionComparison() {
        assumeThat(version.isDevelopment)
        version.compareToRepo(
                repo = PluginVersioning.REPO,
                branch = PluginVersioning.BRANCH
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testData(): Array<Array<out PluginVersionInfo?>> {
            return arrayOf(
                    *arrayOf(
                            this.findVersionSignature() to null,
                            "1.0.0" to arrayOf("1.0.0", RELEASE),
                            "1.1.0-alpha1" to arrayOf("1.1.0-alpha1", RELEASE),
                            "1.1.0-beta2" to arrayOf("1.1.0-beta2", RELEASE),
                            "1.1.0-alpha2-dev-0980ec76" to arrayOf("1.1.0-alpha2", DEV, "0980ec76"),
                            "1.1.0-alpha3-dirty-bd21743c" to arrayOf("1.1.0-alpha3", DIRTY, "bd21743c")
                    ).mapToArray { (version, expectedValues) ->
                        arrayOf(PluginVersionInfo.parse("SonarPet", version), (if (expectedValues != null) PluginVersionInfo(
                                pluginName = "SonarPet",
                                version = version,
                                release = expectedValues[0] as String,
                                buildType = expectedValues[1] as PluginVersionInfo.BuildType,
                                commit = expectedValues.getOrNull(2) as String?
                        ) else null))
                    },
                    *arrayOf(
                            ("SonarPet-custom" to "non-conformant-version") to arrayOf(null, UNKNOWN),
                            ("not-even-related" to "1.0.0-dev-0980ec76") to arrayOf("1.0.0", DEV, "0980ec76")
                    ).mapToArray { (id, expectedValues) ->
                        val (pluginName, version) = id
                        arrayOf(PluginVersionInfo.parse(pluginName, version), PluginVersionInfo(
                                pluginName = pluginName,
                                version = version,
                                release = expectedValues[0] as String?,
                                buildType = expectedValues[1] as PluginVersionInfo.BuildType,
                                commit = expectedValues.getOrNull(2) as String?
                        ))
                    }
            )
        }

        @JvmStatic
        private fun findVersionSignature(): String {
            // This is passed as a system property, since we don't have access to plugin.yml
            return System.getProperty("sonarpet.versionSignature")!!
        }
    }
}