package agreement.vote_storage

import data.internal.crypto.Hash

/**
 * Outcome of the first phase of the agreement
 */
sealed class FirstPhaseOutcome {
    /**
     * Means that some hash was agreed
     * @param superMajority - commit certificate(proof)
     * @param hash - committed hash
     */
    class Commit(val superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes, val hash: Hash) :
        FirstPhaseOutcome()

    /**
     * Means that no one in the network had collected a [Commit]
     * @param superMajority - reject certificate
     */
    class Reject(val superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes) : FirstPhaseOutcome()

    /**
     * Means that another peer may collect commit or reject
     * @param superMajority - supermajority votes which shows that second phase is required
     */
    class Undecided(val superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes, val frequentHash: Hash) :
        FirstPhaseOutcome()
}

/**
 * Outcome of second phase of the agreement
 */
sealed class SecondPhaseOutcome {
    /**
     * Same as [FirstPhaseOutcome.Commit]
     */
    class Commit(val superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes, val hash: Hash) :
        SecondPhaseOutcome()

    /**
     * Same as [FirstPhaseOutcome.Reject]
     */
    class Reject(val superMajority: VoteStorageInsertionOutcome.SuperMajorityVotes) : SecondPhaseOutcome()
}

