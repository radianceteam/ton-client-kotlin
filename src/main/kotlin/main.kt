
import examples.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("TON Client")

    val client = ClientExample()
    client.version()
    client.subscription()

    val abi = AbiExample()
    abi.encodeV2()

    val contract = ContractExample()
    contract.generateRandomSignKeys()

    val crypto = CryptoExample()
    crypto.hash()
    crypto.keys()
    crypto.math()
    crypto.nacl()
    crypto.scrypt()

    val processing = ProcessingExample()
    processing.waitMessage()

    val tvm = TvmExample()
    tvm.executeGet()
    tvm.runExecutor()

}