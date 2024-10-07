package gutugutu3030.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/** コンテンツリスト */
public class ContentsConfig {

  /** ディレクトリ */
  public String directory;

  /** コンテンツリスト */
  public String name;

  /** 開発期間 */
  public String date;

  /** HTMLに飛ばさない場合のURL */
  public String url;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s[directory=%s, name=%s, date=%s, url=%s]"
        .formatted(getClass().getName(), directory, name, date, url);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ContentsConfig) {
      var target = (ContentsConfig) obj;
      return Objects.equals(directory, target.directory)
          && Objects.equals(name, target.name)
          && Objects.equals(date, target.date)
          && Objects.equals(url, target.url);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(directory, name, date, url);
  }

  /**
   * コンテンツリストをMapに変換
   *
   * @param directoryToUrl ファイル名とディレクトリからURLに変換する関数
   * @return Map
   * @throws IOException
   */
  public Map<String, String> getContentsMap(BiFunction<String, String, String> directoryToUrl) {
    try {
      final var thumbnail =
          Files.list(Paths.get("../../" + directoryToUrl.apply("", directory.replace(".", "/"))))
              .filter(Files::isRegularFile)
              .map(Path::getFileName)
              .map(Path::toString)
              .filter(name -> name.contains("thumbnail."))
              .findAny()
              .orElseThrow();
      return Map.of(
          "url",
          Optional.ofNullable(url).orElseGet(() -> directoryToUrl.apply("index.html", directory)),
          "name",
          name,
          "date",
          date,
          "thumbnail",
          directoryToUrl.apply(thumbnail, directory));
    } catch (Exception e) {

      throw new RuntimeException("thumbnailが見つかりません: %s".formatted(directory));
    }
  }
}
