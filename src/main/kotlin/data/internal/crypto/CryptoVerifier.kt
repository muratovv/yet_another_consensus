package data.internal.crypto

interface CryptoVerifier {
    fun verify(hash: Hash, signature: Signature): Boolean
}
