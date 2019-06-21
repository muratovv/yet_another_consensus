package agreement.vote_storage

interface MajorityChecker {
    fun hasCommit(frequent: Int, all: Int): Boolean
    fun hasReject(frequent: Int, voted: Int, all: Int): Boolean
    fun hasOutcome(frequent: Int, voted: Int, all: Int): Boolean
}
