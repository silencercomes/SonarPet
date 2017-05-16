dependencies {
    compileOnly("org.bukkit:bukkit:1.9-R0.1-SNAPSHOT") // Override the version of bukkit
    compileOnly("org.bukkit:craftbukkit:1.9-R0.1-SNAPSHOT") {
        exclude(mapOf("module" to "bukkit"))
    }
    compileOnly(project(":api"))
}
