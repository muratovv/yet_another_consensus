package agreement

import agreement.coordinator.CoordinatorConverter
import agreement.coordinator.CoordinatorRoundInput
import agreement.coordinator.YacPhaseCoordinator
import data.Proposal
import data.internal.crypto.Hasher
import io.reactivex.Observable
import io.reactivex.Scheduler
import main.pipeline.AgreementGate
import main.pipeline.AgreementInput
import main.pipeline.data.ConsensusTransition

/**
 * @param pipelineScheduler - scheduler of the consensus pipeline
 * this scheduler will be invoked on new consensus outcome
 * @param phaseCoordinator - phase coordinator which provides agreed hashes
 * @param outcomeCoordinatorConverter - converter from [YacPhaseCoordinator.outcome] to pipeline outcome
 * @param hasher - hash factory for proposals
 */
class YacAgreementGate(
    private val pipelineScheduler: Scheduler,
    private val phaseCoordinator: YacPhaseCoordinator,
    private val outcomeCoordinatorConverter: CoordinatorConverter,
    private val hasher: Hasher<Proposal>
) : AgreementGate {

    private val outcome: Observable<ConsensusTransition> = phaseCoordinator.outcome().observeOn(pipelineScheduler).map {
        return@map outcomeCoordinatorConverter.retrieveOutcome(it)
    }

    override fun initialize(input: AgreementInput) {
        outcomeCoordinatorConverter.storeState(input.second, input.first)
        val proposalHash = hasher.hash(input.first)
        phaseCoordinator.initialize(CoordinatorRoundInput(proposalHash, input.second.round, input.second.activePeers))
    }

    override fun outcome(): Observable<ConsensusTransition> = outcome.observeOn(pipelineScheduler)

}
