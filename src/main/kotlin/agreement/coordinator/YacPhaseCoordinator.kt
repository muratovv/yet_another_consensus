package agreement.coordinator

import agreement.Supermajority
import agreement.broadcast.Broadcast
import agreement.broadcast.PeerEmitter
import data.internal.*
import data.internal.crypto.Hash
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import util.getAny

typealias PhaseBroadcastType = Broadcast<Peer, Vote>
typealias CommitBroadcastType = Broadcast<Peer, Collection<Vote>>

/**
 * Class provides implementation of two-phase agreement schema for YAC consensus
 * @param broadcastHandler - used broadcasts
 * @param majoritiesEmitter - interface of vote storage which connected to transport level.
 * [YacPhaseCoordinator] assumes that [OneToOneStreamingTrait] will be used for the parameter
 * @param voteFactory - factory which create and signs votes
 * @param outcomeConverter - phase converter
 */
class YacPhaseCoordinator(
    private val broadcastHandler: BroadcastHandler,
    private val majoritiesEmitter: MajoritiesEmitter,
    private val peerEmitter: PeerEmitter,
    private val voteFactory: VoteFactory,
    private val outcomeConverter: OutcomeConverterInterface
) : PhaseCoordinator {

    private val certificatedYacOutcomes: BehaviorSubject<CertificatedYacOutcome> = BehaviorSubject.create()

    init {
        majoritiesEmitter.outcome().subscribe {

            tryToBroadcastSupermajority(it.first, it.second.requiresPropagation)

            // parse the type of supermajority depending on the finalized phase
            when (it.first.attachedAgreementRound.phase) {
                Phase.FIRST -> {
                    when (val firstPhase = outcomeConverter.convertFirstPhase(it.first)) {
                        is FirstPhaseOutcome.CommitOutcome -> {
                            // return commit
                            certificatedYacOutcomes.onNext(CertificatedYacOutcome.Commit(firstPhase.commitCertificate))
                        }
                        is FirstPhaseOutcome.RejectOutcome -> {
                            // return reject
                            certificatedYacOutcomes.onNext(CertificatedYacOutcome.Reject(firstPhase.rejectCertificate))
                        }
                        is FirstPhaseOutcome.UndecidedOutcome -> {
                            // initiate second phase
                            initiateSecondPhase(it.first, firstPhase)
                        }
                    }
                }
                Phase.SECOND -> {
                    when (val secondPhase = outcomeConverter.convertSecondPhase(it.first)) {
                        is SecondPhaseOutcome.CommitOutcome -> {
                            // return commit
                            certificatedYacOutcomes.onNext(CertificatedYacOutcome.Commit(secondPhase.commitCertificate))
                        }
                        is SecondPhaseOutcome.RejectOutcome -> {
                            // return reject
                            certificatedYacOutcomes.onNext(CertificatedYacOutcome.Reject(secondPhase.rejectSertificate))
                        }
                    }
                }
            }
        }
    }

    override fun initialize(input: CoordinatorRoundInput) {
        // notify vote storage about new round
        majoritiesEmitter.initialize(Pair(input.consensusRound, input.peersCollection))
        initiateFirstPhase(input.hash, input.consensusRound.makeAgreementRound(), input.peersCollection)
    }

    override fun outcome(): Observable<CertificatedYacOutcome> = certificatedYacOutcomes

    // ------------------------------| Private methods |----------------------------------------------------------------

    /**
     * Propagate the supermajority to the network if required.
     * Invalidate previous broadcasts
     */
    private fun tryToBroadcastSupermajority(certificate: Supermajority, requiresPropagation: Boolean) {
        broadcastHandler.invalidate()
        if (requiresPropagation) {
            broadcastHandler.commitBroadcast = broadcastHandler.commitBroadcastFactory.create()
                .propagate(certificate.votes, peerEmitter.immediateObservable(certificate.peers))
        }
    }

    /**
     * Initiate the first phase of the agreement
     */
    private fun initiateFirstPhase(hash: Hash, round: AgreementRound, peers: PeerCollection) {
        val vote = voteFactory.create(hash, round)
        broadcastHandler.phaseBroadcast = broadcastHandler.phaseBroadcastFactory.create()
            .propagate(vote, peerEmitter.distributedObservable(peers))
    }

    /**
     * Initiate the second phase of the agreement
     */
    private fun initiateSecondPhase(supermajority: Supermajority, firstPhase: FirstPhaseOutcome.UndecidedOutcome) {
        val secondRound = supermajority.votes.getAny().agreementRound.getNextPhaseRound()
        broadcastHandler.phaseBroadcast = broadcastHandler.phaseBroadcastFactory.create().propagate(
            voteFactory.create(firstPhase.undecidedCertificate.frequentHash.first, secondRound),
            peerEmitter.distributedObservable(firstPhase.undecidedCertificate.peers)
        )
    }

}
