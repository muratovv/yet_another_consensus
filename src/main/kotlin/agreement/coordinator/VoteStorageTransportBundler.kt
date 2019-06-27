package agreement.coordinator

import agreement.Supermajority
import agreement.vote_storage.VoteStorage
import agreement.vote_storage.VoteStorageInsertionOutcome
import common.NetworkObserver
import data.internal.ConsensusRound
import data.internal.Peer
import data.internal.PeerCollection
import data.internal.Vote
import io.reactivex.Observable

/**
 * Sum type of all possible messages from the network
 */
sealed class ReceivedFromNetwork {
    class VoteObserved(val vote: Vote) : ReceivedFromNetwork()
    class VotesObserved(val votes: Collection<Vote>) : ReceivedFromNetwork()
}

class VoteStorageTransportBundler(
    private val voteStorage: VoteStorage,
    voteReceiver: NetworkObserver<Peer, ReceivedFromNetwork>
) : MajoritiesEmitter {

    private val supermajorityOutcome: Observable<Pair<Supermajority, SupermajorityState>> =
        voteReceiver.receivedStream().map {
            when (val received = it.second) {
                is ReceivedFromNetwork.VoteObserved -> {
                    Pair(voteStorage.insert(received.vote), SupermajorityState(true))
                }
                is ReceivedFromNetwork.VotesObserved -> {
                    Pair(voteStorage.insert(received.votes), SupermajorityState(false))
                }
            }
        }.flatMap {
            when (val received = it.first) {
                is VoteStorageInsertionOutcome.SuperMajorityVotes ->
                    Observable.just(Pair(received.supermajority, it.second))

                is VoteStorageInsertionOutcome.Nothing ->
                    Observable.empty<Pair<Supermajority, SupermajorityState>>()
            }
        }

    override fun initialize(input: Pair<ConsensusRound, PeerCollection>) {
        voteStorage.setPeers(input.first, input.second)
    }

    override fun outcome(): Observable<Pair<Supermajority, SupermajorityState>> = supermajorityOutcome

}
