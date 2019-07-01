package data.internal.crypto

interface Hasher<T> {
    fun hash(value: T): Hash
    fun empty(): Hash
}
