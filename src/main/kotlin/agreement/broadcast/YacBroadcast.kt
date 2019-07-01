package agreement.broadcast

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentLinkedQueue

class YacBroadcast<Identity, Data>(private val queue: ConcurrentLinkedQueue<Data>) :
    Broadcast<Identity, Data> {

    private var lastPropagation: Disposable? = null

    override fun propagate(data: Data, to: Observable<Identity>): Broadcast<Identity, Data> {
        decline()
        lastPropagation = to.subscribe { queue.add(data) }
        return this
    }

    override fun decline(): Broadcast<Identity, Data> {
        lastPropagation?.dispose()
        lastPropagation = null
        return this
    }

    class YacBroadcastFactory<Identity, Data>(private val queue: ConcurrentLinkedQueue<Data>) :
        Broadcast.BroadcastFactory<Identity, Data> {
        override fun create(): Broadcast<Identity, Data> {
            return YacBroadcast(queue)
        }
    }
}
