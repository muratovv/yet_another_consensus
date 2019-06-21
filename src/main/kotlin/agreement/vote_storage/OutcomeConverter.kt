package agreement.vote_storage

import data.internal.Vote
import data.internal.crypto.Hash
import mu.KLogging

/**
 * Class provides conversion from [SuperMajorityVotes] to phase outcomes [FirstPhaseOutcome] and [SecondPhaseOutcome]
 */
class OutcomeConverter(private val majorityChecker: MajorityChecker) {
    companion object : KLogging()

    fun convertFirstPhase(superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes): FirstPhaseOutcome {
        val sortedVotes = sortSuperMajority(superMajority)
        val frequent = sortedVotes[0].first
        val all = superMajority.peers.size

        if (majorityChecker.hasCommit(frequent, all)) {
            return FirstPhaseOutcome.Commit(superMajority, sortedVotes[0].second)
        }

        val voted = sortedVotes.fold(0) { acc, pair -> acc + pair.first }
        if (majorityChecker.hasReject(frequent, voted, all)) {
            return FirstPhaseOutcome.Reject(superMajority)
        }
        return FirstPhaseOutcome.Undecided(superMajority, sortedVotes[0].second)
    }

    fun convertSecondPhase(superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes): SecondPhaseOutcome {
        return when (val fetched = convertFirstPhase(superMajority)) {
            is FirstPhaseOutcome.Commit -> {
                SecondPhaseOutcome.Commit(fetched.superMajority, fetched.hash)
            }
            is FirstPhaseOutcome.Reject -> {
                SecondPhaseOutcome.Reject(fetched.superMajority)
            }
            is FirstPhaseOutcome.Undecided -> {
                logger.error { "Undecided has been appeared in second phase" }
                SecondPhaseOutcome.Reject(fetched.superMajority)
            }
        }
    }

    private fun sortSuperMajority(superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes): List<Pair<Int, Hash>> {
        return superMajority.votes
            .groupBy { vote: Vote -> vote.hash }
            .map { entry: Map.Entry<Hash, List<Vote>> -> Pair(entry.value.size, entry.key) }
            .sortedBy { pair -> pair.first }
            .reversed()
    }
}
