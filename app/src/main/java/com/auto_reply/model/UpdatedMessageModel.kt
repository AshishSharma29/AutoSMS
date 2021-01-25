package com.auto_reply.model

import java.io.Serializable

data class UpdatedMessageModel(
    var incomingMessage: String,
    var outgoingMessage: String,
    var missedMessage: String,
    var webSiteUrl: String
) : Serializable