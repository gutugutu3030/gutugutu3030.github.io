package gutugutu3030.config;

import gutugutu3030.config.html_creator.HtmlCreator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Web設定 */
public class WebConfig {

  /** メインページに載せるコンテンツリスト */
  public List<ContentsConfig> contents;

  /** キャリア */
  public List<Map<String, String>> career;

  /** 資格 */
  public List<QualificationConfig> qualification;

  /** 趣味で作った資料 */
  public String hobbyDataPath;

  /** HTML作成クラス */
  public List<HtmlCreator> htmlCreator;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s[contents=%s,career=%s,qualification=%s,hobbyDataPath=%s]"
        .formatted(getClass().getName(), contents, career, htmlCreator, hobbyDataPath);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WebConfig) {
      var target = (WebConfig) obj;
      return Objects.equals(contents, target.contents)
          && Objects.equals(career, target.career)
          && Objects.equals(qualification, target.qualification)
          && Objects.equals(hobbyDataPath, target.hobbyDataPath)
          && Objects.equals(htmlCreator, target.htmlCreator);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(contents, career, qualification, hobbyDataPath, htmlCreator);
  }
}
