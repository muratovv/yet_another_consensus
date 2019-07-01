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

    operator fun compareTo(consensusRound: ConsensusRound): Int {
        val commit = commitRound.compareTo(consensusRound.commitRound)
        val attempt = attemptRound.compareTo(consensusRound.attemptRound)
        if (commit != 0) return commit
        if (attempt != 0) return attempt
        return 0
    }
}

data class AgreementRound(val commitRound: Long, val attemptRound: Int, val phase: Phase = Phase.FIRST) {
    operator fun compareTo(agreementRound: AgreementRound): Int {
        val consensusRound = reduceToConsensusRound().compareTo(agreementRound.reduceToConsensusRound())
        val phase = phase.compareTo(agreementRound.phase)
        if (consensusRound != 0) return consensusRound
        if (phase != 0) return phase
        return 0
    }

    fun getNextPhaseRound(): AgreementRound = AgreementRound(commitRound, attemptRound, Phase.SECOND)

    fun reduceToConsensusRound() = ConsensusRound(commitRound, attemptRound)
}
