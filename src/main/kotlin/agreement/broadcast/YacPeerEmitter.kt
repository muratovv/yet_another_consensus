package agreement.broadcast

import data.internal.Peer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class YacPeerEmitter(
    private val scheduler: Scheduler,
    private val repeats: Long,
    private val delay: Pair<Long, TimeUnit>
) : PeerEmitter {

    /**
     * Emits collection by the following rule:
     * immediately emits first element of the collection and rest of the collection plus collection [repeats] times
     * with corresponding [delay].
     */
    override fun distributedObservable(peers: Collection<Peer>): Observable<Peer> {
        val biFunction = BiFunction { peer: Peer, _: Long -> peer }

        val collectionObservable = Observable.fromIterable(peers)

        val firstObservable: Observable<Peer> = collectionObservable.skipLast(peers.size - 1)

        val restElements: Observable<Peer> = collectionObservable.skip(1)
            .mergeWith(collectionObservable.repeat(repeats))


        val intervalObservable: Observable<Peer> =
            restElements.zipWith(Observable.interval(delay.first, delay.second, scheduler), biFunction)

        return firstObservable.mergeWith(intervalObservable)
    }

    override fun immediateObservable(peers: Collection<Peer>): Observable<Peer> {
        return Observable.fromIterable(peers).observeOn(scheduler)
    }

    class YacPeerEmitterFactory(
        private val repeats: Long,
        private val delay: Pair<Long, TimeUnit>,
        private val scheduler: Scheduler
    ) : PeerEmitter.PeerEmitterFactory {
        override fun create() = YacPeerEmitter(scheduler, repeats, delay)
    }
}
