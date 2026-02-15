package io.github.gutugutu3030.util

import io.kvision.html.Tag
import io.kvision.html.code
import io.kvision.html.link

/**
 * 文字列内のMarkdown形式のリンクをHTMLタグに変換する拡張関数
 * @receiver 変換対象の文字列
 * @param tag 変換後のHTMLタグ
 * @return Markdownリンクが変換されたHTMLタグ
 */
fun String.parseMarkdown(tag: Tag): Tag {

    val matches = MarkdownRule.entries.flatMap { rule ->
        rule.pattern.findAll(this).map{
            Triple(rule, it, it.range.first)
        }
    }.sortedBy { it.third }

    var lastIndex = 0
    matches.forEach { (rule, matchResult, third) ->
        if(third > lastIndex){
            tag.apply {
                +this@parseMarkdown.substring(lastIndex, third)
            }
        }
        rule.transform(tag, matchResult)
        lastIndex = matchResult.range.last + 1
    }
    if(lastIndex < this.length){
        tag.apply {
            +substring(lastIndex)
        }
    }
    return tag
}

/**
 * Markdown形式のルールを定義するインターフェース
 * @property pattern Markdown形式の正規表現パターン
 * @property transform マッチしたMarkdownをHTMLタグに変換する関数
 */
private enum class MarkdownRule(
    val pattern: Regex,
    val transform: (Tag, MatchResult) -> Unit
){
    /**
     * Markdown形式のリンクをHTMLタグに変換するルール
     */
    LINK("""\[([^\]]+)\]\(([^)]+)\)""".toRegex(), { tag, matchResult ->
        val text = matchResult.groupValues[1]
        val url = matchResult.groupValues[2]
        tag.link(text, url = url)
    }),
    /**
     * Markdown形式のコードをHTMLタグに変換するルール
     */
    CODE("""`([^`]+)`""".toRegex(), { tag, matchResult ->
        val code = matchResult.groupValues[1]
        tag.code(code)
    })
}
