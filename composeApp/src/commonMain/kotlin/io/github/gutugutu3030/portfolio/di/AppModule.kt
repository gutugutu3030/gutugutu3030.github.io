package io.github.gutugutu3030.portfolio.di

import io.github.gutugutu3030.portfolio.data.ContentListRepository
import io.github.gutugutu3030.portfolio.data.ContentListRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

/**
 * アプリケーション全体の依存関係を提供するオブジェクト。
 */
object AppModule {

    /**
     * 共有で使用する [HttpClient] を生成する。
     *
     * @return 新しい [HttpClient] インスタンス
     */
    fun createHttpClient(): HttpClient = HttpClient(Js)

    /**
     * [ContentListRepository] の実装を生成する。
     *
     * @param httpClient データ取得に使用する [HttpClient]
     * @return 新しい [ContentListRepository] インスタンス
     */
    fun createContentListRepository(httpClient: HttpClient): ContentListRepository {
        return ContentListRepositoryImpl(httpClient)
    }
}
