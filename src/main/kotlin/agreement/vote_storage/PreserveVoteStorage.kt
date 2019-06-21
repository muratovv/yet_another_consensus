package agreement.vote_storage

import data.internal.Peer
import data.internal.PeersSet
import data.internal.Round
import data.internal.Vote

class PreserveVoteStorage(
    private val majorityChecker: MajorityChecker,
    private val rounds: MutableMap<Round, RoundEntry>
) : VoteStorage {

    override fun setPeers(round: Round, peers: PeersSet) {
        if (rounds[round] != null) {
            assert(rounds[round]!!.peers.isEmpty())
            rounds[round]!!.peers = peers.toSet()
        } else {
            rounds[round] = RoundEntry(peers.toSet(), round)
        }
    }

    override fun insert(vote: Vote): VoteStorageInsertionOutcome {
        val actualRound = vote.round
        if (rounds[actualRound] != null) {
            rounds[actualRound]?.addVote(vote)
        } else {
            rounds[actualRound] = RoundEntry(setOf(), vote.round)
        }

        val entry = rounds[actualRound]!!
        if (majorityChecker.hasOutcome(entry.mostFrequentHash(), entry.votes.size, entry.peers.size)) {
            return VoteStorageInsertionOutcome.SuperMajorityVotes(entry.votes.toList(), entry.peers.toList())
        }
        return VoteStorageInsertionOutcome.Nothing
    }
}


class RoundEntry(var peers: Set<Peer>, private val round: Round) {
    val votes: MutableSet<Vote> = HashSet()

    fun addVote(vote: Vote): Boolean {
        return if (vote.round == round) votes.add(vote) else false
    }

    fun mostFrequentHash(): Int = votes.groupBy { vote -> vote.hash }.map { entry -> entry.value.size }.max()!!
}
