package agreement.coordinator

import agreement.Supermajority
import common.Streaming
import data.internal.ConsensusRound
import data.internal.PeerCollection
import io.reactivex.Observable

/**
 * State of emitted supermajority
 */
data class SupermajorityState(val requiresPropagation: Boolean = false)

/**
 * Interface is responsible for emitting new supermajorities available from the network
 */
interface MajoritiesEmitter : Streaming<Pair<ConsensusRound, PeerCollection>, Pair<Supermajority, SupermajorityState>> {
    /**
     * New finalized states available for the node
     */
    override fun outcome(): Observable<Pair<Supermajority, SupermajorityState>>

    /**
     * Notify emitter about new round
     * @param input - the pair of new consensus round and attached participants of the round
     */
    override fun initialize(input: Pair<ConsensusRound, PeerCollection>)
}
