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

  /** HTML作成クラス */
  public List<HtmlCreator> htmlCreator;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s[contents=%s,career=%s,qualification=%s]"
        .formatted(getClass().getName(), contents, career, htmlCreator);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WebConfig) {
      var target = (WebConfig) obj;
      return Objects.equals(contents, target.contents);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(contents);
  }
}
