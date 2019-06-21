package agreement.vote_storage

import data.internal.crypto.Hash

sealed class SecondPhaseOutcome {
    class Commit(val superMajority: SuperMajorityVotes, val hash: Hash) : SecondPhaseOutcome()
    class Reject(val superMajority: SuperMajorityVotes) : SecondPhaseOutcome()
}

sealed class FirstPhaseOutcome {
    class Commit(val superMajority: SuperMajorityVotes, val hash: Hash) : FirstPhaseOutcome()
    class Reject(val superMajority: SuperMajorityVotes) : FirstPhaseOutcome()
    class Undecided(val superMajority: SuperMajorityVotes, val frequentHash: Hash) : FirstPhaseOutcome()
}

