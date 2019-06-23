package data.internal

import data.internal.crypto.Hash
import data.internal.crypto.Signature

data class Vote(val agreementRound: AgreementRound, val hash: Hash, val signature: Signature)


// ------------------------------| Validation |--------------------------------

typealias VoteValidator = (Vote) -> Boolean

fun Collection<Vote>.validate(vararg predicates: VoteValidator): Boolean {
    return this.isNotEmpty() and this.all { predicates.all { function -> function(it) } }
}

class SameRoundPredicate : VoteValidator {
    private var first: Vote? = null
    override operator fun invoke(vote: Vote): Boolean {
        return if (first == null) {
            first = vote
            true
        } else vote.agreementRound == first!!.agreementRound
    }
}

class SameHashPredicate : VoteValidator {
    private var first: Vote? = null
    override fun invoke(vote: Vote): Boolean {
        return if (first == null) {
            first = vote
            true
        } else vote.hash == first!!.hash
    }
}

// ------------------------------| Histograms |--------------------------------

typealias HashHistogramItem = Pair<Hash, Int>

fun Collection<Vote>.getFrequentHash(): HashHistogramItem =
    hashHistogram().reversed()[0]

fun Collection<Vote>.hashHistogram(): List<HashHistogramItem> =
    this.groupBy { vote: Vote -> vote.hash }
        .map { entry: Map.Entry<Hash, List<Vote>> -> Pair(entry.key, entry.value.size) }
        .sortedBy { pair -> pair.second }

// ------------------------------| Creation |----------------------------------

interface VoteFactory {
    fun create(hash: Hash, round: AgreementRound): Vote
}
