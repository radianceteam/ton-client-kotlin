package examples

import com.radiance.tonclient.await

class ContractExample {

    suspend fun generateRandomSignKeys() {
        println("Contract Example")

        val keyPair = Helper.crypto.generateRandomSignKeys().await()
        print("key pair : ")
        println(keyPair)
    }
}