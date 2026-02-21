/**
 * Service Worker for gutugutu3030 Portfolio PWA
 *
 * キャッシュ更新方法: CACHE_VERSION の値を変更してデプロイする
 */

const CACHE_VERSION = 'v1';

// =========================================================
// Precache: アプリシェル（オフラインで必ず動くべきファイル）
// =========================================================
const PRECACHE_NAME = 'precache-' + CACHE_VERSION;
const PRECACHE_URLS = [
    '/',
    '/index.html',
    '/offline.html',
    '/manifest.json',
    '/apple-touch-icon.png',
    '/icon.svg',
    '/main.bundle.js',
    // YAMLデータ
    '/content_list.yaml',
    '/profile.yaml',
    '/library.yaml',
    '/star.yaml',
];

// =========================================================
// Runtime cache names
// =========================================================
const CACHE_YAML      = 'yaml-'     + CACHE_VERSION;
const CACHE_CONTENTS  = 'contents-' + CACHE_VERSION;
const CACHE_STAR      = 'star-'     + CACHE_VERSION;

// =========================================================
// Install: Precache をすべて取得してキャッシュに格納
// =========================================================
self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(PRECACHE_NAME)
            .then((cache) => cache.addAll(PRECACHE_URLS))
            .then(() => self.skipWaiting())
    );
});

// =========================================================
// Activate: 古いバージョンのキャッシュを削除
// =========================================================
self.addEventListener('activate', (event) => {
    const currentCaches = [PRECACHE_NAME, CACHE_YAML, CACHE_CONTENTS, CACHE_STAR];
    event.waitUntil(
        caches.keys().then((cacheNames) =>
            Promise.all(
                cacheNames
                    .filter((name) => !currentCaches.includes(name))
                    .map((name) => caches.delete(name))
            )
        ).then(() => self.clients.claim())
    );
});

// =========================================================
// Fetch: リクエストの種類に応じてキャッシュ戦略を切り替え
// =========================================================
self.addEventListener('fetch', (event) => {
    const url = new URL(event.request.url);

    // 同一オリジン以外（YouTube等）は素通り
    if (url.origin !== self.location.origin) {
        return;
    }

    // ---- star/full/ → NetworkOnly（キャッシュしない）----
    if (url.pathname.startsWith('/star/full/')) {
        return; // ブラウザのデフォルト動作に委ねる
    }

    // ---- pdf/ → NetworkOnly（キャッシュしない）----------
    if (url.pathname.startsWith('/pdf/')) {
        return;
    }

    // ---- /star または /pdf へのページ遷移 ---------------
    // オフライン時は offline.html を返す
    if (event.request.mode === 'navigate' &&
        (url.pathname.startsWith('/star') || url.pathname.startsWith('/pdf'))) {
        event.respondWith(
            fetch(event.request).catch(() =>
                caches.match('/offline.html')
            )
        );
        return;
    }

    // ---- その他のページ遷移（SPA）-----------------------
    // オフライン時は index.html を返す
    if (event.request.mode === 'navigate') {
        event.respondWith(
            fetch(event.request).catch(() =>
                caches.match('/index.html')
            )
        );
        return;
    }

    // ---- YAML → NetworkFirst（5秒タイムアウト）----------
    if (url.pathname.endsWith('.yaml')) {
        event.respondWith(networkFirst(event.request, CACHE_YAML, 5000));
        return;
    }

    // ---- contents/ → CacheFirst -------------------------
    if (url.pathname.startsWith('/contents/')) {
        event.respondWith(cacheFirst(event.request, CACHE_CONTENTS));
        return;
    }

    // ---- star/small/ → NetworkFirst（8秒タイムアウト）---
    if (url.pathname.startsWith('/star/small/')) {
        event.respondWith(networkFirst(event.request, CACHE_STAR, 8000));
        return;
    }

    // ---- その他（main.bundle.js, icon等）→ CacheFirst ---
    event.respondWith(cacheFirst(event.request, PRECACHE_NAME));
});

// =========================================================
// ヘルパー: CacheFirst
// =========================================================
async function cacheFirst(request, cacheName) {
    const cached = await caches.match(request);
    if (cached) return cached;
    try {
        const response = await fetch(request);
        if (response.ok) {
            const cache = await caches.open(cacheName);
            cache.put(request, response.clone());
        }
        return response;
    } catch {
        return new Response('Network error', { status: 503 });
    }
}

// =========================================================
// ヘルパー: NetworkFirst（タイムアウト付き）
// =========================================================
async function networkFirst(request, cacheName, timeoutMs) {
    const cache = await caches.open(cacheName);

    const fetchPromise = fetch(request).then((response) => {
        if (response.ok) {
            cache.put(request, response.clone());
        }
        return response;
    });

    const timeoutPromise = new Promise((_, reject) =>
        setTimeout(() => reject(new Error('timeout')), timeoutMs)
    );

    try {
        return await Promise.race([fetchPromise, timeoutPromise]);
    } catch {
        const cached = await cache.match(request);
        return cached || new Response('Offline', { status: 503 });
    }
}

