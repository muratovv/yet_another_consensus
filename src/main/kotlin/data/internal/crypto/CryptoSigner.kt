package data.internal.crypto

interface CryptoSigner {
    fun sign(hash: Hash): Signature
}
