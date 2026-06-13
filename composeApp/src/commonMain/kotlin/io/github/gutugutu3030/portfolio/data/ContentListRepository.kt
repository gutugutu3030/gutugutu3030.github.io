package io.github.gutugutu3030.portfolio.data

/**
 * 制作物一覧の取得を担当するリポジトリ。
 */
interface ContentListRepository {

    /**
     * 制作物一覧を非同期で取得する。
     *
     * @return 取得した [ContentListConfig]
     */
    suspend fun load(): ContentListConfig
}
