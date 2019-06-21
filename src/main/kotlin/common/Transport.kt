package common

import io.reactivex.Observable
import io.reactivex.Single

class SendStatus

interface TransportSender<Identity, Data> {
    fun send(to: Identity, data: Data): Single<SendStatus>
}

typealias Received<Identity, Data> = Pair<Identity, Data>

interface TransportReceiver<Identity, Data> {
    fun receivedStream(): Observable<Received<Identity, Data>>
}

interface P2pTransport<Identity, Data> : TransportSender<Identity, Data>, TransportReceiver<Identity, Data>
