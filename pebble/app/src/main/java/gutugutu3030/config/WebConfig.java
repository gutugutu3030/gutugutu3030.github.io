package gutugutu3030.config;

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
  public List<String> qualification;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s[contents=%s]".formatted(getClass().getName(), contents);
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
