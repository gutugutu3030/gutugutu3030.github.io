package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.util.Map;
import java.util.function.BiFunction;

/** インデックスページの作成を行うクラス */
public class IndexHtmlCreator extends HtmlCreator {

  /** {@inheritDoc} */
  @Override
  public void create(PebbleEngine engine, WebConfig config) {
    final BiFunction<String, String, String> directoryToUrl =
        (file, directory) -> "contents/%s/%s".formatted(directory.replace(".", "/"), file);

    final var contentsList =
        config.contents.stream().map(c -> c.getContentsMap(directoryToUrl)).toList().reversed();
    final var newMap = Map.of("directory", "./", "contents", contentsList);
    final PebbleTemplate compiledTemplate = engine.getTemplate(path);
    write("../../index.html", compiledTemplate, newMap);
  }
}
