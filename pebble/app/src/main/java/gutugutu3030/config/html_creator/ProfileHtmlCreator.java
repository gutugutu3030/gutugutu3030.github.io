package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class ProfileHtmlCreator extends HtmlCreator {

  /**
   * {@inheritDoc}
   *
   * @throws IOException
   */
  @Override
  public void create(PebbleEngine engine, WebConfig config) throws IOException {
    final var hobbyList =
        Files.list(Paths.get("../../" + config.hobbyDataPath))
            .filter(Files::isRegularFile)
            .map(Path::getFileName)
            .map(Path::toString)
            .collect(Collectors.toList());
    final var newMap =
        Map.of(
            "directory",
            "./",
            "career",
            config.career,
            "qualification",
            config.qualification,
            "hobbyDataPath",
            config.hobbyDataPath,
            "hobbyList",
            hobbyList);
    final PebbleTemplate compiledTemplate = engine.getTemplate(path);

    write("../../profile.html", compiledTemplate, newMap);
  }
}
