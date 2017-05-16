group = "net.techcable.sonarpet"
description = "SonarPet"
val nmsVersions: List<String> by extensions

dependencies {
    "shade"(project(":api"))
    for (version in nmsVersions) {
        "shade"(project(":nms-$version")) {
            exclude(mapOf("module" to "craftbukkit"))
        }
    }
}
