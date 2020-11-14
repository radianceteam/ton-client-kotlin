package examples

import com.radiance.tonclient.*
import com.radiance.tonclient.Abi.*
import java.util.*
import java.util.concurrent.CompletableFuture


class Helper {

    companion object {

        //context = TONContext.create("""{"network": {"server_address": "net.ton.dev"}}""");
        val context = TONContext.create("""{"network": {"server_address": "http://localhost"}}""");
        val crypto = Crypto(context);
        val abi = Abi(context);
        val processing = Processing(context);
        val net = Net(context);
        val boc = Boc(context);
        val tvm = Tvm(context);
        val client = Client(context)

        val eventsAbi = abiFromResource("/Events.abi.json");
        val eventsTvc = String(Base64.getEncoder().encode(binaryFromResources("/Events.tvc")));
        val giverWalletAbi = abiFromResource("/GiverWallet.abi.json");
        val walletAbi = abiFromResource("/Wallet.abi.json");
        val multisigWalletAbi = abiFromResource("/SetcodeMultisigWallet.abi.json");
        val giverAbi = abiFromResource("/Giver.abi.json");
        val subscriptionAbi = abiFromResource("/Subscription.abi.json");
        val subscriptionTvc = String(Base64.getEncoder().encode(binaryFromResources("/Subscription.tvc")));


        fun abiFromResource(path: String?): Abi.ABI? {
            val s: Scanner = Scanner(Helper.javaClass.getResourceAsStream(path)).useDelimiter("\\A")
            val data = if (s.hasNext()) s.next() else ""
            s.close()
            return Abi.ABI.Serialized(data)
        }
        fun binaryFromResources(path: String): ByteArray {
            return Helper.javaClass.getResource(path).readBytes()
        }

        fun assertTrue(description: String, value: Boolean) {
            println("Is True: %b".format(value))
        }

        fun <T> assertEquals(value: T, expected: T): Boolean {
            return expected == value
        }

        fun <T> assertEquals(description: String, value: T, expected: T) {
            println(
                "assertion - %s: %b".format(
                    description,
                    assertEquals(
                        value,
                        expected
                    )
                )
            )
        }

        fun <T> assertNotEquals(description: String, value: T, expected: T) {
            println(
                "assertion (not equal) - %s: %b".format(
                    description,
                    !assertEquals(
                        value,
                        expected
                    )
                )
            )
        }

        fun fail(message: String) {
            println("[ ERROR ] This line shouldn't be reached : %s".format(message))
        }

        fun signDetached(data: String?, keys: Crypto.KeyPair): CompletableFuture<String>? {
            return crypto.naclSignKeypairFromSecretKey(keys.secret)
                .thenCompose { signKeys: Crypto.KeyPair ->
                    crypto.naclSignDetached(
                        data,
                        signKeys.secret
                    )
                }
        }

        fun getGramsFromGiver(address: String): CompletableFuture<Processing.ResultOfProcessMessage?>? {
            return processing.processMessage(
                giverAbi,
                "0:841288ed3b55d9cdafa806807f02a0ae0c169aa5edfe88a789a6482429756a94",
                null,
                CallSet(
                    "sendGrams",
                    null,
                    "{\"dest\":\"$address\", \"amount\":500000000}"
                ),
                Abi.Signer.None,
                null,
                false,
                null
            ) //event -> System.out.println("Event: " + event));
        }

        fun deployWithGiver(
            ABI: ABI?,
            deploySet: DeploySet?,
            callSet: CallSet?,
            signer: Signer?
        ): CompletableFuture<String?>? {
            val address = arrayOfNulls<String>(1)
            return abi.encodeMessage(ABI, null, deploySet, callSet, signer, null)
                .thenCompose { encoded ->
                    address[0] = encoded.getAddress()
                    getGramsFromGiver(encoded.getAddress())
                }
                .thenCompose { processed ->
                    processing.processMessage(
                        ABI,
                        null,
                        deploySet,
                        callSet,
                        signer,
                        null,
                        false,
                        null
                    )
                }.thenApply { processed -> address[0] }
        }

    }

}