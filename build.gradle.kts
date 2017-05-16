import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.junit.JUnitOptions

buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath("org.ow2.asm:asm-all:5.2") // Used to check for guava compat
        classpath(kotlinModule("gradle-plugin"))
    }
}
plugins {
    id("java")
    id("maven")
    id("org.jetbrains.kotlin.jvm").version("1.1.2")
    id("com.github.johnrengelman.shadow").version("1.2.4").apply(false)
}
allprojects {
    apply {
        plugin("maven")
    }

    group = "net.techcable.sonarpet"
    version = "1.1.0-alpha2-SNAPSHOT"
}


subprojects {
    apply {
        plugin("java")
        plugin("kotlin")
    }
    configurations {
        create("shade") {
            description = "Shade into the output jar"
            isTransitive = true
        }
        "compile" {
            extendsFrom(configurations["shade"])
        }
    }
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    tasks.withType<Test> {
        maxParallelForks = 4
        useJUnit()
        if (!hasProperty("runSlowTests") && System.getProperty("user.name") != "jenkins") {
            (options as JUnitOptions).excludeCategories("net.techcable.sonarpet.test.SlowTest")
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "techcable-repo"
            setUrl("https://repo.techcable.net/content/groups/public/")
        }
        maven { setUrl("https://hub.spigotmc.org/nexus/content/groups/public/") }
        maven { setUrl("http://repo.dmulloy2.net/content/groups/public/") }
        maven { setUrl("http://repo.md-5.net/content/groups/public/") }
        maven { setUrl("http://ci.hawkfalcon.com/plugin/repository/everything/") }
        maven { setUrl("http://maven.sk89q.com/repo/") }
        maven { setUrl("http://repo.kitteh.org/content/groups/public") }
        maven { setUrl("http://nexus.theyeticave.net/content/repositories/pub_releases") }
        maven { setUrl("http://maven.elmakers.com/repository/") }
    }

    dependencies {
        testCompile("junit:junit:4.12")
        testCompile("com.googlecode.junit-toolbox:junit-toolbox:2.3")
        compile("net.techcable:pineapple:0.1.0-beta5")
        compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:1.1.2")
        compile("com.dsh105:Commodus:1.0.6") {
            exclude(module = "Minecraft-Reflection")
        }
        compile("com.dsh105:PowerMessage:1.0.1-SNAPSHOT") {
            exclude(module = "Commodus")
        }
        compile("org.slf4j:slf4j-jdk14:1.7.5")
        compile("com.zaxxer:HikariCP:2.4.5")
        /*
         * An outdated version of ASM is used by Paper.
         * We need to shade in our (modern) version,
         * or else we'll use Paper's outdated version and crash.
         * The bootstrap loader also depends on ASM to automatically relocate dependencies too,
         * since we need to keep our own seperate version of guava and other things.
         */
        "shade"("org.ow2.asm:asm:5.2")
        "shade"("org.ow2.asm:asm-commons:5.2")
        "shade"("org.ow2.asm:asm-util:5.2")
        // Provided dependencies
        compileOnly("org.bukkit:bukkit:1.11.2-R0.1-SNAPSHOT")
        compileOnly("org.projectlombok:lombok:1.16.12")
        compileOnly("org.kitteh:VanishNoPacket:3.18.7") {
            exclude(module = "Vault")
        }
        compileOnly("com.sk89q:worldedit:6.0.0-SNAPSHOT")
        compileOnly("com.sk89q:worldguard:6.0.0-SNAPSHOT")
        compileOnly("com.comphenix.protocol:ProtocolLib:3.6.5-SNAPSHOT")
    }

    // All test modules depend on the API's test module
    if (name != "api") {
        val apiTests = project(":api").convention.getPlugin(JavaPluginConvention::class).sourceSets["test"].output
        dependencies {
            testCompile(apiTests)
        }
    }
}
