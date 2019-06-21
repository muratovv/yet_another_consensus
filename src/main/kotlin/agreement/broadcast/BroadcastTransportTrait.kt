package agreement.broadcast

import agreement.vote_storage.Nothing
import agreement.vote_storage.SuperMajorityVotes
import agreement.vote_storage.VoteStorage
import common.P2pTransport
import data.internal.Peer
import data.internal.Vote
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BroadcastTransportTrait(private val transport: P2pTransport<Peer, Vote>, private val storage: VoteStorage) :
    Broadcast {
    init {
        transport.receivedStream()
            .subscribe {
                val insertionOutcome = storage.insert(it.second)
                when (insertionOutcome) {
                    is SuperMajorityVotes -> {
                        TODO("Impelement")
                    }
                    is Nothing -> {
                    }
                }
            }
    }

    private val stateSubject: BehaviorSubject<SuperMajorityVotes> = BehaviorSubject.create()

    override fun initialize(input: BroadcastIncome) {
        input.second.subscribe {
            transport.send(it, input.first)
        }
    }

    override fun outcome(): Observable<SuperMajorityVotes> {
        return stateSubject
    }
}
