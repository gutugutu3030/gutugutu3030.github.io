package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/** 星空写真サイトのHTML作成を行うクラス */
public class StarHtmlCreator extends HtmlCreator {

  /** サムネイル画像のパス */
  public String thumbnailImagePath;

  /** フルサイズ画像のパス */
  public String fullsizeImagePath;

  /** ビデオ挿入のパス */
  public Map<String, String> video = new HashMap<>();

  @Override
  public void create(PebbleEngine engine, WebConfig config) throws IOException {
    final var images =
        Files.list(Paths.get("../../" + thumbnailImagePath))
            .sorted(Comparator.comparing(p -> p.getFileName().toString()))
            .map(
                p -> {
                  final var videoPath = video.get(p.getFileName().toString());
                  if (videoPath != null) {
                    return Map.of(
                        "type",
                        "video",
                        "thumbnail",
                        thumbnailImagePath + p.getFileName().toString(),
                        "fullsize",
                        videoPath);
                  }
                  if (Paths.get("../../" + fullsizeImagePath + p.getFileName().toString())
                      .toFile()
                      .exists()) {
                    return Map.of(
                        "type",
                        "image",
                        "thumbnail",
                        thumbnailImagePath + p.getFileName().toString(),
                        "fullsize",
                        fullsizeImagePath + p.getFileName().toString());
                  }
                  return Map.of(
                      "type",
                      "image",
                      "thumbnail",
                      thumbnailImagePath + p.getFileName().toString(),
                      "fullsize",
                      thumbnailImagePath + p.getFileName().toString());
                })
            .toList()
            .reversed();
    final var newMap = Map.of("directory", "./", "images", images);
    final PebbleTemplate compiledTemplate = engine.getTemplate(path);
    write("../../star.html", compiledTemplate, newMap);
  }
}
