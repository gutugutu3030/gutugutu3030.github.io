package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class RootHtmlCreator extends HtmlCreator {

  /** {@inheritDoc} */
  @Override
  public void create(PebbleEngine engine, WebConfig config) throws IOException {
    final Map<String, Object> newMap = Map.of("directory", "./");
    Files.list(Paths.get(ClassLoader.getSystemResource(path).getPath()))
        .filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".pebl"))
        .forEach(
            p -> {
              PebbleTemplate compiledTemplate = engine.getTemplate(path + p.getFileName());
              write(
                  "../../" + p.getFileName().toString().replace("pebl", "html"),
                  compiledTemplate,
                  newMap);
            });
  }
}
