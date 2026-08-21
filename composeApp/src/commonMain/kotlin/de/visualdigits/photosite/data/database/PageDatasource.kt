package de.visualdigits.photosite.data.database

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.data.http.safeCall
import de.visualdigits.photosite.domain.model.errorhandling.DataError
import de.visualdigits.photosite.domain.model.photosite.PageUi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class PageDatasource(
    private val httpClient: HttpClient
) {

    val jsonMapper = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    suspend fun getPageTree(hostUrl: String?): Result<PageUi, DataError.Remote> {
        val result =  safeCall<String> {
            httpClient.get(urlString = "$hostUrl/api/v1/pages")
        }
        return if (result is Result.Success) {
            val json = result.data
            val pageTree = jsonMapper.decodeFromString<PageUi>(json)
            Result.Success(pageTree)
        } else {
            Result.Error(DataError.Remote.SERIALIZATION)
        }
    }
}
