package agreement.broadcast

import agreement.vote_storage.SuperMajorityVotes
import data.internal.Round
import io.reactivex.Observable
import mu.KLogger

class BroadcastCacheVoteTrait(
    private val broadcast: Broadcast,
    private val logger: KLogger
) : Broadcast {

    private val emmitter: Observable<SuperMajorityVotes> = broadcast.outcome()
    private var lastRound = Round(0, 0)

    override fun initialize(input: BroadcastIncome) {
        val newRound = input.first.round
        if (newRound > lastRound) {
            logger.error { "Expected new round: $newRound will be greater than last processed: $lastRound" }
        }
        lastRound = newRound
        return broadcast.initialize(input)
    }

    override fun outcome(): Observable<SuperMajorityVotes> {
        return emmitter.filter {
            if (lastRound < it.attachedRound()) {
                lastRound = it.attachedRound()
                true
            } else false
        }
    }

}
