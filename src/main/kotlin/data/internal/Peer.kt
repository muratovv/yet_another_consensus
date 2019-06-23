package data.internal

import data.internal.crypto.PublicKey

data class Peer(val key: PublicKey)

typealias PeerCollection = Collection<Peer>
