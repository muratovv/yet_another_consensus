package agreement.coordinator

import data.Proposal
import data.internal.ConsensusRound
import data.internal.crypto.Hasher
import main.pipeline.data.AgreementOutcome
import main.pipeline.data.ConsensusState
import main.pipeline.data.ConsensusTransition

class CoordinatorConverter(private val hasher: Hasher<Proposal>) {
    private var lastState: ConsensusState = ConsensusState(emptyList(), ConsensusRound(0, 0))
    private var lastProposal: Proposal = Proposal()

    fun storeState(consensusState: ConsensusState, proposal: Proposal) {
        lastState = consensusState
        lastProposal = proposal
    }

    fun retrieveOutcome(yacOutcome: CertificatedYacOutcome): ConsensusTransition {
        val outState = ConsensusState(lastState.activePeers, lastState.round)
        when (yacOutcome) {
            is CertificatedYacOutcome.Commit -> {
                val agreedRound =
                    yacOutcome.commitCertificate.attachedAgreementRound.reduceToConsensusRound()
                if (agreedRound > lastState.round) {
                    return ConsensusTransition(
                        AgreementOutcome.RequireSynchronization(agreedRound),
                        outState
                    )
                }

                if (agreedRound < lastState.round) {
                    // impossible situation
                }

                val agreedHash = yacOutcome.commitCertificate.committedHash.first

                if (agreedHash == hasher.empty()) {
                    return ConsensusTransition(AgreementOutcome.CommitEmpty, outState)
                }

                return if (agreedHash == hasher.hash(lastProposal)) {
                    ConsensusTransition(AgreementOutcome.Commit(lastProposal), outState)
                } else {
                    ConsensusTransition(AgreementOutcome.CommitAnother(agreedHash), outState)
                }

            }
            is CertificatedYacOutcome.Reject -> {

                val agreedRound =
                    yacOutcome.rejectCertificate.attachedAgreementRound.reduceToConsensusRound()
                if (agreedRound > lastState.round) {
                    return ConsensusTransition(
                        AgreementOutcome.RequireSynchronization(agreedRound),
                        outState
                    )
                }

                if (agreedRound < lastState.round) {
                    // impossible situation
                }

                return ConsensusTransition(AgreementOutcome.Reject, outState)

            }
        }
    }
}
