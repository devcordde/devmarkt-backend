package club.devcord.devmarkt.database.template;

import club.devcord.devmarkt.dto.template.Template;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TemplateImpl implements TemplateDAO {

  private final TemplateRepo repo;

  public TemplateImpl(TemplateRepo repo) {
    this.repo = repo;
  }

  @Override
  public InsertResult insert(Template template) {
    if (repo.existsByName(template.name())) {
      return InsertResult.DUPLICATED;
    }
    repo.save(Transformer.transform(template));
    return InsertResult.INSERTED;
  }

  @Override
  public ReplaceResult replace(Template template) {
    var opt = repo.findByName(template.name());
    if (opt.isEmpty()) {
      return ReplaceResult.NOT_FOUND;
    }
    var found = opt.get();
    repo.delete(found);
    repo.save(Transformer.transform(template, found.id()));
    return ReplaceResult.REPLACED;
  }

  @Override
  public DeleteResult delete(String name) {
    if (!repo.existsByName(name)) {
      return DeleteResult.NOT_FOUND;
    }
    repo.deleteByName(name);
    return DeleteResult.DELETED;
  }

  @Override
  public Optional<Template> find(String name) {
    return repo.findByName(name)
        .map(Transformer::transform);
  }

  @Override
  public Set<String> allNames() {
    return repo.findName();
  }
}
