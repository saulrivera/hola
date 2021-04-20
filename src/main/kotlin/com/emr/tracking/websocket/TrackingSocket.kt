package com.emr.tracking.websocket

import com.emr.tracking.model.PatientBeaconRegistry
import com.emr.tracking.model.StreamReading
import com.emr.tracking.model.StreamSocket
import com.emr.tracking.model.StreamSocketGateway
import com.emr.tracking.repository.GatewayRepository
import com.emr.tracking.repository.MongoPatientBeaconRegistry
import com.emr.tracking.repository.MongoPatientRepository
import com.emr.tracking.repository.StreamRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.mongodb.internal.connection.SocketStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.HashSet
import kotlin.jvm.Throws

class User(val id: Long, val name: String)
class Message(val type: String, val data: String)

@Component
class TrackingSocket(
    private val streamRepository: StreamRepository,
    private val mongoPatientBeaconRegistry: MongoPatientBeaconRegistry,
    private val mongoPatientRepository: MongoPatientRepository,
    private val gatewayRepository: GatewayRepository,
) : TextWebSocketHandler() {
    companion object {
        val log: Logger = LoggerFactory.getLogger(TrackingSocket::class.java)
    }

    var sessionList: MutableSet<WebSocketSession> = Collections.synchronizedSet(HashSet<WebSocketSession>())
    private var uids = AtomicLong(0)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = ObjectMapper().readTree(message.payload)

        when (json.get("type").asText()) {
            "join" -> {
                val user = User(uids.getAndIncrement(), json.get("data").asText())
                sessionList.add(session)
                broadcastToOthers(session, Message("join", Gson().toJson(user)))
                val activeBeaconStreamsMessage = Message("activeBeacons", Gson().toJson(activeBeaconsStreams()))
                emit(session, activeBeaconStreamsMessage)
            }
            "say" -> {
                broadcast(Message("say", json.get("data").asText()))
            }
        }
    }

    private fun emit(session: WebSocketSession, msg: Message) {
        session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
    }

    private fun broadcast(msg: Message) {
        synchronized(sessionList) {
            sessionList.forEach { emit(it, msg) }
        }
    }
    private fun broadcastToOthers(me: WebSocketSession, msg: Message)  {
        synchronized(sessionList) {
            sessionList.filterNot { it == me }.forEach { emit(it, msg) }
        }
    }

    fun broadcastTracking(data: Any) {
        val message = Message("tracing", Gson().toJson(data))
        log.info("Message sent: $message")
        broadcast(message)
    }

    fun emitBeaconDetachment(streamSocket: StreamSocket) {
        val message = Message("detach", Gson().toJson(streamSocket))
        broadcast(message)
    }

    fun emitBeaconUpdate(streamSocket: StreamSocket) {
        val message = Message("update", Gson().toJson(streamSocket))
        broadcast(message)
    }

    fun activeBeaconsStreams(): List<StreamSocket> {
        val records = mongoPatientBeaconRegistry.findAll().filter { it.active }

        return records.mapNotNull(fun(it: PatientBeaconRegistry): StreamSocket? {
            val streamMemory = streamRepository.findByBeaconMac(it.beaconId) ?: return null
            val patient = mongoPatientRepository.findById(it.patientId).get()
            val gateway = gatewayRepository.findByMac(streamMemory.gatewayId) ?: return null

            return StreamSocket(
                streamMemory.trackingId,
                streamMemory.rssi,
                streamMemory.calibratedRssi1m,
                StreamSocketGateway(
                    gateway.uniqueId,
                    listOf(gateway.position.first, gateway.position.second, gateway.floor.toDouble())
                ),
                patient
            )
        })
    }
}
