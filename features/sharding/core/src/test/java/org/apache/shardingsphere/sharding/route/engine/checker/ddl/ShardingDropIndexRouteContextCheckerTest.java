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

package org.apache.shardingsphere.sharding.route.engine.checker.ddl;

import org.apache.shardingsphere.infra.binder.context.statement.type.CommonSQLStatementContext;
import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereTable;
import org.apache.shardingsphere.infra.route.context.RouteContext;
import org.apache.shardingsphere.infra.route.context.RouteMapper;
import org.apache.shardingsphere.infra.route.context.RouteUnit;
import org.apache.shardingsphere.infra.session.query.QueryContext;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sharding.exception.connection.ShardingDDLRouteException;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.apache.shardingsphere.sharding.rule.ShardingTable;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.IndexNameSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.IndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.ddl.index.DropIndexStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShardingDropIndexRouteContextCheckerTest {
    
    private final DatabaseType databaseType = TypedSPILoader.getService(DatabaseType.class, "PostgreSQL");
    
    @Mock
    private ShardingRule shardingRule;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ShardingSphereDatabase database;
    
    @Mock
    private RouteContext routeContext;
    
    @Mock
    private QueryContext queryContext;
    
    @Test
    void assertCheckWithSameRouteResultShardingTableIndexForPostgreSQL() {
        DropIndexStatement sqlStatement = new DropIndexStatement(databaseType);
        sqlStatement.getIndexes().add(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("t_order_index"))));
        sqlStatement.getIndexes().add(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("t_order_index_new"))));
        ShardingSphereTable table = mock(ShardingSphereTable.class);
        when(table.getName()).thenReturn("t_order");
        when(database.getSchema("public").getAllTables()).thenReturn(Collections.singleton(table));
        when(database.getSchema("public").getTable("t_order")).thenReturn(table);
        when(table.containsIndex("t_order_index")).thenReturn(true);
        when(shardingRule.isShardingTable("t_order")).thenReturn(true);
        when(shardingRule.getShardingTable("t_order")).thenReturn(new ShardingTable(Arrays.asList("ds_0", "ds_1"), "t_order"));
        Collection<RouteUnit> routeUnits = new LinkedList<>();
        routeUnits.add(new RouteUnit(new RouteMapper("ds_0", "ds_0"), Collections.singletonList(new RouteMapper("t_order", "t_order_0"))));
        routeUnits.add(new RouteUnit(new RouteMapper("ds_1", "ds_1"), Collections.singletonList(new RouteMapper("t_order", "t_order_0"))));
        when(routeContext.getRouteUnits()).thenReturn(routeUnits);
        when(queryContext.getSqlStatementContext()).thenReturn(new CommonSQLStatementContext(sqlStatement));
        assertDoesNotThrow(() -> new ShardingDropIndexRouteContextChecker().check(shardingRule, queryContext, database, mock(ConfigurationProperties.class), routeContext));
    }
    
    @Test
    void assertCheckWithDifferentRouteResultShardingTableIndexForPostgreSQL() {
        DropIndexStatement sqlStatement = new DropIndexStatement(databaseType);
        sqlStatement.getIndexes().add(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("t_order_index"))));
        sqlStatement.getIndexes().add(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("t_order_index_new"))));
        ShardingSphereTable table = mock(ShardingSphereTable.class);
        when(table.getName()).thenReturn("t_order");
        when(database.getSchema("public").getAllTables()).thenReturn(Collections.singleton(table));
        when(database.getSchema("public").getTable("t_order")).thenReturn(table);
        when(table.containsIndex("t_order_index")).thenReturn(true);
        when(shardingRule.isShardingTable("t_order")).thenReturn(true);
        when(shardingRule.getShardingTable("t_order")).thenReturn(new ShardingTable(Arrays.asList("ds_0", "ds_1"), "t_order"));
        Collection<RouteUnit> routeUnits = new LinkedList<>();
        routeUnits.add(new RouteUnit(new RouteMapper("ds_0", "ds_0"), Collections.singletonList(new RouteMapper("t_order", "t_order_0"))));
        when(routeContext.getRouteUnits()).thenReturn(routeUnits);
        when(queryContext.getSqlStatementContext()).thenReturn(new CommonSQLStatementContext(sqlStatement));
        assertThrows(ShardingDDLRouteException.class, () -> new ShardingDropIndexRouteContextChecker().check(shardingRule, queryContext, database, mock(ConfigurationProperties.class), routeContext));
    }
}
