import groovy.json.JsonOutput
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

import kotlin.io.*
import java.io.File

apply {
    plugin("com.github.johnrengelman.shadow")
}

val versionSignature: String by lazy { rawComputeVersionSignature() }
description = "SonarPet"
dependencies {
    add("shade", project(":core"))
}

val java: JavaPluginConvention
    get() = convention.getPlugin(JavaPluginConvention::class)
val processResources: AbstractCopyTask by tasks
val shadowJar: ShadowJar by tasks

shadowJar.apply {
    baseName = "SonarPet"
    classifier = null
    version = null
    // Shade only dependencies marked as "shade", transitively
    configurations = listOf(project.configurations["shade"])
    dependencies {
        /*
         * Since shade is transitive, gradle will usually add all sub-dependencies,
         * even if they're not explicitly marked as shade.
         * We override this behavior by searching shaded dependencies ourselves,
         * and only using the ones the dependency itself specifies as "shade"
         */
        val shadeDependencyIds = findDeclaredDependenciesRecursively(project, "shade").map { it.dependencyId }.toSet()
        exclude { !shadeDependencyIds.contains(it.dependencyId) }
    }
    // Relocate ASM so it doesn't conflict with Paper
    relocate("org.objectweb.asm", "net.techcable.sonarpet.libs.asm")
    tasks["build"].dependsOn(this)
}

processResources.apply {
    filesMatching("plugin.yml") {
        filter {
            // NOTE: We use our 'version signature' with the git commit included
            it.replace("\${project.versionSignature}", versionSignature)
        }
    }
}

// If the versionSignature changes, we must processResources again
processResources.inputs.property("versionSignature", versionSignature)


val generatedResources = File("$buildDir/main/generated-resources")
task("generateResources") {
    doLast {
        val dependencies = findDependenciesRecursively(project.configurations.compile.resolvedConfiguration).toMutableSet()
        val shadeDependencyIds = findDeclaredDependenciesRecursively(project, "shade").map { it.dependencyId }.toSet()
        // Remove any dependencies that are already shaded in
        dependencies.removeAll { it.dependencyId in shadeDependencyIds }
        writeDependencyInfo(dependencies)
    }
}

for (sourceSet in listOf("main", "test").map { java.sourceSets[it] }) {
    sourceSet.output.dir(mapOf("builtBy" to tasks["generateResources"]), generatedResources)
}


fun rawComputeVersionSignature(): String {
    val version = project.version.toString()
    if (!version.endsWith("-SNAPSHOT")) {
        // If it's not a snapshot version, there's not much to do
        return version + "-release"
    }
    val versionBase = version.replace("-SNAPSHOT", "")
    // Determine the current git commit
    // Determine if there are uncommitted changes
    var prog = ProcessBuilder("git", "status", "--porcelain=1").redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    val isClean = prog.inputStream.reader().use { it.readText().isBlank() }
    check(prog.waitFor() == 0) { "Failed to execute git status!" }
    /*
     * NOTE: Prefer the short option over manual slicing since it handles uniqueness.
     * If we ever run into hash collisions in the first few chars, it'll still work.
     */
    prog     = ProcessBuilder("git", "rev-parse", "--short", "HEAD").redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    val currentCommit = prog.inputStream.reader().use { it.readText().trim() }
    check(prog.waitFor() == 0) { "Failed to execute git rev-parse!" }
    val statusText = if (isClean) "dev" else "dirty"
    return "$versionBase-$statusText-$currentCommit"
}

fun writeDependencyInfo(dependencies: Collection<ResolvedDependency>) {
    val dependencyInfoFile = File(generatedResources, "dependencies.json")
    dependencyInfoFile.parentFile.mkdirs()
    dependencyInfoFile.delete() // Delete existing
    check(dependencyInfoFile.createNewFile()) // Recreate the file
    // Write pretty-printed info
    dependencyInfoFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mapOf(
            "repositories" to listOf(
                    mapOf("name" to "maven-central", "url" to repositories.mavenCentral().url.toString()),
                    mapOf("name" to "techcable-repo", "url" to (repositories.getByName("techcable-repo") as MavenArtifactRepository).url.toString())
            ),
            "dependencies" to dependencies.map { it.name }.sorted()
    ))))
}

fun findDependenciesRecursively(config: ResolvedConfiguration): Set<ResolvedDependency> {
    val result = HashSet<ResolvedDependency>()
    val toResolve = config.firstLevelModuleDependencies.toMutableList()
    toResolve.popAll { dependency ->
        if (result.add(dependency)) {
            toResolve.addAll(dependency.children)
        }
    }
    return result
}

fun findDeclaredDependenciesRecursively(project: Project, configurationType: String):  Set<ResolvedDependency> {
    val result = HashMap<String, ResolvedDependency>()
    val toResolve = mutableListOf(project)
    toResolve.popAll { proj ->
        val originalDependencies = proj.configurations[configurationType].dependencies
        proj.configurations[configurationType]?.resolvedConfiguration?.firstLevelModuleDependencies?.forEach {
            val dependencyId = it.dependencyId
            result[dependencyId] = it
            val originalDependency = originalDependencies.find { it.dependencyId == dependencyId }
            check(originalDependency != null) { "Couldn't find originalDependency for $dependencyId in $proj" }
            if (originalDependency is ProjectDependency) {
                toResolve.add(originalDependency.dependencyProject)
            }
        }
    }
    return result.values.toSet()
}

/**
 * Treating the list as a stack of elements,
 * keep poping the last element off until it's empty.
 *
 * The list is safe to modify from within the closure,
 * and will not throw a CME while being iterated over.
 */
inline fun <T> MutableList<T>.popAll(consumer: (T) -> Unit) {
    while (this.isNotEmpty()) {
        consumer(this.removeAt(this.lastIndex))
    }
}

val Dependency.dependencyId: String
    get() = "$group:$name:$version"
val ResolvedDependency.dependencyId: String
    get() = "$moduleGroup:$moduleName:$moduleVersion"
