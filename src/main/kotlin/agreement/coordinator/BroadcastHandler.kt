package agreement.coordinator

import agreement.broadcast.Broadcast
import data.internal.Peer
import data.internal.Vote

/**
 * Helper class for [YacPhaseCoordinator] which responsible for managing broadcasts
 */
class BroadcastHandler(
    val phaseBroadcastFactory: Broadcast.BroadcastFactory<Peer, Vote>,
    val commitBroadcastFactory: Broadcast.BroadcastFactory<Peer, Collection<Vote>>
) {
    var phaseBroadcast: PhaseBroadcastType? = null
        set(value) {
            field?.decline()
            field = value
        }
    var commitBroadcast: CommitBroadcastType? = null
        set(value) {
            field?.decline()
            field = value
        }

    fun invalidate() {
        phaseBroadcast = null
        commitBroadcast = null
    }
}
