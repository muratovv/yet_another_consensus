package main.pipeline.data

import data.Proposal
import data.internal.ConsensusRound
import data.internal.crypto.Hash

data class ConsensusTransition(val agreedOn: AgreementOutcome, val roundState: ConsensusState)

/**
 * States of agreement phase of the consensus
 */
sealed class AgreementOutcome {
    /**
     * Same value which voted is committed
     */
    class Commit(val proposal: Proposal) : AgreementOutcome()

    /**
     * Committed value which peer din't vote
     */
    class CommitAnother(val hash: Hash) : AgreementOutcome()

    /**
     * Peers reject any value for storing
     */
    object Reject : AgreementOutcome()

    /**
     * Committed empty value
     */
    object CommitEmpty : AgreementOutcome()

    /**
     * Finalized value of future agreementRound corresponding to voted
     */
    class RequireSynchronization(val newRound: ConsensusRound) : AgreementOutcome()
}
