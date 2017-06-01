package net.techcable.sonarpet.versioning

class VersionDifference(
        val aheadBy: Int,
        val behindBy: Int
) {
    val type: VersionDifference.Type
    init {
        require(aheadBy >= 0 && behindBy >= 0) {
            "Negative aheadBy or behindBy: $aheadBy, $behindBy"
        }
        type = when {
            aheadBy > 0 && behindBy > 0 -> Type.DIVERGED
            aheadBy > 0 -> Type.AHEAD
            behindBy > 0 -> Type.BEHIND
            else -> Type.IDENTICAL
        }
    }
    val isIdentical: Boolean
        get() = this.type == Type.IDENTICAL
    override fun toString(): String {
        return when (type) {
            Type.DIVERGED -> "$behindBy commits behind, $aheadBy commits ahead of"
            Type.AHEAD -> "$aheadBy commits ahead of"
            Type.BEHIND -> "$behindBy commits behind of"
            Type.IDENTICAL -> "identical to"
        }
    }
    enum class Type {
        AHEAD,
        BEHIND,
        DIVERGED,
        IDENTICAL
    }
}