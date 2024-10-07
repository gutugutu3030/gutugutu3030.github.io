package gutugutu3030.config.html_creator;

import gutugutu3030.config.WebConfig;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/** ファイルの作成を行うクラス */
public abstract class HtmlCreator {

  /** ファイルのパス */
  public String path;

  /**
   * ファイルの作成を行う
   *
   * @param engine PebbleEngine
   * @param config WebConfig
   */
  public abstract void create(PebbleEngine engine, WebConfig config) throws IOException;

  /**
   * ファイルの作成を行う
   *
   * @param fileName ファイル名
   * @param compiledTemplate コンパイルされたテンプレート
   * @param newMap テンプレートに渡す変数
   */
  protected void write(
      String fileName, PebbleTemplate compiledTemplate, Map<String, Object> newMap) {
    try (FileWriter writer = new FileWriter(fileName)) {
      compiledTemplate.evaluate(writer, newMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
