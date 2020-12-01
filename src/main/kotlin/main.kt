
import com.radiance.tonclient.Crypto
import examples.graphql.Sender
import examples.modules.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("===================================")
    println("---         TON Client          ---")
    println("===================================")

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

    println("===================================")
    println("---         TON GraphQL        ---")
    println("===================================")

// -- set appropriate string values
//    val senderAddress = ""
//    val senderKeys = Crypto.KeyPair("", "")
//    val recepientAddress = ""
//    Sender().sendMoney(senderAddress, senderKeys, recepientAddress)

}