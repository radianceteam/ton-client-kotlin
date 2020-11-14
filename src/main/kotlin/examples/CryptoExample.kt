package examples

import com.radiance.tonclient.TONException
import com.radiance.tonclient.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.awt.event.HierarchyListener
import java.util.*
import java.util.concurrent.ExecutionException
import javax.print.attribute.IntegerSyntax
import javax.xml.stream.events.EndDocument
import kotlin.math.round

class CryptoExample {

    suspend fun math() {

        val composite = 0xda
        val factorizeResult = Helper.crypto.factorize(composite.toString(16)).await()
        println("""crypto.factorize("0x%s"): %s""".format(composite.toString(16), factorizeResult.joinToString(",") ))
        Helper.assertEquals("crypto.factorize", composite, factorizeResult.map{it.toInt(16)}.reduce{acc, i -> acc * i })

        try {
            Helper.crypto.factorize("c7").await()
            Helper.fail("Managed to factorize a prime number")
        }
        catch (e: Exception) {
            println(e.cause)
        }

        val params = listOf(4, 2, 6)
        val paramsHex = params.map { it.toString(16) }
        val modularPowerResult = Helper.crypto.modularPower(paramsHex[0], paramsHex[1], paramsHex[2]).await()
        println("""crypto.modularPower(%s): %s""".format(paramsHex.joinToString(","), modularPowerResult))
        Helper.assertEquals("crypto.modularPower", (round(Math.pow(params[0].toDouble(), params[1].toDouble())) % params[2]).toInt().toString(16), modularPowerResult)

        val str = "abcdABCD0123"
        println("crypto.tonCrc16('%s'): %s".format(str, Helper.crypto.tonCrc16(str)))
    }

    suspend fun hash() {
        Helper.assertEquals("crypto.sha512", Helper.crypto.sha512(Encoder.encodeToString("Message to hash with sha 512".toByteArray())).await(),"2616a44e0da827f0244e93c2b0b914223737a6129bc938b8edf2780ac9482960baa9b7c7cdb11457c1cebd5ae77e295ed94577f32d4c963dc35482991442daa5")
        Helper.assertEquals("crypto.sha256", Helper.crypto.sha256(Encoder.encodeToString("Message to hash with sha 256".toByteArray())).await(),"16fd057308dd358d5a9b3ba2de766b2dfd5e308478fc1f7ba5988db2493852f5")
    }

    suspend fun keys() {

        val keys = Helper.crypto.generateRandomSignKeys().await()
        println("crypto.generateRandomSignKeys() : public:%s secret:%s".format(keys.public, keys.secret))

        val safePublic = Helper.crypto.convertPublicKeyToTonSafeFormat(keys.public).await()
        println("Safe public key :' %s'".format(safePublic))

        val randomBytes = Helper.crypto.generateRandomBytes(15).await()
        println("Random bytes: %s".format(randomBytes))

        val ros = Helper.crypto.sign(randomBytes, keys).await()
        println("crypto.sign: %s".format(ros))

        val verified = Helper.crypto.verifySignature(ros.signed, keys.public).await()
        println("crypto.verifySignature: %s".format(verified))

        try {
            Helper.crypto.verifySignature(randomBytes, keys.public).await()
            Helper.fail("Verified wrong data")
        }
        catch (e: Exception) {
            println(e.cause)
        }

        Helper.crypto.mnemonicWords(1).await()

        val xPrivate = Helper.crypto.hdkeyXprvFromMnemonic("abuse boss fly battle rubber wasp afraid hamster guide essence vibrant tattoo", null, null).await()
        println("Extended private key: %s".format(xPrivate))
        Helper.assertEquals("crypto.hdkeyXprvFromMnemonic", xPrivate, "xprv9s21ZrQH143K25JhKqEwvJW7QAiVvkmi4WRenBZanA6kxHKtKAQQKwZG65kCyW5jWJ8NY9e3GkRoistUjjcpHNsGBUv94istDPXvqGNuWpC")

        val publicKey = Helper.crypto.hdkeyPublicFromXprv(xPrivate).await()
        println("Public key: %s".format(publicKey))

        try {
            Helper.crypto.hdkeyPublicFromXprv(randomBytes).await()
            Helper.fail("Extraction from random data must fail")
        }
        catch (e: Exception) {
            println(e.cause)
        }

        val secretKey = Helper.crypto.hdkeySecretFromXprv(xPrivate).await()
        println("Secret key: %s".format(secretKey))

        try {
            Helper.crypto.hdkeySecretFromXprv(randomBytes).await()
            Helper.fail("Extraction from random data must fail")
        }
        catch (e: Exception) {
            println(e.cause)
        }
    }

    suspend fun scrypt() {
        Helper.assertEquals("crypto.scrypt",
            Helper.crypto.scrypt(
                Encoder.encodeToString("Test Password".toByteArray()),
                Encoder.encodeToString("Test Salt".toByteArray()),
                10, 8, 16, 64).await(),
            "52e7fcf91356eca55fc5d52f16f5d777e3521f54e3c570c9bbb7df58fc15add73994e5db42be368de7ebed93c9d4f21f9be7cc453358d734b04a057d0ed3626d")
    }

    suspend fun nacl() {
        Helper.assertEquals("crypto.naclSignKeypairFromSecretKey",
            Helper.crypto.naclSignKeypairFromSecretKey("8fb4f2d256e57138fb310b0a6dac5bbc4bee09eb4821223a720e5b8e1f3dd674").await().public,
            "aa5533618573860a7e1bf19f34bd292871710ed5b2eafa0dcdbb33405f2231c6")


        Helper.assertEquals("crypto.naclSign",
            Helper.crypto.naclSign(Encoder.encodeToString("Test Message".toByteArray()), "56b6a77093d6fdf14e593f36275d872d75de5b341942376b2a08759f3cbae78f1869b7ef29d58026217e9cf163cbfbd0de889bdf1bf4daebf5433a312f5b8d6e").await(),
            "+wz+QO6l1slgZS5s65BNqKcu4vz24FCJz4NSAxef9lu0jFfs8x3PzSZRC+pn5k8+aJi3xYMA3BQzglQmjK3hA1Rlc3QgTWVzc2FnZQ==")

        Helper.assertEquals("crypto.naclSignOpen", String(Decoder.decode(
            Helper.crypto.naclSignOpen("""+wz+QO6l1slgZS5s65BNqKcu4vz24FCJz4NSAxef9lu0jFfs8x3PzSZRC+pn5k8+aJi3xYMA3BQzglQmjK3hA1Rlc3QgTWVzc2FnZQ==""", "1869b7ef29d58026217e9cf163cbfbd0de889bdf1bf4daebf5433a312f5b8d6e").await())),
            "Test Message")

        val keys = Helper.crypto.naclBoxKeypair().await()
        Helper.assertEquals("box public length", keys.public.length, 64)
        Helper.assertEquals("box secret length", keys.secret.length, 64)
        Helper.assertNotEquals("public / secret key", keys.public, keys.secret)

        Helper.assertEquals("crypto.naclBoxKeypairFromSecretKey",
            Helper.crypto.naclBoxKeypairFromSecretKey("e207b5966fb2c5be1b71ed94ea813202706ab84253bdf4dc55232f82a1caf0d4").await().public,
            "a53b003d3ffc1e159355cb37332d67fc235a7feb6381e36c803274074dc3933a")

        Helper.assertEquals("crypto.naclBox",
            Helper.crypto.naclBox(Encoder.encodeToString("Test Message".toByteArray()), "cd7f99924bf422544046e83595dd5803f17536f5c9a11746", "c4e2d9fe6a6baf8d1812b799856ef2a306291be7a7024837ad33a8530db79c6b", "d9b9dc5033fb416134e5d2107fdbacab5aadb297cb82dbdcd137d663bac59f7f").await(),
            "li4XED4kx/pjQ2qdP0eR2d/K30uN94voNADxwA==")

        Helper.assertEquals("crypto.naclBoxOpen", String(Decoder.decode(
            Helper.crypto.naclBoxOpen("li4XED4kx/pjQ2qdP0eR2d/K30uN94voNADxwA==", "cd7f99924bf422544046e83595dd5803f17536f5c9a11746","c4e2d9fe6a6baf8d1812b799856ef2a306291be7a7024837ad33a8530db79c6b", "d9b9dc5033fb416134e5d2107fdbacab5aadb297cb82dbdcd137d663bac59f7f").await())),
            "Test Message")

    }

    companion object {
        val Encoder = Base64.getEncoder()
        val Decoder = Base64.getDecoder()
    }

}