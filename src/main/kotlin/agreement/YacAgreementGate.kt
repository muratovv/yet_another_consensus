package agreement

import common.Voter
import data.Proposal
import data.internal.ConsensusState

interface YacOutcome
typealias YacAgreementInput = Pair<Proposal, ConsensusState>
typealias YacAgreementOutcome = Pair<YacOutcome, ConsensusState>

interface YacAgreementGate : Voter<YacAgreementInput, YacAgreementOutcome>
