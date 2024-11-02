package gutugutu3030.config;

import java.util.Objects;

/** 資格設定 */
public class QualificationConfig {

  /** 資格名 */
  public String description;

  /** リンク */
  public String url;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s[description=%s, url=%s]".formatted(getClass().getName(), description, url);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof QualificationConfig) {
      var target = (QualificationConfig) obj;
      return description.equals(target.description) && url.equals(target.url);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(description, url);
  }
}
