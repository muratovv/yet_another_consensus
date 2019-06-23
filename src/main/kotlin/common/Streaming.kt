package common

import io.reactivex.Observable

/**
 * Interface for data streaming
 * @param Input - type of input data
 * @param Output - type of outcomes
 */
interface Streaming<Input, Output> {
    fun initialize(input: Input)
    fun outcome(): Observable<Output>
}
