package io.github.gutugutu3030.portfolio.data

import com.charleskorn.kaml.Yaml
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * [ContentListRepository] の Ktor Client を使った実装。
 *
 * @property httpClient 静的リソースを取得するための [HttpClient]
 */
class ContentListRepositoryImpl(
    private val httpClient: HttpClient
) : ContentListRepository {

    /**
     * `content_list.yaml` を取得し、[ContentListConfig] としてデコードする。
     *
     * @return 取得した [ContentListConfig]
     */
    override suspend fun load(): ContentListConfig {
        val yamlText: String = httpClient.get("content_list.yaml").body()
        return Yaml.default.decodeFromString(ContentListConfig.serializer(), yamlText)
    }
}
