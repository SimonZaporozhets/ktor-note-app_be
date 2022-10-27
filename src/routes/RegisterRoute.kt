package com.amdroiddevs.routes

import com.amdroiddevs.data.checkIfUserExists
import com.amdroiddevs.data.registerUser
import com.amdroiddevs.security.getHashWithSalt
import data.collections.User
import data.requests.AccountRequest
import data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.registerRoute() {
    route("/register") {
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<AccountRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                val userExists = checkIfUserExists(request.email)

                if (!userExists) {
                    if (registerUser(User(request.email, getHashWithSalt(request.password)))) {
                        call.respond(OK, SimpleResponse(true, "Successfully created account!"))
                    } else {
                        call.respond(OK, SimpleResponse(false, "An unknown error occured"))
                    }
                } else {
                    call.respond(OK, SimpleResponse(false, "A user with that E-Mail already exists"))
                }
            }
        }
    }
}