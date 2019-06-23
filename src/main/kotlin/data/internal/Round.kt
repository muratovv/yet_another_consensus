package data.internal

/**
 * Phase of the agreement agreementRound
 */
enum class Phase {
    FIRST,
    SECOND,
}

data class ConsensusRound(val commitRound: Long, val attemptRound: Int) {
    fun makeAgreementRound() = AgreementRound(commitRound, attemptRound, Phase.FIRST)
}

data class AgreementRound(val commitRound: Long, val attemptRound: Int, val phase: Phase = Phase.FIRST) {
    operator fun compareTo(agreementRound: AgreementRound): Int {
        val commit = commitRound.compareTo(agreementRound.commitRound)
        val attempt = attemptRound.compareTo(agreementRound.attemptRound)
        val phase = phase.compareTo(agreementRound.phase)
        if (commit != 0) return commit
        if (attempt != 0) return attempt
        if (phase != 0) return phase
        return 0
    }

    fun getNextPhaseRound(): AgreementRound = AgreementRound(commitRound, attemptRound, Phase.SECOND)
}
