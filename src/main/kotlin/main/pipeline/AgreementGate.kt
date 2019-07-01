package main.pipeline

import common.Streaming
import data.Proposal
import main.pipeline.data.ConsensusState
import main.pipeline.data.ConsensusTransition

typealias AgreementInput = Pair<Proposal, ConsensusState>

interface AgreementGate : Streaming<AgreementInput, ConsensusTransition>
