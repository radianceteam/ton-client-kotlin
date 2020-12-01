package examples.modules

import com.radiance.tonclient.Abi
import com.radiance.tonclient.await
import examples.Helper
import java.util.concurrent.atomic.AtomicInteger

class ProcessingExample {

    suspend fun waitMessage() {
        val keys = Helper.crypto.generateRandomSignKeys().await()

        val encoded = Helper.abi.encodeMessage(Helper.eventsAbi, null, Abi.DeploySet(Helper.eventsTvc, null, null),Abi.CallSet("constructor", Abi.FunctionHeader(Int.MAX_VALUE, null, keys.public), null), Abi.Signer.Keys(keys), null).await()

        Helper.getGramsFromGiver(encoded.address)!!.await()

        var events = listOf<String>()
        val transCounter = AtomicInteger()

        val subscription = Helper.net.subscribeCollection("transactions", null, "id account_addr") { event ->
            if (event.result.findValue("account_addr").asText() == encoded.address) {
                transCounter.incrementAndGet()
            }
        }.await()

        val blockId = Helper.processing.sendMessage(encoded.message, Helper.eventsAbi, true) { event ->
            events.plus(event.type)
        }.await()

        // -- there is currently an issue with the time out
        // Helper.processing.waitForTransaction(Helper.eventsAbi, encoded.message, blockId, true) { event ->
        //     events.plus(event.type)
        // }.await()

        Helper.net.unsubscribe(subscription).await()

        Helper.assertNotEquals("Transaction counter", transCounter.get(), 0)
    }

}