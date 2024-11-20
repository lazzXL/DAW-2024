package pt.isel

interface UpdatedChannelEmitter {
    fun emit(signal: UpdatedChannel)
    fun onCompletion(callback: () -> Unit)
    fun onError(callback: (Throwable) -> Unit)
}
