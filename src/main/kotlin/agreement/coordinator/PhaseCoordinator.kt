package agreement.coordinator

import agreement.CommitCertificate
import agreement.RejectCertificate
import common.Streaming
import data.internal.ConsensusRound
import data.internal.PeerCollection
import data.internal.crypto.Hash

/**
 * Round input
 */
data class CoordinatorRoundInput(
    val hash: Hash,
    val consensusRound: ConsensusRound,
    val peersCollection: PeerCollection
)

/**
 * Round outcome
 */
sealed class CertificatedYacOutcome {
    class Commit(val commitCertificate: CommitCertificate) : CertificatedYacOutcome()
    class Reject(val rejectCertificate: RejectCertificate) : CertificatedYacOutcome()
}

/**
 * Interface of the phase coordinator which responsible for propagation data among peers in the network
 */
interface PhaseCoordinator : Streaming<CoordinatorRoundInput, CertificatedYacOutcome>
