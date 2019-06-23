package common

import io.reactivex.Observable

/**
 * Type of filtering function
 */
typealias FilterLambdaType<Input, Output> = (input: Input?, lastOutput: Output) -> Boolean

/**
 * Type of notificator function
 */
typealias IgnoreLambdaType<Input, Output> = (input: Input?, lastOutput: Output) -> Unit

/**
 * Trait of [Streaming] which guarantees one [Streaming.initialize] call leads to one output value in [Streaming.outcome]
 * @param wrappedStream - initial stream which required to change the behaviour
 * @param filterLambda - lambda which invokes on filtering values. If lambda returns true the rest of messages will be ignored until [initialize] call.
 * Default parameter every time gets first value.
 * @param ignoreLambda - lambda which invokes on values which were ignored by filtration.
 * Default parameter ignores every value
 */
class OneToOneStreamingTrait<Input, Output>(
    private val wrappedStream: Streaming<Input, Output>,
    private val filterLambda: FilterLambdaType<Input, Output> = { _, _ -> true },
    private val ignoreLambda: IgnoreLambdaType<Input, Output> = { _, _ -> run {} }
) : Streaming<Input, Output> {
    private var lastInput: Input? = null
    private var requiredToFilter = true
    private val subject: Observable<Output> = wrappedStream.outcome().filter {
        return@filter if (requiredToFilter and filterLambda(lastInput, it)) {
            requiredToFilter = false
            true
        } else {
            ignoreLambda(lastInput, it)
            false
        }
    }

    override fun initialize(input: Input) {
        lastInput = input
        requiredToFilter = true
        wrappedStream.initialize(input)
    }

    override fun outcome(): Observable<Output> {
        return subject
    }
}
