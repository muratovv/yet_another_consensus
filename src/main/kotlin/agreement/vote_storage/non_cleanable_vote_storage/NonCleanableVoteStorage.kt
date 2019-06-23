package agreement.vote_storage.non_cleanable_vote_storage

import agreement.CertificateValidator
import agreement.vote_storage.VoteStorage
import agreement.vote_storage.VoteStorageInsertionOutcome
import data.internal.*
import util.getAny

/**
 * Implementation of [VoteStorage] which houlds all passed rounds from the start
 */
class NonCleanableVoteStorage(
    private val supermajorityValidator: CertificateValidator,
    private val rounds: MutableMap<AgreementRound, RoundStorage>
) : VoteStorage {
    override fun setPeers(round: ConsensusRound, peers: PeerCollection) {
        setPeers(round.makeAgreementRound(), peers)
        setPeers(round.makeAgreementRound().getNextPhaseRound(), peers)
    }

    override fun insert(vote: Vote): VoteStorageInsertionOutcome {
        return findStorage(vote.agreementRound).insert(vote)
    }

    override fun insert(votes: Collection<Vote>): VoteStorageInsertionOutcome {
        return if (validate(votes)) {
            val storage = findStorage(votes.getAny().agreementRound)
            votes.forEach {
                storage.insert(it)
            }
            storage.getOutcome()
        } else {
            VoteStorageInsertionOutcome.Nothing
        }
    }

    // ------------------------------| Private Api |--------------------------------------------------------------------
    private fun setPeers(round: AgreementRound, peers: PeerCollection): RoundStorage {
        var roundStorage = rounds[round]
        if (roundStorage != null) {
            assert(roundStorage.peers.isEmpty())
            roundStorage.peers = peers.toSet()
        } else {
            roundStorage = RoundStorage(round, supermajorityValidator)
        }
        rounds[round] = roundStorage
        return roundStorage
    }

    private fun findStorage(round: AgreementRound): RoundStorage {
        return rounds[round] ?: setPeers(round, emptySet())
    }

    private fun validate(votes: Collection<Vote>): Boolean {
        return votes.validate(SameRoundPredicate())
    }
}
