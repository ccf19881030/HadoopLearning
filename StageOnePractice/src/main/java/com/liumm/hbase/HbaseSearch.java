package com.liumm.hbase;

import com.liumm.base.HbaseBase;
import com.liumm.entity.City;
import com.liumm.hbase.endpiont.HbaseMultipleRegionClient;
import com.liumm.hbase.endpiont.HbaseSingleRegionClient;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: 根据地区编号查询下级地区信息
 *
 * @Author liumm
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 * @Date 2018-06-30 16:53
 */
public class HbaseSearch {

    /***
     * @method getCityList
     * @description 根据父ID查询城市信息
     * @date: 18/6/30 17:10
     * @author: liumm
     * @param parentId
     * @return java.util.List<com.liumm.entity.City>
     */
    public static List<City> getCityList(String parentId) throws Exception {
        Connection connection = HbaseBase.connectToHbase(HbaseBase.hbaseMasterIPS, HbaseBase.hbaseMasterPort, HbaseBase.hbaseZnode);
        Table table = connection.getTable(TableName.valueOf("city"));
        //scan进行扫描
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(parentId + "|"));
        ResultScanner scanner = table.getScanner(scan);
        List<City> list = new ArrayList<>();
        for (Result result : scanner) {
            byte[] area = result.getValue(Bytes.toBytes("cfn"), Bytes.toBytes("areaId"));
            byte[] parent = result.getValue(Bytes.toBytes("cfn"), Bytes.toBytes("parentId"));
            byte[] name = result.getValue(Bytes.toBytes("cfn"), Bytes.toBytes("areaName"));
            list.add(new City(Bytes.toString(area), Bytes.toString(parent), Bytes.toString(name)));
        }
        return list;
    }

    /**
     * @param parentId
     * @return java.util.Map<java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Long>
     * @method getCountByParent
     * @description 根据多个ParentID查询它当前的子区域的ID个数
     * @date: 18/6/30 17:17
     * @author: liumm
     */
    public static Map<String, Long> getCountByParent(String... parentId) throws Exception {
        Map<String, Long> map = new HashMap<>();
        Connection connection = HbaseBase.connectToHbase(HbaseBase.hbaseMasterIPS, HbaseBase.hbaseMasterPort, HbaseBase.hbaseZnode);
        Table table = connection.getTable(TableName.valueOf("city"));
        //保证同一地区的子地区在一个分区里面
        HbaseSingleRegionClient client = new HbaseSingleRegionClient();
        client.callCoprocessor(table, parentId);
        return map;
    }

}
