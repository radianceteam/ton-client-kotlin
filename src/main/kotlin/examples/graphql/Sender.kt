package examples.graphql

import com.radiance.tonclient.*
import examples.Helper
import java.math.BigInteger

class Sender {

    suspend fun sendMoney(senderAddress: String, senderKeys: Crypto.KeyPair, recepientAddress: String) {

        val context = TONContext.create(Client.ClientConfig(Client.NetworkConfig("https://net.ton.dev/graphql")));

        Abi(context)
                .encodeMessageBody(
                        Helper.abiFromResource("/examples/graphql/Transfer.abi.json"),
                        Abi.CallSet(
                                "transfer",
                                null,
                                """{ "comment": ${BigInteger(1, "Hello friend!!!".encodeToByteArray()).toString(16)} }"""),
                                true,
                                Abi.Signer.None,
                                null
                ).thenCompose { payload ->
                    Processing(context).processMessage(
                            Helper.abiFromResource("/examples/graphql/SetcodeMultisigWallet.abi.json"),
                            senderAddress,
                            null,
                            Abi.CallSet(
                                    "sendTransaction",
                                    null,
                                    """   { 
                                        |   "dest": "${recepientAddress}",
                                        |   "value": 300000000,
                                        |   "bounce": false,
                                        |   "flags": 3,
                                        |   "payload": "${payload.body}
                                        | }""".trimMargin()
                            ),
                            Abi.Signer.Keys(senderKeys),
                            null,
                            true
                    ) { println(it) }.thenApply { println(it) }
                }

    }

}