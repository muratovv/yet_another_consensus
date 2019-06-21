package data.internal

/**
 * Phase of the agreement round
 */
enum class Phase {
    FIRST,
    SECOND,
}

data class Round(val commitRound: Long, val attemptRound: Int, val phase: Phase = Phase.FIRST) {
    operator fun compareTo(round: Round): Int {
        val commit = commitRound.compareTo(round.commitRound)
        val attempt = attemptRound.compareTo(round.attemptRound)
        val phase = phase.compareTo(round.phase)
        if (commit != 0) return commit
        if (attempt != 0) return attempt
        if (phase != 0) return phase
        return 0
    }
}
