package main.pipeline

import common.Streaming
import data.Proposal
import data.internal.ConsensusTransition

interface YacOutcome
typealias YacAgreementInput = Pair<Proposal, ConsensusTransition>
typealias YacAgreementOutcome = Pair<YacOutcome, ConsensusTransition>

interface YacAgreementGate : Streaming<YacAgreementInput, YacAgreementOutcome>
