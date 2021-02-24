import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    application
}


group = "me.paradox"
version = "0"


repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

dependencies {
    val ktorVersion = "1.5.1"
    val serializationVersion = "1.0.1"
    testImplementation(kotlin("test-junit"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation ("org.slf4j:slf4j-simple:1.6.1")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$serializationVersion")

    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")




//    testImplementation ("org.slf4j:slf4j-simple:1.6.1")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    )
}

application {
    mainClassName = "ServerKt"
}