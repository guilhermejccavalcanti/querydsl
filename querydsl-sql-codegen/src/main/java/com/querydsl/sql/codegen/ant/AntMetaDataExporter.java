/*
 * Copyright 2011, Mysema Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.codegen.ant;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.mysema.codegen.model.SimpleType;
import com.querydsl.codegen.BeanSerializer;
import com.querydsl.sql.codegen.DefaultNamingStrategy;
import com.querydsl.sql.codegen.MetaDataExporter;
import com.querydsl.sql.codegen.NamingStrategy;

/**
 * AntMetaDataExporter exports JDBC metadata to Querydsl query types
 *
 * @author tiwe
 *
 */
public class AntMetaDataExporter extends Task {

    /**
     * JDBC driver class name
     */
    private String jdbcDriver;

    /**
     * JDBC connection url
     */
    private String jdbcUrl;

    /**
     * JDBC connection username
     */
    private String jdbcUser;

    /**
     * JDBC connection password
     */
    private String jdbcPassword;

    /**
     * name prefix for querydsl-types (default: "Q")
     */
    private String namePrefix;

    /**
     * name suffix for querydsl-types (default: "")
     */
    private String nameSuffix;

    /**
     * name prefix for bean types (default: "")
     */
    private String beanPrefix;

    /**
     * name suffix for bean types (default: "")
     */
    private String beanSuffix;

    /**
     * package name for sources
     */
    private String packageName;

    /**
     * package name for bean sources (default: packageName)
     */
    private String beanPackageName;

    /**
     * schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a schema;
     *        {@code null} means that the schema name should not be used to narrow
     *        the search (default: null)
     */
    private String schemaPattern;

    /**
     * tableNamePattern a table name pattern; must match the
     *        table name as it is stored in the database (default: null)
     */
    private String tableNamePattern;

    /**
     * target source folder to create the sources into (e.g. target/generated-sources/java)
     */
    private String targetFolder;

    /**
     * namingstrategy class to override (default: DefaultNamingStrategy)
     */
    private String namingStrategyClass;

    /**
     *
     */
    private String beanSerializerClass;

    /**
     *
     */
    private String serializerClass;

    /**
     * serialize beans as well
     */
    private boolean exportBeans;

    /**
     *
     */
    private String[] beanInterfaces;

    /**
     *
     */
    private boolean beanAddToString;

    /**
     *
     */
    private boolean beanAddFullConstructor;

    /**
     *
     */
    private boolean beanPrintSupertype;

    /**
     * wrap key properties into inner classes (default: false)
     */
    private boolean innerClassesForKeys;

    /**
     * export validation annotations (default: false)
     */
    private boolean validationAnnotations;

    /**
     * export column annotations (default: false)
     */
    private boolean columnAnnotations;

    /**
     *
     */
    private String[] customTypes;

    /**
     *
     */
    private boolean createScalaSources;

    /**
     *
     */
    private boolean schemaToPackage;

    /**
     *
     */
    private boolean lowerCase;

    /**
     *
     */
    private boolean exportTables;

    /**
     *
     */
    private boolean exportViews;

    /**
     *
     */
    private boolean exportAll;

    /**
     *
     */
    private boolean exportPrimaryKeys;

    /**
     *
     */
    private boolean exportForeignKeys;

    /**
     * override default column order (default: alphabetical)
     */
    private String columnComparatorClass;

    /**
     *
     */
    private boolean spatial;

    /**
     * Comma-separated list of table types to export (allowable values will
     * depend on JDBC driver). Allows for arbitrary set of types to be exported,
     * e.g.: "TABLE, MATERIALIZED VIEW". The exportTables and exportViews
     * parameters will be ignored if this parameter is set. (default: none)
     */
    private String tableTypesToExport;

    /**
     * java import added to generated query classes:
     * com.bar for package (without .* notation)
     * com.bar.Foo for class
     */
    private String[] imports;

    // Ant only
    private String sourceEncoding;


    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute() {
        if (targetFolder == null) {
            throw new BuildException("targetFolder is a mandatory property");
        }

        Connection dbConn = null;
        File targetPackagePath = new File(targetFolder);

        try {
            Class.forName(jdbcDriver).newInstance();

            dbConn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);

            NamingStrategy namingStrategy = new DefaultNamingStrategy();
            MetaDataExporter exporter = new MetaDataExporter();
            if (namePrefix != null) {
                exporter.setNamePrefix(namePrefix);
            }
            if (nameSuffix != null) {
                exporter.setNameSuffix(nameSuffix);
            }
            if (beanPrefix != null) {
                exporter.setBeanPrefix(beanPrefix);
            }
            if (beanSuffix != null) {
                exporter.setBeanSuffix(beanSuffix);
            }
            exporter.setPackageName(packageName);
            exporter.setBeanPackageName(beanPackageName);
            exporter.setTargetFolder(targetPackagePath);
            exporter.setNamingStrategy(namingStrategy);
            exporter.setInnerClassesForKeys(innerClassesForKeys);
            exporter.setSchemaPattern(schemaPattern);
            exporter.setTableNamePattern(tableNamePattern);
            exporter.setColumnAnnotations(columnAnnotations);
            exporter.setValidationAnnotations(validationAnnotations);
            exporter.setSchemaToPackage(schemaToPackage);
            exporter.setLowerCase(lowerCase);
            exporter.setExportTables(exportTables);
            exporter.setExportViews(exportViews);
            exporter.setExportAll(exportAll);
            exporter.setTableTypesToExport(tableTypesToExport);
            exporter.setExportPrimaryKeys(exportPrimaryKeys);
            exporter.setExportForeignKeys(exportForeignKeys);
            exporter.setSpatial(spatial);

            if (imports != null && imports.length > 0) {
                exporter.setImports(imports);
            }

            if (exportBeans) {
                BeanSerializer serializer = new BeanSerializer();
                if (beanInterfaces != null) {
                    for (String iface : beanInterfaces) {
                        int sepIndex = iface.lastIndexOf('.');
                        if (sepIndex < 0) {
                            serializer.addInterface(new SimpleType(iface));
                        } else {
                            String packageName = iface.substring(0, sepIndex);
                            String simpleName = iface.substring(sepIndex + 1);
                            serializer.addInterface(new SimpleType(iface, packageName, simpleName));
                        }
                    }
                }
                serializer.setAddFullConstructor(beanAddFullConstructor);
                serializer.setAddToString(beanAddToString);
                serializer.setPrintSupertype(beanPrintSupertype);
                exporter.setBeanSerializer(serializer);
            }
            if (sourceEncoding != null) {
                exporter.setSourceEncoding(sourceEncoding);
            }
            if (columnComparatorClass != null) {
                exporter.setColumnComparatorClass((Class) Class.forName(this.columnComparatorClass).asSubclass(Comparator.class));
            }

            exporter.export(dbConn.getMetaData());

        } catch (RuntimeException e) {
            throw new BuildException(e);
        } catch (InstantiationException e) {
            throw new BuildException(e);
        } catch (IllegalAccessException e) {
            throw new BuildException(e);
        } catch (ClassNotFoundException e) {
            throw new BuildException(e);
        } catch (SQLException e) {
            throw new BuildException(e);
        } finally {
            if (dbConn != null) {
                try {
                    dbConn.close();
                } catch (SQLException e2) {
                    throw new BuildException(e2);
                }
            }
        }
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public String getBeanPrefix() {
        return beanPrefix;
    }

    public void setBeanPrefix(String beanPrefix) {
        this.beanPrefix = beanPrefix;
    }

    public String getBeanSuffix() {
        return beanSuffix;
    }

    public void setBeanSuffix(String beanSuffix) {
        this.beanSuffix = beanSuffix;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBeanPackageName() {
        return beanPackageName;
    }

    public void setBeanPackageName(String beanPackageName) {
        this.beanPackageName = beanPackageName;
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }

    public void setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
    }

    public String getTableNamePattern() {
        return tableNamePattern;
    }

    public void setTableNamePattern(String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    public String getNamingStrategyClass() {
        return namingStrategyClass;
    }

    public void setNamingStrategyClass(String namingStrategyClass) {
        this.namingStrategyClass = namingStrategyClass;
    }

    public String getBeanSerializerClass() {
        return beanSerializerClass;
    }

    public void setBeanSerializerClass(String beanSerializerClass) {
        this.beanSerializerClass = beanSerializerClass;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(String serializerClass) {
        this.serializerClass = serializerClass;
    }

    public boolean isExportBeans() {
        return exportBeans;
    }

    public void setExportBeans(boolean exportBeans) {
        this.exportBeans = exportBeans;
    }

    public String[] getBeanInterfaces() {
        return beanInterfaces;
    }

    public void setBeanInterfaces(String[] beanInterfaces) {
        this.beanInterfaces = beanInterfaces;
    }

    public boolean isBeanAddToString() {
        return beanAddToString;
    }

    public void setBeanAddToString(boolean beanAddToString) {
        this.beanAddToString = beanAddToString;
    }

    public boolean isBeanAddFullConstructor() {
        return beanAddFullConstructor;
    }

    public void setBeanAddFullConstructor(boolean beanAddFullConstructor) {
        this.beanAddFullConstructor = beanAddFullConstructor;
    }

    public boolean isBeanPrintSupertype() {
        return beanPrintSupertype;
    }

    public void setBeanPrintSupertype(boolean beanPrintSupertype) {
        this.beanPrintSupertype = beanPrintSupertype;
    }

    public boolean isInnerClassesForKeys() {
        return innerClassesForKeys;
    }

    public void setInnerClassesForKeys(boolean innerClassesForKeys) {
        this.innerClassesForKeys = innerClassesForKeys;
    }

    public boolean isValidationAnnotations() {
        return validationAnnotations;
    }

    public void setValidationAnnotations(boolean validationAnnotations) {
        this.validationAnnotations = validationAnnotations;
    }

    public boolean isColumnAnnotations() {
        return columnAnnotations;
    }

    public void setColumnAnnotations(boolean columnAnnotations) {
        this.columnAnnotations = columnAnnotations;
    }

    public String[] getCustomTypes() {
        return customTypes;
    }

    public void setCustomTypes(String[] customTypes) {
        this.customTypes = customTypes;
    }

    public boolean isCreateScalaSources() {
        return createScalaSources;
    }

    public void setCreateScalaSources(boolean createScalaSources) {
        this.createScalaSources = createScalaSources;
    }

    public boolean isSchemaToPackage() {
        return schemaToPackage;
    }

    public void setSchemaToPackage(boolean schemaToPackage) {
        this.schemaToPackage = schemaToPackage;
    }

    public boolean isLowerCase() {
        return lowerCase;
    }

    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public boolean isExportTables() {
        return exportTables;
    }

    public void setExportTables(boolean exportTables) {
        this.exportTables = exportTables;
    }

    public boolean isExportViews() {
        return exportViews;
    }

    public void setExportViews(boolean exportViews) {
        this.exportViews = exportViews;
    }

    public boolean isExportAll() {
        return exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

    public boolean isExportPrimaryKeys() {
        return exportPrimaryKeys;
    }

    public void setExportPrimaryKeys(boolean exportPrimaryKeys) {
        this.exportPrimaryKeys = exportPrimaryKeys;
    }

    public boolean isExportForeignKeys() {
        return exportForeignKeys;
    }

    public void setExportForeignKeys(boolean exportForeignKeys) {
        this.exportForeignKeys = exportForeignKeys;
    }

    public String getColumnComparatorClass() {
        return columnComparatorClass;
    }

    public void setColumnComparatorClass(String columnComparatorClass) {
        this.columnComparatorClass = columnComparatorClass;
    }

    public boolean isSpatial() {
        return spatial;
    }

    public void setSpatial(boolean spatial) {
        this.spatial = spatial;
    }

    public String getTableTypesToExport() {
        return tableTypesToExport;
    }

    public void setTableTypesToExport(String tableTypesToExport) {
        this.tableTypesToExport = tableTypesToExport;
    }

    public String[] getImports() {
        return imports;
    }

    public void setImports(String[] imports) {
        this.imports = imports;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }
}