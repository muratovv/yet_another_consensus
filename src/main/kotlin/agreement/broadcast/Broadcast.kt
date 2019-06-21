package agreement.broadcast

import agreement.vote_storage.SuperMajorityVotes
import common.Voter
import data.internal.Peer
import data.internal.Vote
import io.reactivex.Observable

typealias BroadcastIncome = Pair<Vote, Observable<Peer>>

interface Broadcast : Voter<BroadcastIncome, SuperMajorityVotes>
