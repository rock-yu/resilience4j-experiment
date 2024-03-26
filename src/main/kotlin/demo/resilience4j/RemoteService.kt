package demo.resilience4j


interface RemoteService {
    fun process(i: Int): Int
}
