package agreement.broadcast

import agreement.vote_storage.VoteStorage
import agreement.vote_storage.VoteStorageInsertionOutcome
import common.P2pTransport
import data.internal.Peer
import data.internal.Vote
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

// TODO 2019-06-22, @muratovv: add logs
class BroadcastTransportTrait(private val transport: P2pTransport<Peer, Vote>, private val storage: VoteStorage) :
    Broadcast {
    init {
        transport.receivedStream()
            .subscribe {
                val insertionOutcome = storage.insert(it.second)
                when (insertionOutcome) {
                    is VoteStorageInsertionOutcome.SuperMajorityVotes -> {
                        TODO("Impelement")
                    }
                    is Nothing -> {
                    }
                }
            }
    }

    private val stateSubject: BehaviorSubject<VoteStorageInsertionOutcome.SuperMajorityVotes> = BehaviorSubject.create()

    override fun initialize(input: BroadcastIncome) {
        input.second.subscribe {
            transport.send(it, input.first)
        }
    }

    override fun outcome(): Observable<VoteStorageInsertionOutcome.SuperMajorityVotes> {
        return stateSubject
    }
}
