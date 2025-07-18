/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.binder.engine.segment.dml.expression.type;

import com.google.common.collect.LinkedHashMultimap;
import org.apache.shardingsphere.infra.binder.engine.statement.SQLStatementBinderContext;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.hint.HintValueContext;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.ExistsSubqueryExpression;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.subquery.SubquerySegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.item.ProjectionsSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.dml.SelectStatement;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

class ExistsSubqueryExpressionBinderTest {
    
    private final DatabaseType databaseType = TypedSPILoader.getService(DatabaseType.class, "FIXTURE");
    
    @Test
    void assertBindExistsSubqueryExpression() {
        SelectStatement selectStatement = new SelectStatement(databaseType);
        selectStatement.setProjections(new ProjectionsSegment(0, 0));
        ExistsSubqueryExpression existsSubqueryExpression = new ExistsSubqueryExpression(0, 0, new SubquerySegment(0, 0, selectStatement, "t_test"));
        SQLStatementBinderContext binderContext = new SQLStatementBinderContext(mock(ShardingSphereMetaData.class), "foo_db", new HintValueContext(), mock());
        ExistsSubqueryExpression actual = ExistsSubqueryExpressionBinder.bind(existsSubqueryExpression, binderContext, LinkedHashMultimap.create());
        assertThat(actual.getStartIndex(), is(existsSubqueryExpression.getStartIndex()));
        assertThat(actual.getStopIndex(), is(existsSubqueryExpression.getStopIndex()));
        assertThat(actual.getText(), is("t_test"));
        assertThat(actual.getSubquery().getStartIndex(), is(existsSubqueryExpression.getSubquery().getStartIndex()));
        assertThat(actual.getSubquery().getStopIndex(), is(existsSubqueryExpression.getSubquery().getStopIndex()));
        assertThat(actual.getSubquery().getText(), is("t_test"));
    }
}
