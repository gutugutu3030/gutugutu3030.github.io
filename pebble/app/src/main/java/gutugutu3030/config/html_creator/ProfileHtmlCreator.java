package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.util.Map;

public class ProfileHtmlCreator extends HtmlCreator {

  /** {@inheritDoc} */
  @Override
  public void create(PebbleEngine engine, WebConfig config) {
    final var newMap =
        Map.of("directory", "./", "career", config.career, "qualification", config.qualification);
    final PebbleTemplate compiledTemplate = engine.getTemplate(path);
    write("../../profile.html", compiledTemplate, newMap);
  }
}
