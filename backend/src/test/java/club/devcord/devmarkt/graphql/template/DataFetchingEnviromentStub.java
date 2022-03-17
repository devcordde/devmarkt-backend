/*
 * Copyright 2022 Contributors to the Devmarkt-Backend project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.devcord.devmarkt.graphql.template;

import graphql.GraphQLContext;
import graphql.cachecontrol.CacheControl;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.MergedField;
import graphql.execution.directives.QueryDirectives;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.SelectedField;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

public class DataFetchingEnviromentStub implements DataFetchingEnvironment {

  private final String[] fields;

  public DataFetchingEnviromentStub(String... fields) {
    this.fields = fields;
  }

  @Override
  public <T> T getSource() {
    return null;
  }

  @Override
  public Map<String, Object> getArguments() {
    return null;
  }

  @Override
  public boolean containsArgument(String name) {
    return false;
  }

  @Override
  public <T> T getArgument(String name) {
    return null;
  }

  @Override
  public <T> T getArgumentOrDefault(String name, T defaultValue) {
    return null;
  }

  @Override
  public <T> T getContext() {
    return null;
  }

  @Override
  public GraphQLContext getGraphQlContext() {
    return null;
  }

  @Override
  public <T> T getLocalContext() {
    return null;
  }

  @Override
  public <T> T getRoot() {
    return null;
  }

  @Override
  public GraphQLFieldDefinition getFieldDefinition() {
    return null;
  }

  @Override
  public List<Field> getFields() {
    return null;
  }

  @Override
  public MergedField getMergedField() {
    return null;
  }

  @Override
  public Field getField() {
    return null;
  }

  @Override
  public GraphQLOutputType getFieldType() {
    return null;
  }

  @Override
  public ExecutionStepInfo getExecutionStepInfo() {
    return null;
  }

  @Override
  public GraphQLType getParentType() {
    return null;
  }

  @Override
  public GraphQLSchema getGraphQLSchema() {
    return null;
  }

  @Override
  public Map<String, FragmentDefinition> getFragmentsByName() {
    return null;
  }

  @Override
  public ExecutionId getExecutionId() {
    return null;
  }

  @Override
  public DataFetchingFieldSelectionSet getSelectionSet() {
    return new DataFetchingSelectionSetStub(fields);
  }

  @Override
  public QueryDirectives getQueryDirectives() {
    return null;
  }

  @Override
  public <K, V> DataLoader<K, V> getDataLoader(String dataLoaderName) {
    return null;
  }

  @Override
  public DataLoaderRegistry getDataLoaderRegistry() {
    return null;
  }

  @Override
  public CacheControl getCacheControl() {
    return null;
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public OperationDefinition getOperationDefinition() {
    return null;
  }

  @Override
  public Document getDocument() {
    return null;
  }

  @Override
  public Map<String, Object> getVariables() {
    return null;
  }

  private static class DataFetchingSelectionSetStub implements DataFetchingFieldSelectionSet {

    private final String[] fieldNames;

    private DataFetchingSelectionSetStub(String[] fieldNames) {
      this.fieldNames = fieldNames;
    }

    @Override
    public boolean contains(String fieldGlobPattern) {
      return false;
    }

    @Override
    public boolean containsAnyOf(String fieldGlobPattern, String... fieldGlobPatterns) {
      return false;
    }

    @Override
    public boolean containsAllOf(String fieldGlobPattern, String... fieldGlobPatterns) {
      return false;
    }

    @Override
    public List<SelectedField> getFields() {
      return Stream.of(fieldNames)
          .map(SelectedFieldStub::new)
          .collect(Collectors.toList());
    }

    @Override
    public List<SelectedField> getImmediateFields() {
      return null;
    }

    @Override
    public List<SelectedField> getFields(String fieldGlobPattern, String... fieldGlobPatterns) {
      return null;
    }

    @Override
    public Map<String, List<SelectedField>> getFieldsGroupedByResultKey() {
      return null;
    }

    @Override
    public Map<String, List<SelectedField>> getFieldsGroupedByResultKey(String fieldGlobPattern,
        String... fieldGlobPatterns) {
      return null;
    }
  }

  private static class SelectedFieldStub implements SelectedField {

    private final String name;

    private SelectedFieldStub(String field) {
      this.name = field;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getQualifiedName() {
      return null;
    }

    @Override
    public String getFullyQualifiedName() {
      return null;
    }

    @Override
    public List<GraphQLObjectType> getObjectTypes() {
      return null;
    }

    @Override
    public List<String> getObjectTypeNames() {
      return null;
    }

    @Override
    public List<GraphQLFieldDefinition> getFieldDefinitions() {
      return null;
    }

    @Override
    public GraphQLOutputType getType() {
      return null;
    }

    @Override
    public Map<String, Object> getArguments() {
      return null;
    }

    @Override
    public int getLevel() {
      return 0;
    }

    @Override
    public boolean isConditional() {
      return false;
    }

    @Override
    public String getAlias() {
      return null;
    }

    @Override
    public String getResultKey() {
      return null;
    }

    @Override
    public SelectedField getParentField() {
      return null;
    }

    @Override
    public DataFetchingFieldSelectionSet getSelectionSet() {
      return null;
    }
  }
}
