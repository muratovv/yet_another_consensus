package agreement

import agreement.vote_storage.MajorityChecker
import data.internal.*
import util.getAny

// ------------------------------| Validation |-------------------------------------------------------------------------

typealias CertificateValidator = (votes: Collection<Vote>, peers: Collection<Peer>) -> Boolean
typealias OutcomeUpcCastValidator = (supermajority: Supermajority) -> Boolean

open class SupermajorityChecker(internal val majorityChecker: MajorityChecker) : CertificateValidator {
    override fun invoke(votes: Collection<Vote>, peers: Collection<Peer>): Boolean =
        votes.validate(SameRoundPredicate()) and
                (votes.map { vote -> vote.signature.peer }.toSet().size == votes.size) and
                votes.all { vote -> peers.contains(vote.signature.peer) } and
                majorityChecker.hasOutcome(votes.getFrequentHash().second, votes.size, peers.size)

}

class CommitChecker(majorityChecker: MajorityChecker) : SupermajorityChecker(majorityChecker), OutcomeUpcCastValidator {
    override fun invoke(votes: Collection<Vote>, peers: Collection<Peer>): Boolean {
        return super.invoke(votes, peers) and commitInvariant(votes, peers)

    }

    override fun invoke(supermajority: Supermajority): Boolean {
        return commitInvariant(supermajority.votes, supermajority.peers)
    }

    private fun commitInvariant(votes: Collection<Vote>, peers: Collection<Peer>): Boolean {
        return votes.validate(SameHashPredicate()) and
                majorityChecker.hasCommit(votes.getFrequentHash().second, peers.size)
    }
}

class RejectChecker(majorityChecker: MajorityChecker) : SupermajorityChecker(majorityChecker), OutcomeUpcCastValidator {
    override fun invoke(votes: Collection<Vote>, peers: Collection<Peer>): Boolean {
        return super.invoke(votes, peers) and rejectInvariant(votes, peers)

    }

    override fun invoke(supermajority: Supermajority): Boolean {
        return rejectInvariant(supermajority.votes, supermajority.peers)
    }

    private fun rejectInvariant(votes: Collection<Vote>, peers: Collection<Peer>): Boolean =
        votes.validate(SameHashPredicate()) and
                majorityChecker.hasReject(votes.getFrequentHash().second, votes.size, peers.size)
}

class UndecidedChecker(majorityChecker: MajorityChecker) : SupermajorityChecker(majorityChecker),
    OutcomeUpcCastValidator {

    override fun invoke(votes: Collection<Vote>, peers: Collection<Peer>): Boolean {
        return super.invoke(votes, peers) and undecidedInvariant(votes, peers)

    }

    override fun invoke(supermajority: Supermajority): Boolean {
        return undecidedInvariant(supermajority.votes, supermajority.peers)
    }

    private fun undecidedInvariant(votes: Collection<Vote>, peers: Collection<Peer>): Boolean {
        val frequent = votes.getFrequentHash().second
        val voted = votes.size
        val all = peers.size
        return majorityChecker.hasOutcome(frequent, voted, all) and
                !majorityChecker.hasReject(frequent, voted, all) and
                !majorityChecker.hasCommit(frequent, all) and
                (votes.getFrequentHash().second >= peers.size / 2)
    }

}


// ------------------------------| Data types |-------------------------------------------------------------------------

open class Supermajority internal constructor(val votes: Collection<Vote>, val peers: Collection<Peer>) {

    companion object Factory {
        // TODO 2019-06-22, @muratovv: rework with optional type
        fun create(validator: CertificateValidator, votes: Collection<Vote>, peers: Collection<Peer>): Supermajority? {
            return if (validator(votes, peers)) Supermajority(votes, peers) else null
        }
    }

    val attachedAgreementRound: AgreementRound = votes.getAny().agreementRound
}

class CommitCertificate(votes: Collection<Vote>, peers: Collection<Peer>) : Supermajority(votes, peers) {
    companion object Factory {
        fun create(
            validator: CertificateValidator,
            votes: Collection<Vote>,
            peers: Collection<Peer>
        ): CommitCertificate? =
            if (validator(votes, peers)) CommitCertificate(votes, peers) else null

        fun tryUpCast(validator: OutcomeUpcCastValidator, supermajority: Supermajority): CommitCertificate? =
            if (validator(supermajority)) CommitCertificate(supermajority.votes, supermajority.peers) else null
    }

    val committedHash = votes.getFrequentHash()
}

class RejectCertificate(votes: Collection<Vote>, peers: Collection<Peer>) : Supermajority(votes, peers) {
    companion object Factory {
        fun create(
            validator: CertificateValidator,
            votes: Collection<Vote>,
            peers: Collection<Peer>
        ): RejectCertificate? {
            return if (validator(votes, peers)) RejectCertificate(votes, peers) else null
        }

        fun tryUpCast(validator: OutcomeUpcCastValidator, supermajority: Supermajority): RejectCertificate? =
            if (validator(supermajority)) RejectCertificate(supermajority.votes, supermajority.peers) else null
    }
}

class UndecidedCertificate(votes: Collection<Vote>, peers: Collection<Peer>) : Supermajority(votes, peers) {
    companion object Factory {
        fun create(
            validator: CertificateValidator,
            votes: Collection<Vote>,
            peers: Collection<Peer>
        ): UndecidedCertificate? {
            return if (validator(votes, peers)) UndecidedCertificate(votes, peers) else null
        }

        fun tryUpCast(validator: OutcomeUpcCastValidator, supermajority: Supermajority): UndecidedCertificate? =
            if (validator(supermajority)) UndecidedCertificate(supermajority.votes, supermajority.peers) else null
    }

    val frequentHash = votes.getFrequentHash()
}
