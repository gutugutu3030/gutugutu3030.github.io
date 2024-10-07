package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/** コンテンツページの作成を行うクラス */
public class ContentsHtmlCreator extends HtmlCreator {
  /** {@inheritDoc} */
  @Override
  public void create(PebbleEngine engine, WebConfig config) throws IOException {
    Files.list(Paths.get(ClassLoader.getSystemResource("contents").getPath()))
        .filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".pebl"))
        .forEach(
            p -> {
              PebbleTemplate compiledTemplate = engine.getTemplate(path + p.getFileName());
              final var directory =
                  Arrays.stream(p.getFileName().toString().split("\\."))
                      .filter(s -> s.length() > 0)
                      .filter(s -> !s.equals("pebl"))
                      .collect(Collectors.joining("/"));
              final var newMap = new HashMap<String, Object>();
              newMap.put("directory", "./" + "../".repeat(directory.split("/").length + 1));

              try (FileWriter writer =
                  new FileWriter("../../contents/%s/index.html".formatted(directory))) {
                compiledTemplate.evaluate(writer, newMap);
              } catch (IOException e) {
                e.printStackTrace();
              }
            });
  }
}
