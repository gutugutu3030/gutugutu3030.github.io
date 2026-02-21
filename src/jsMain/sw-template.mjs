/**
 * Service Worker テンプレート
 * ビルド時に workbox-webpack-plugin (InjectManifest) が
 * self.__WB_MANIFEST を precache リストに置換して sw.js を生成します。
 *
 * .mjs 拡張子により webpack 5 が ESModule として処理するため
 * import 文がそのまま使用できます。
 *
 * このファイルは src/jsMain/ に置くことで resources/ 経由で
 * docs/ にコピーされないようにしています。
 */

import { precacheAndRoute, cleanupOutdatedCaches } from 'workbox-precaching';
import { registerRoute, NavigationRoute } from 'workbox-routing';
import { NetworkFirst, CacheFirst, NetworkOnly } from 'workbox-strategies';
import { ExpirationPlugin } from 'workbox-expiration';
import { CacheableResponsePlugin } from 'workbox-cacheable-response';

// =====================================================================
// Precache（ビルド成果物: main.bundle.js, index.html, YAML, 静的リソース等）
// workbox-webpack-plugin が self.__WB_MANIFEST を自動生成した manifest に置換する
// =====================================================================
precacheAndRoute(self.__WB_MANIFEST);

// 古いキャッシュを自動削除
cleanupOutdatedCaches();

// Service Worker を即座に有効化（iOS 含む全クライアントに適用）
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', (event) => {
    event.waitUntil(self.clients.claim());
});

// =====================================================================
// ランタイムキャッシュ戦略
// =====================================================================

// ① YAML ファイル → NetworkFirst（最新データ優先、オフライン時はキャッシュで表示）
registerRoute(
    ({ url }) => url.pathname.endsWith('.yaml'),
    new NetworkFirst({
        cacheName: 'yaml-cache',
        networkTimeoutSeconds: 5,
        plugins: [
            new ExpirationPlugin({
                maxEntries: 50,
                maxAgeSeconds: 60 * 60 * 24 * 30, // 30日
            }),
        ],
    })
);

// ② contents/ 以下の画像・データ → CacheFirst（一度取得したら長期保持）
registerRoute(
    ({ url }) => url.pathname.startsWith('/contents/'),
    new CacheFirst({
        cacheName: 'contents-cache',
        plugins: [
            new CacheableResponsePlugin({ statuses: [0, 200] }),
            new ExpirationPlugin({
                maxEntries: 500,
                maxAgeSeconds: 60 * 60 * 24 * 365, // 1年
            }),
        ],
    })
);

// ③ star/small/ のサムネイル → NetworkFirst
//    オンライン時はキャッシュを更新しつつ表示、オフライン時はキャッシュがあれば表示
registerRoute(
    ({ url }) => url.pathname.startsWith('/star/small/'),
    new NetworkFirst({
        cacheName: 'star-small-cache',
        networkTimeoutSeconds: 8,
        plugins: [
            new CacheableResponsePlugin({ statuses: [0, 200] }),
            new ExpirationPlugin({
                maxEntries: 60,
                maxAgeSeconds: 60 * 60 * 24 * 7, // 7日（意図的に短く）
            }),
        ],
    })
);

// ④ star/full/ のフル画像 → NetworkOnly（一切キャッシュしない）
registerRoute(
    ({ url }) => url.pathname.startsWith('/star/full/'),
    new NetworkOnly()
);

// ⑤ PDF → NetworkOnly（一切キャッシュしない）
registerRoute(
    ({ url }) => url.pathname.startsWith('/pdf/'),
    new NetworkOnly()
);

// ⑥ YouTube → NetworkOnly
registerRoute(
    ({ url }) => url.hostname === 'www.youtube.com' || url.hostname === 'youtube.com',
    new NetworkOnly()
);

// =====================================================================
// ページナビゲーション（SPA ルーティング）のフォールバック
// =====================================================================
const OFFLINE_URL = '/offline.html';

registerRoute(
    new NavigationRoute(
        async (params) => {
            const { url, request } = params;
            const isStarOrPdf =
                url.pathname.startsWith('/star') ||
                url.pathname.startsWith('/pdf');

            if (isStarOrPdf) {
                // オンラインなら通常通り fetch、オフラインなら offline.html を返す
                try {
                    return await fetch(request);
                } catch {
                    const offlineResponse = await caches.match(OFFLINE_URL);
                    if (offlineResponse) return offlineResponse;
                    return new Response(
                        '<h1>オフラインです</h1><p><a href="/">トップへ戻る</a></p>',
                        { status: 503, headers: { 'Content-Type': 'text/html; charset=utf-8' } }
                    );
                }
            }

            // その他のページ（SPA） → オフライン時は precache された index.html を返す
            try {
                return await fetch(request);
            } catch {
                const cachedIndex = await caches.match('/index.html');
                return cachedIndex || Response.error();
            }
        }
    )
);

