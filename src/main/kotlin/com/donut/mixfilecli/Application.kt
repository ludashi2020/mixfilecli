package com.donut.mixfilecli

import com.donut.mixfilecli.plugins.configureHTTP
import com.donut.mixfilecli.plugins.configureRouting
import com.donut.mixfilecli.plugins.configureSerialization
import com.donut.mixfiledesktop.server.serverPort
import com.donut.mixfiledesktop.server.startServer
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addFileSource
import io.ktor.server.application.*
import kotlinx.coroutines.MainScope
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

val appScope by lazy { MainScope() }

data class Config(
    val uploader: String = "A1",
    val upload_task: Int = 10,
    val download_task: Int = 5,
    val port: Int = 4719,
    val upload_retry: Int = 3,
    val custom_url: String = "",
    val custom_referer: String = "",
    val host: String = "0.0.0.0"
)

var config: Config = Config()


@OptIn(ExperimentalHoplite::class)
fun main(args: Array<String>) {
    checkConfig()
    config = ConfigLoaderBuilder.default()
        .addFileSource("config.yml")
        .withExplicitSealedTypes()
        .build()
        .loadConfigOrThrow<Config>()
    startServer()
}

fun checkConfig() {
    val currentDir = System.getProperty("user.dir")
    val inputStream: InputStream? = object {}.javaClass.getResourceAsStream("/config.yml")
    val outputFile = File(currentDir, "config.yml")
    if (!outputFile.exists()) {
        FileOutputStream(outputFile).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
    }
}

@OptIn(ExperimentalHoplite::class)
fun Application.module() {
    serverPort = config.port
    configureSerialization()
    configureHTTP()
    configureRouting()
}
