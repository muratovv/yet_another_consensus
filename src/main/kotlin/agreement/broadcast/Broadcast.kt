package agreement.broadcast

import io.reactivex.Observable


/**
 * Interface of broadcast which shares data among the network
 */
interface Broadcast<Identity, Data> {
    /**
     * Share the data with participants
     * @param data - data for sending
     * @param to - identities of participants
     * Note: assumes that data propagation is asynchronous
     */
    fun propagate(data: Data, to: Observable<Identity>): Broadcast<Identity, Data>

    /**
     * Stop propagation of the data
     * Note: method should be thread-safe
     */
    fun decline(): Broadcast<Identity, Data>
}

interface BroadcastFactory<Identity, Data> {
    fun create(): Broadcast<Identity, Data>
}
