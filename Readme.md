# TON-SDK Client Kotlin Examples

This project describes how to setup a gradle project (see: ```build.gradle.kts```)
and how to use the underlying dependency [https://github.com/radianceteam/ton-client-java/](https://github.com/radianceteam/ton-client-java/).

Just dive into the ```examples``` package and have a look.

With the ```main.kt```, you can run all the example in a row.
If you want, you can comment out the examples in this file and focus on a specific implementation.

## How to run the examples

Install ```gradle``` with your favorite dependency management tool.

For example:
- https://gradle.org/install/ *All OS*
- https://chocolatey.org/ *Windows* ```choco install gradle```
- https://sdkman.io/install *Windows* ```sdk install gradle```
- https://brew.sh/index_de *MacOS* ```brew install gradle```
- ```sudo apt install gradle``` *Ubuntu / Debian*
- ...
*You may need admin rights.*

Then run

```
./start-container.sh     # only once
gradle run
```

in this projects root folder.

## One more thing

The ton-client-java produces ```CompletableFuture```s as results. There is a convenience extension function to
use it in a coroutine, if you are wondering where the ```.await()``` function comes from. 

(see: ```com.radiance.tonclient/package.kt``` - copied from [https://github.com/Kotlin/coroutines-examples/blob/master/examples/future/await.kt](https://github.com/Kotlin/coroutines-examples/blob/master/examples/future/await.kt) by [Roman Elizarov](https://github.com/elizarov))  
