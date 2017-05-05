package net.techcable.sonarpet.maven

import com.google.common.collect.ImmutableList
import com.googlecode.junittoolbox.ParallelRunner
import net.techcable.sonarpet.test.*
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.IOException
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

private val TECHCABLE_REPOSITORY = MavenRepository.create("techcable-repo", URL("https://repo.techcable.net/content/groups/public"))
private val TEST_ARTIFACTS = ImmutableList.of(
        MavenArtifact.createJar("com.google.guava", "guava", "21.0"),
        MavenArtifact.createJar("org.ow2.asm", "asm", "5.1")
)
private val TEST_SNAPSHOT_ARTIFACTS = ImmutableList.of(
        MavenArtifact.createJar("net.techcable", "event4j", "1.1.0-SNAPSHOT"),
        MavenArtifact.createJar("net.techcable", "srghelper", "1.0.0-SNAPSHOT"),
        MavenArtifact.createJar("com.dsh105", "PowerMessage", "1.0.1-SNAPSHOT"),
        MavenArtifact.createJar("net.techcable.npclib", "nms", "2.0.0-beta2-SNAPSHOT"),
        MavenArtifact.createJar("net.techcable", "npclib", "2.0.0-beta2-SNAPSHOT")
)

@RunWith(ParallelRunner::class)
class MavenRepositoryTest {
    private val tempLocal = LocalRepository.createTemp("test-repo")
    companion object {
        var mavenRepositoryLogger: Logger? = null
        @BeforeClass
        @JvmStatic
        fun enableLogging() {
            val logger = Logger.getLogger(MavenRepository::javaClass.name)
            logger.level = Level.FINE
            // Hold on to it, so the settings persist
            mavenRepositoryLogger = logger
        }
    }

    @Test
    @Category(SlowTest::class)
    fun testResolveArtifacts() {
        for (artifact in TEST_ARTIFACTS) {
            MavenRepository.central().assertValidArtifact(artifact)
        }
    }

    @Test
    @Category(SlowTest::class)
    fun testResolveSnapshotArtifacts() {
        for (snapshotArtifact in TEST_SNAPSHOT_ARTIFACTS) {
            assertThat(snapshotArtifact.isSnapshot)
            TECHCABLE_REPOSITORY.assertValidArtifact(snapshotArtifact)
        }
    }

    private fun MavenRepository.assertValidArtifact(artifact: MavenArtifact) {
        assumeNoError<IOException> {
            val resolved = assertNotNull(this.find(artifact)) {
                "Couldn't find $artifact in ${this.name}!"
            }
            // Download to the temporary local repo, and verify it's validity
            assertValidJar(tempLocal.downloadFrom(resolved)) {
                "Invalid jar for $resolved: $it"
            }
        }
    }
}
