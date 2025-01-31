package com.alibaba.polardbx.qatest.ddl.auto.gsi;

import com.alibaba.polardbx.qatest.ddl.auto.autoNewPartition.BaseAutoPartitionNewPartition;
import com.alibaba.polardbx.qatest.util.JdbcUtil;
import org.junit.Assert;
import org.junit.Test;

public class AlterTableWithGsiTest extends BaseAutoPartitionNewPartition {

    private String tableName = "wumu";
    private String indexTableName = "g_i_wumu";

    private static final String createOption = " if not exists ";

    @Test
    public void testAlterTableConvertCharset() {
        final String primaryTable = tableName + "_1";
        final String indexTable = indexTableName + "_1";

        dropTableIfExists(primaryTable);
        String sql = String.format(HINT_CREATE_GSI
                + "create table "
                + createOption
                + "%s(a int primary key auto_increment,b varchar(30), c varchar(30), d varchar(30), e varchar(30)"
                + ", unique index u_i_c(c)"
                + ", global index %s(a, b, d) covering(c) partition by hash(a)"
                + ") partition by hash(a)",
            primaryTable,
            indexTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);

        sql = String.format("alter table %s convert to character set latin1", primaryTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);
        Assert.assertTrue(showCreateTable(tddlConnection, primaryTable).contains("latin1"));
        Assert.assertTrue(showCreateTable(tddlConnection, indexTable).contains("latin1"));

        sql = String.format("alter table %s convert to character set utf8 collate utf8_bin", primaryTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);
        Assert.assertTrue(showCreateTable(tddlConnection, primaryTable).contains("utf8"));
        Assert.assertTrue(showCreateTable(tddlConnection, indexTable).contains("utf8_bin"));
        Assert.assertTrue(showCreateTable(tddlConnection, primaryTable).contains("utf8"));
        Assert.assertTrue(showCreateTable(tddlConnection, indexTable).contains("utf8_bin"));

        sql = String.format("alter table %s convert to character set utf8 collate utf8_general_cixx", primaryTable);
        JdbcUtil.executeUpdateFailed(tddlConnection, sql, "unknown collate name 'utf8_general_cixx'");

        sql = String.format("alter table %s convert to character set utf8 collate LATIN1_BIN", primaryTable);
        JdbcUtil.executeUpdateFailed(tddlConnection, sql, "collate name 'latin1_bin' not support for 'utf8'");

        sql = String.format("alter table %s convert to character set utf2", primaryTable);
        JdbcUtil.executeUpdateFailed(tddlConnection, sql, "unknown charset name 'utf2'");

        dropTableIfExists(primaryTable);
    }

    @Test
    public void testAlterTableConvertCharset2() {
        final String primaryTable = tableName + "_2";
        final String indexTable = indexTableName + "_2";

        dropTableIfExists(primaryTable);
        String sql = String.format(HINT_CREATE_GSI
                + "create table "
                + createOption
                + "%s(a int primary key auto_increment,b varchar(30), c varchar(30), d varchar(30), e varchar(30)"
                + ", unique index u_i_c(c)"
                + ") partition by hash(a)",
            primaryTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);

        sql = String.format("alter table %s convert to character set latin1", primaryTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);
        Assert.assertTrue(showCreateTable(tddlConnection, primaryTable).contains("latin1"));

        sql = String.format("alter table %s add global index %s(b, d) covering(c) partition by hash(b)",
            primaryTable, indexTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);

        sql = String.format("alter table %s convert to character set utf8 collate utf8_bin", primaryTable);
        JdbcUtil.executeUpdateFailed(tddlConnection, sql, "not supported yet");

        dropTableIfExists(primaryTable);
    }

    @Test
    public void testAlterTableConvertCharset3() {
        final String primaryTable = tableName + "_3";

        dropTableIfExists(primaryTable);
        String sql = String.format(HINT_CREATE_GSI
                + "create table "
                + createOption
                + "%s(a int primary key auto_increment,b varchar(30), c varchar(30), d varchar(30), e varchar(30)"
                + ", unique index u_i_c(c)"
                + ") partition by hash(b)",
            primaryTable);
        JdbcUtil.executeUpdateSuccess(tddlConnection, sql);

        sql = String.format("alter table %s convert to character set utf8 collate utf8_bin", primaryTable);
        JdbcUtil.executeUpdateFailed(tddlConnection, sql, "not supported yet");

        dropTableIfExists(primaryTable);
    }
}
