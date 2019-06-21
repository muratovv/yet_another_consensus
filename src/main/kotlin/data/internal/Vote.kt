package data.internal

import data.internal.crypto.Hash
import data.internal.crypto.Signature

data class Vote(val round: Round, val hash: Hash, val signature: Signature)
