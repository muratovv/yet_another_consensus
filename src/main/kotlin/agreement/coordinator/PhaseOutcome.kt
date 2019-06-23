package agreement.coordinator

import agreement.CommitCertificate
import agreement.RejectCertificate
import agreement.UndecidedCertificate

/**
 * Outcome of the first phase of the agreement
 */
sealed class FirstPhaseOutcome {
    /**
     * Means that some hash was agreed
     */
    class CommitOutcome(val commitCertificate: CommitCertificate) : FirstPhaseOutcome()

    /**
     * Means that no one in the network had collected a [CommitOutcome]
     */
    class RejectOutcome(val rejectCertificate: RejectCertificate) : FirstPhaseOutcome()

    /**
     * Means that another peer may collect commit or reject
     */
    class UndecidedOutcome(val undecidedCertificate: UndecidedCertificate) : FirstPhaseOutcome()
}

/**
 * Outcome of second phase of the agreement
 */
sealed class SecondPhaseOutcome {
    /**
     * Same as [FirstPhaseOutcome.CommitOutcome]
     */
    class CommitOutcome(val commitCertificate: CommitCertificate) :
        SecondPhaseOutcome()

    /**
     * Same as [FirstPhaseOutcome.RejectOutcome]
     */
    class RejectOutcome(val rejectSertificate: RejectCertificate) : SecondPhaseOutcome()
}

