package de.visualdigits.photosite.data.datasource

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.data.http.safeCall
import de.visualdigits.photosite.domain.model.errorhandling.DataError
import de.visualdigits.photosite.domain.model.photosite.PageUi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLParameter

class PageDatasource(
    private val httpClient: HttpClient
) {
    companion object {

        private fun collectPageMap(page: PageUi, pageMap: MutableMap<String, PageUi> = mutableMapOf()): Map<String, PageUi> {
            pageMap[page.path] = page
            page.children.forEach { child ->
                child.parent = page
                collectPageMap(child, pageMap)
            }

            return pageMap
        }
    }

    suspend fun getPageTree(hostUrl: String?): Result<Map<String, PageUi>, DataError.Remote> {
        Logger.i("Fetching page tree...")
        val result = safeCall<PageUi> {
            httpClient.get(urlString = "$hostUrl/api/v1/pages")
        }

        return processTree(result)
    }

    suspend fun getNavigationTree(hostUrl: String?): Result<Map<String, PageUi>, DataError.Remote> {
        Logger.i("Fetching navigation tree...")
        val result = safeCall<PageUi> {
            httpClient.get(urlString = "$hostUrl/api/v1/navigation/main")
        }

        return processTree(result)
    }

    suspend fun getPage(hostUrl: String?, path: String): Result<PageUi, DataError.Remote> {
        Logger.i("Fetching page '$path'...")
        val result = safeCall<PageUi> {
            httpClient.get(urlString = "$hostUrl/api/v1/page?path=${path.encodeURLParameter()}")
        }

        return when (result) {
            is Result.Success -> {
                Result.Success(result.data)
            }

            is Result.Error -> {
                Logger.e("Error while fetching page '$path'...", result.throwable)
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
    }

    private fun processTree(result: Result<PageUi, DataError.Remote>): Result<Map<String, PageUi>, DataError.Remote> {
        return when (result) {
            is Result.Success -> {
                val pageTree = result.data
                val pageMap = collectPageMap(pageTree)
                Result.Success(pageMap)
            }

            is Result.Error -> {
                Logger.e("Error while processing page tree...", result.throwable)
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
    }
}
