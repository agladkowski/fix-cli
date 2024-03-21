package org.gladkowski

import ch.qos.logback.classic.Level
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import quickfix.*
import quickfix.field.MsgType
import quickfix.field.Password
import quickfix.field.Username
import quickfix.fix44.MessageCracker
import java.io.ByteArrayInputStream

fun main(args: Array<String>) {
    val parser = ArgParser("fix-cli")
    val host by parser.option(ArgType.String, description = "Host").required()
    val port by parser.option(ArgType.Int, description = "Port").required()
    val senderCompId by parser.option(ArgType.String, shortName = "s", fullName = "sender", description = "SenderCompId").required()
    val targetCompId by parser.option(ArgType.String, shortName = "t", fullName = "target", description = "TargetCompId").required()
    val username by parser.option(ArgType.String, shortName = "u", description = "Username")
    val password by parser.option(ArgType.String, shortName = "p", description = "Password")
    val fixVersion by parser.option(ArgType.String, fullName = "fixVer", description = "Fix protocol version, default 4.4")
    val verbose by parser.option(ArgType.Boolean, fullName = "verbose", description = "Enable verbose logging mode")
    parser.parse(args)
    enableVerboseDebug(verbose)

    // Start fix initiator
    val sessionSettings = generateQuickFixJConfiguration(host, port, senderCompId, targetCompId, fixVersion)
    val fixSessionInitiator = SocketInitiator(
        FixClientApp(username, password),
        NoopStoreFactory(),
        sessionSettings,
        { _: SessionID? -> null },
        DefaultMessageFactory()
    )
    fixSessionInitiator.start()

    // Schedule shutdown hook
    object : Thread() {
        override fun run() {
            name = "fix-cli shutdown hook"
            ShutdownUtil().await()
            fixSessionInitiator.stop()
        }
    }.start()
}

private fun enableVerboseDebug(verbose: Boolean?) {
    val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    root.level = Level.ERROR
    if (verbose == true) {
        root.level = Level.DEBUG
    }
}

class FixClientApp(private val username: String?, private val password: String?) : MessageCracker(), Application {
    override fun onCreate(session: SessionID?) {
        println("Created : $session")
    }

    override fun onLogon(session: SessionID?) {
        println("Logon   : $session")
    }

    override fun onLogout(session: SessionID?) {
        println("Logout  : $session")
    }

    override fun toAdmin(message: Message?, session: SessionID?) {
        password?.let {
            if (it.isNotBlank())
                setLogonValue(message, Password.FIELD, it)
        }
        username?.let {
            if (it.isNotBlank())
                setLogonValue(message, Username.FIELD, it)
        }
        println("Sent    : ${message?.javaClass?.simpleName} $message")
    }

    private fun setLogonValue(message: Message?, fieldType: Int, fieldValue: String) {
        try {
            val msgType = message!!.header.getString(MsgType.FIELD)
            if (MsgType.LOGON.compareTo(msgType) == 0) {
                message.setString(fieldType, fieldValue) // Turns out setting this in quickfixj config does not work
            }
        } catch (e: FieldNotFound) {
            throw RuntimeException(e)
        }
    }

    override fun fromAdmin(message: Message?, sesison: SessionID?) {
        println("Received: ${message?.javaClass?.simpleName} $message")
    }

    override fun toApp(message: Message?, session: SessionID?) {
        println("Sent    : $message")
    }

    override fun fromApp(message: Message?, session: SessionID?) {
        println("Received: $message")
    }
}

@Throws(ConfigError::class)
private fun generateQuickFixJConfiguration(
    host: String, port: Int, senderCompId: String, targetCompId: String, fixVersion: String?): SessionSettings {
    // Intentionally embedding fragment of settings.ini, so we can easily add options listed below:
    // https://www.quickfixj.org/usermanual/2.3.0/usage/configuration.html
    val settingsIni = String.format(
        """
                [DEFAULT]
                ConnectionType=initiator
                HeartBtInt=10
                BeginString=FIX.%s
                StartTime=00:00:00
                EndTime=00:00:00
                ReconnectInterval=10
                SocketConnectHost=%s
                [SESSION]
                TargetCompID=%s
                SenderCompID=%s
                SocketConnectPort=%s           
            """.trimIndent(),fixVersion ?: "4.4", host, targetCompId, senderCompId, port
    )
    println("Generated quickfixj settings:\n\n$settingsIni")
    return SessionSettings(ByteArrayInputStream(settingsIni.toByteArray()))
}
