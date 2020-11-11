import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    java
    application
}
group = "me.jbc"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    implementation(group="com.radiance.tonclient", name = "ton-client-binding", version = "1.0-SNAPSHOT", classifier = "jar-with-dependencies")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}