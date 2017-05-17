package net.techcable.sonarpet.utils.compat

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.google.common.util.concurrent.MoreExecutors

object ModernGuava: GuavaVersion {
    override fun <E> immutableListCollector() = ImmutableList.toImmutableList<E>()!!
    override fun <E> immutableSetCollector() = ImmutableSet.toImmutableSet<E>()!!

    override fun directExecutor() = MoreExecutors.directExecutor()!!
}