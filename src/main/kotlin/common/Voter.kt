package common

import io.reactivex.Observable

interface Voter<Input, Output> {
    fun initialize(input: Input)
    fun outcome(): Observable<Output>
}
