package functional


sealed class Either<out E, out V> {
    data class Error<out E>(val value: E) : Either<E, Nothing>()
    data class Value<out V>(val value: V) : Either<Nothing, V>()
}

fun <A> success(value: A): Either<Nothing, A> = Either.Value(value)
inline fun <A> success(crossinline func: () -> A): Either<Nothing, A> = Either.Value(func())

fun <A> failure(value: A): Either<A, Nothing> = Either.Error(value)
inline fun <A> failure(crossinline func: () -> A): Either<A, Nothing> = Either.Error(func())

fun <A> either(action: () -> A): Either<Exception, A> = try {
    success(action())
} catch (e: Exception) {
    failure(e)
}

inline infix fun <E, V, V2> Either<E, V>.map(func: (V) -> V2): Either<E, V2> = when (this) {
    is Either.Error -> this
    is Either.Value -> Either.Value(func(value))
}

inline infix fun <E, V, V2> Either<E, V>.flatMap(func: (V) -> Either<E, V2>): Either<E, V2> = when (this) {
    is Either.Error -> this
    is Either.Value -> func(value)
}

inline infix fun <E, V> Either<E, V>.recoverWith(func: (E) -> Either<E, V>): Either<E, V> = when (this) {
    is Either.Error -> func(value)
    is Either.Value -> this
}

inline infix fun <E, V, E2> Either<E, V>.mapError(func: (E) -> E2): Either<E2, V> = when (this) {
    is Either.Error -> Either.Error(func(value))
    is Either.Value -> this
}

inline fun <E, V, A> Either<E, V>.fold(error: (E) -> A, value: (V) -> A): A = when (this) {
    is Either.Error -> error(this.value)
    is Either.Value -> value(this.value)
}