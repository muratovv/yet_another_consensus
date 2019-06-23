package agreement.broadcast

import data.internal.Peer
import io.reactivex.Observable

/**
 * Interface responsible to creation peer observables
 */
interface PeerEmitter {
    /**
     * Create observable which spread peers in the time
     * @param peers - initial collection
     */
    fun distributedObservable(peers: Collection<Peer>): Observable<Peer>

    /**
     * Create observable which immediately emits the peers
     * @param peers - initial collection
     */
    fun immediateObservable(peers: Collection<Peer>): Observable<Peer>
}
