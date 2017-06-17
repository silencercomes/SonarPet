package net.techcable.sonarpet.utils

/**
 * A [Collection] that contains no duplicates,
 * and can be used as a [Set].
 */
class UniqueCollection<out T>(
        private val handle: Collection<T>
): Set<T> {
    override fun iterator(): Iterator<T> {
        return DuplicateCheckingIterator(
                delegate = handle.iterator(),
                expectedSize = handle.size
        )
    }
    private class DuplicateCheckingIterator<out T>(
            private val delegate: Iterator<T>,
            expectedSize: Int
    ): Iterator<T> {
        private val seenElements: MutableSet<T> = HashSet(((expectedSize / 0.75) + 1).toInt())
        override fun hasNext() = delegate.hasNext()

        override fun next(): T {
            val element = delegate.next()
            if (!seenElements.add(element)) {
                throw IllegalStateException("Duplicate elements: $element")
            }
            return element
        }
    }

    // Plain delegates
    override fun isEmpty() = handle.isEmpty()
    override val size: Int
        get() = handle.size
    override fun contains(element: @UnsafeVariance T) = handle.contains(element)
    override fun containsAll(elements: Collection<@UnsafeVariance T>) = handle.containsAll(elements)
}
