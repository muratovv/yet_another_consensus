package main.pipeline.data

import data.internal.ConsensusRound
import data.internal.PeerCollection

data class ConsensusState(val activePeers: PeerCollection, val round: ConsensusRound)
