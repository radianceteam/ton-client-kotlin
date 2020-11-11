
import com.radiance.tonclient.ClientModule
import com.radiance.tonclient.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ton.sdk.TONContext

fun main() {
    println("TON Client")

    GlobalScope.launch(Dispatchers.Unconfined) {
        val version = ClientModule(TONContext.create("""{"network": {"server_address": "net.ton.dev"}}""")).version().await()
        println(version)
    }


}