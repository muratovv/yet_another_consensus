package agreement.coordinator

import agreement.*
import mu.KLogging

interface OutcomeConverterInterface {
    fun convertFirstPhase(supermajority: Supermajority): FirstPhaseOutcome
    fun convertSecondPhase(supermajority: Supermajority): SecondPhaseOutcome
}

/**
 * Class provides conversion from [Supermajority] to phase outcomes [FirstPhaseOutcome] or [SecondPhaseOutcome]
 */
class OutcomeConverter(
    private val commitUpCastValidator: OutcomeUpcCastValidator,
    private val rejectUpCastValidator: OutcomeUpcCastValidator,
    private val undecidedUpCastValidator: OutcomeUpcCastValidator
) : OutcomeConverterInterface {
    companion object : KLogging()

    override fun convertFirstPhase(supermajority: Supermajority): FirstPhaseOutcome {

        val commit = CommitCertificate.tryUpCast(commitUpCastValidator, supermajority)
        if (commit != null) {
            return FirstPhaseOutcome.CommitOutcome(commit)
        }

        val reject = RejectCertificate.tryUpCast(rejectUpCastValidator, supermajority)
        if (reject != null) {
            return FirstPhaseOutcome.RejectOutcome(reject)
        }

        val undecided = UndecidedCertificate.tryUpCast(undecidedUpCastValidator, supermajority)!!
        return FirstPhaseOutcome.UndecidedOutcome(undecided)
    }

    override fun convertSecondPhase(supermajority: Supermajority): SecondPhaseOutcome {
        return when (val outcome = convertFirstPhase(supermajority)) {
            is FirstPhaseOutcome.CommitOutcome -> SecondPhaseOutcome.CommitOutcome(
                outcome.commitCertificate
            )
            is FirstPhaseOutcome.RejectOutcome -> SecondPhaseOutcome.RejectOutcome(
                outcome.rejectCertificate
            )
            is FirstPhaseOutcome.UndecidedOutcome -> {
                logger.error { "unexpected undecided in the second phase, return reject" }
                SecondPhaseOutcome.RejectOutcome(
                    RejectCertificate.create(
                        { _, _ -> true },
                        outcome.undecidedCertificate.votes,
                        outcome.undecidedCertificate.peers
                    )!!
                )
            }
        }
    }
}
