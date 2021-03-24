package com.emr.tracking.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

class TrackingSocket : TextWebSocketHandler() {
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
}
