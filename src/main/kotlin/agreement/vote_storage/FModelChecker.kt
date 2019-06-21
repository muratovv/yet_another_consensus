package agreement.vote_storage


/**
 * Class represents K*F+1 consistency model,
 * where F - number of faulty peers,
 * K - model parameter
 *
 * @param toleranceModel - K-parameter
 * @param supermajorityThreshold - multiplier of peers number which required for commit
 */
class FModelChecker(private val toleranceModel: Int, private val supermajorityThreshold: Int) : MajorityChecker {

    override fun hasCommit(frequent: Int, all: Int) = if (all == 0) false else
        frequent >= Math.ceil(fetchTolerancePart(all) * supermajorityThreshold).toInt()

    override fun hasReject(frequent: Int, voted: Int, all: Int): Boolean = if (all == 0) false else
        !hasCommit(frequent + (all - voted) + Math.ceil(fetchTolerancePart(all)).toInt(), all)

    override fun hasOutcome(frequent: Int, voted: Int, all: Int): Boolean = if (all == 0) false else
        (voted >= Math.ceil(fetchTolerancePart(all) * (toleranceModel - 1)).toInt()) or
                hasCommit(frequent, all) or hasReject(frequent, voted, all)

    private fun fetchTolerancePart(all: Int): Double = all.toDouble() / toleranceModel
}
