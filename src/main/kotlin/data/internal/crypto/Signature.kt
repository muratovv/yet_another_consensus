package data.internal.crypto

import data.internal.Peer

typealias SignatureBlob = String

data class Signature(val peer: Peer, val signatureBlob: SignatureBlob)
