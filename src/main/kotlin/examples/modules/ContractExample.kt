package examples.modules

import com.radiance.tonclient.await
import examples.Helper

class ContractExample {

    suspend fun generateRandomSignKeys() {
        println("Contract Example")

        val keyPair = Helper.crypto.generateRandomSignKeys().await()
        print("key pair : ")
        println(keyPair)
    }
}