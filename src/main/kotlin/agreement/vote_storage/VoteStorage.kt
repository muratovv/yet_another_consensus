package agreement.vote_storage

import agreement.Supermajority
import data.internal.ConsensusRound
import data.internal.PeerCollection
import data.internal.Vote

sealed class VoteStorageInsertionOutcome {
    /**
     * Class means that supermajority of votes was collected
     */
    class SuperMajorityVotes(val supermajority: Supermajority) : VoteStorageInsertionOutcome()

    /**
     * Object means that storage doesn't have information about peers or enough votes for making a decision
     */
    object Nothing : VoteStorageInsertionOutcome()
}

interface VoteStorage {
    /**
     * Set peers for corresponding round
     * @param round - round for setting
     * @param peers - participants of the round
     */
    fun setPeers(round: ConsensusRound, peers: PeerCollection)

    /**
     * Insert the vote storage
     */
    fun insert(vote: Vote): VoteStorageInsertionOutcome

    /**
     * Inserts votes from the collection
     */
    fun insert(votes: Collection<Vote>): VoteStorageInsertionOutcome
}
