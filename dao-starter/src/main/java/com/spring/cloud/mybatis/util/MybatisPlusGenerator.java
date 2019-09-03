package com.spring.cloud.mybatis.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class MybatisPlusGenerator {

    /*** 表前缀去除*/
   // private static String[] tablePrefix = new String[]{"hari_"};
    /*** 表名*/
    public static String[] table = new String[]{"gateway_routes"};
    /*** 模块名*/
    public static String modelName = "gateway";
    //作者
    public static String author = "shennanb";
    public static String sqlUrl = "jdbc:mysql://118.25.213.82:3306/my_dataBase";
    public static String sqlUserName = "root";
    public static String sqlPassword = "123456";

    public static void main(String[] args) {
        execute( true);
        execute( false);
    }

    public static void execute(boolean api) {
        AutoGenerator mpg = new AutoGenerator();
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //strategy.setTablePrefix(tablePrefix);
        strategy.setInclude(table);
        // strategy.setExclude(new String[]{"test"});   // 排除生成的表
        mpg.setStrategy(strategy);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUrl(sqlUrl);
        dsc.setUsername(sqlUserName);
        dsc.setPassword(sqlPassword);
        mpg.setDataSource(dsc);
        mpg.setGlobalConfig(gc(api));
        mpg.setTemplate(templateConfig(api));
        mpg.setPackageInfo(pc(api));
        mpg.execute();
    }

    private static GlobalConfig gc(boolean api) {
        String path = Class.class.getClass().getResource("/").getPath();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(path.substring(0, path.indexOf("/target")) + "/src/main/java");
        gc.setFileOverride(false);
        gc.setOpen(false);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(true);// XML columList
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        if (api) {
            gc.setControllerName("%sApi");
        } else {
            gc.setControllerName("%sController");
        }

        gc.setAuthor(author);
        return gc;
    }

    private static PackageConfig pc(boolean api) {
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.spring.cloud");
        pc.setEntity("entity.mysql." + modelName);
        pc.setService("service." + modelName);
        pc.setServiceImpl("service.impl." + modelName);
        pc.setMapper("dao.mapper." + modelName);
        pc.setXml("dao.mapper.xml." + modelName);
        if (api) {
            pc.setController("api."+modelName);
        } else {
            pc.setController("controller." + modelName);
        }
        return pc;
    }

    private static TemplateConfig templateConfig(boolean api) {
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setEntity("templates/entity.java");
        if (api) {
            templateConfig.setController("templates/controller2.java");
        } else {
            templateConfig.setController("templates/controller3.java");
        }
        return templateConfig;
    }

}