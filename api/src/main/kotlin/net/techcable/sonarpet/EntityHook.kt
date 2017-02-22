package net.techcable.sonarpet

@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class EntityHook(
        vararg val value: EntityHookType
)