package util

fun <T> Collection<T>.getAny(): T {
    return this.iterator().next()
}
