package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Map;

/** 星空写真サイトのHTML作成を行うクラス */
public class StarHtmlCreator extends HtmlCreator {

  /** サムネイル画像のパス */
  public String thumbnailImagePath;

  /** フルサイズ画像のパス */
  public String fullsizeImagePath;

  @Override
  public void create(PebbleEngine engine, WebConfig config) throws IOException {
    Comparator<Path> LastModifiedTimeComparator =
        Comparator.comparing(
            p -> {
              try {
                return Files.getLastModifiedTime(p);
              } catch (IOException e) {
                return FileTime.fromMillis(0);
              }
            });
    final var images =
        Files.list(Paths.get("../../" + thumbnailImagePath))
            .sorted(LastModifiedTimeComparator)
            .map(
                p -> {
                  return Map.of(
                      "thumbnail",
                      thumbnailImagePath + p.getFileName().toString(),
                      "fullsize",
                      fullsizeImagePath + p.getFileName().toString());
                })
            .toList()
            .reversed();
    final var newMap = Map.of("directory", "./", "images", images);
    final PebbleTemplate compiledTemplate = engine.getTemplate(path);
    write("../../star.html", compiledTemplate, newMap);
  }
}
