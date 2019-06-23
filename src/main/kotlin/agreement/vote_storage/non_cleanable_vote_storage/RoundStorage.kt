package agreement.vote_storage.non_cleanable_vote_storage

import agreement.CertificateValidator
import agreement.Supermajority
import agreement.vote_storage.VoteStorageInsertionOutcome
import data.internal.AgreementRound
import data.internal.PeerCollection
import data.internal.Vote

class RoundStorage(
    private val round: AgreementRound,
    private val supermajorityValidator: CertificateValidator
) {
    var peers: PeerCollection = hashSetOf()
    private val votes: MutableSet<Vote> = hashSetOf()

    fun insert(vote: Vote): VoteStorageInsertionOutcome {
        if (validate(vote)) {
            votes.add(vote)
        }
        return getOutcome()
    }

    fun getOutcome(): VoteStorageInsertionOutcome {
        return if (supermajorityValidator(votes, peers))
            VoteStorageInsertionOutcome.SuperMajorityVotes(Supermajority.create(supermajorityValidator, votes, peers)!!)
        else VoteStorageInsertionOutcome.Nothing
    }

    private fun validate(vote: Vote): Boolean {
        return (round == vote.agreementRound) and
                (votes.find { collectionVote -> collectionVote.signature.peer == vote.signature.peer } == null)
    }
}
