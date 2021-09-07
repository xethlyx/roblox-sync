package com.xethlyx.robloxsync.bungee

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RobloxData(robloxData: JsonObject) {
    var description: String = robloxData.get("description")!!.asString
    var created: String = robloxData.get("created")!!.asString
    var isBanned: Boolean = robloxData.get("isBanned")!!.asBoolean
    var id: Number = robloxData.get("id")!!.asNumber
    var username: String = robloxData.get("name")!!.asString
    var displayName: String = robloxData.get("displayName")!!.asString

    companion object {
        fun from(username: String): RobloxData? {
            val request = URL("https://api.roblox.com/users/get-by-username?username=$username").openConnection()
            request.connect()

            val jsonParser = JsonParser()
            val jsonObject = jsonParser.parse(InputStreamReader(request.content as InputStream)).asJsonObject

            if (jsonObject.has("success") && !jsonObject.get("success").asBoolean) {
                return null
            }

            val id = jsonObject.get("Id").asNumber
            return from(id)
        }

        fun from(id: Number): RobloxData? {
            val request = URL("https://users.roblox.com/v1/users/$id").openConnection() as HttpURLConnection
            request.connect()

            if (request.responseCode != 200) return null

            val jsonParser = JsonParser()
            val robloxData = jsonParser.parse(InputStreamReader(request.content as InputStream)).asJsonObject
            return RobloxData(robloxData)
        }
    }
}