package com.namaanda.termuxbridge.server

import com.namaanda.termuxbridge.accessibility.MyAccessibilityService
import fi.iki.elonen.NanoHTTPD
import java.net.URLDecoder

class HttpServer(
    private val service: MyAccessibilityService,
    port: Int
) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession?): Response {
        session ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Error")

        val method = session.method
        val uri = session.uri
        val params = session.parameters

        if (method == Method.GET) {
            when (uri) {
                "/status" -> {
                    return newFixedLengthResponse("Server is running.")
                }
                "/click" -> {
                    val text = params["text"]?.get(0)?.let { URLDecoder.decode(it, "UTF-8") }
                    if (text.isNullOrEmpty()) {
                        return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Parameter 'text' is missing.")
                    }
                    val success = service.clickOnText(text)
                    return newFixedLengthResponse("Click on '$text' executed. Success: $success")
                }
                else -> {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Unknown command.")
                }
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, MIME_PLAINTEXT, "Method not supported.")
    }
}