package data.internal

/**
 * States of agreement phase of the consensus
 */
sealed class ConsensusTransition {
    /**
     * Same value which voted is committed
     */
    class CommitSame

    /**
     * Committed value which peer din't vote
     */
    class CommitAnother

    /**
     * Peers reject any value for storing
     */
    class Reject

    /**
     * Committed empty value
     */
    class CommitEmpty

    /**
     * Finalized value of future agreementRound corresponding to voted
     */
    class RequireSynchronization
}
