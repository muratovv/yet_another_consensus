package agreement.vote_storage

import data.internal.PeerList
import data.internal.PeersSet
import data.internal.Round
import data.internal.Vote

sealed class VoteStorageInsertionOutcome {
    // TODO 2019-06-22, @muratovv: rework list with set
    class SuperMajorityVotes(val votes: List<Vote>, val peers: PeerList) : VoteStorageInsertionOutcome() {
        fun attachedRound() = votes[0].round
    }

    object Nothing : VoteStorageInsertionOutcome()
}

interface VoteStorage {

    fun setPeers(round: Round, peers: PeersSet)

    fun insert(vote: Vote): VoteStorageInsertionOutcome
}
