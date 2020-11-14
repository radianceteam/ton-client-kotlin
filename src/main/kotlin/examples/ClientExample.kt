package examples

import com.radiance.tonclient.await
import kotlinx.coroutines.delay

class ClientExample {

    suspend fun version() {

        println("Client version: %s".format(Helper.client.version().await()))

    }

    suspend fun subscription() {

        val handle = Helper.net.subscribeCollection("transaction", null, "id account_addr") { event ->

        }.await()
        println("Handle: %s".format(handle))
        delay(3000)
        Helper.net.unsubscribe(handle).await()

    }

}