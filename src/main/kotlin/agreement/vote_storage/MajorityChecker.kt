package agreement.vote_storage

/**
 * The interface provides methods with consistency model
 */
interface MajorityChecker {

    /**
     * Checks is there a commit
     * @param frequent - the most frequent voted value
     * @param all - all voting participants
     * @return true if commit
     */
    fun hasCommit(frequent: Int, all: Int): Boolean

    /**
     * Checks is there a reject - guarantee that no one from the network had collected a commit
     * @param frequent - the most frequent voted value
     * @param voted - number of voted peers
     * @param all - all voting participants
     * @return true if reject
     */
    fun hasReject(frequent: Int, voted: Int, all: Int): Boolean

    /**
     * Checks is there a finalized value
     * @param frequent - the most frequent voted value
     * @param voted - number of voted peers
     * @param all - all voting participants
     * @return true if finalized
     */
    fun hasOutcome(frequent: Int, voted: Int, all: Int): Boolean
}
