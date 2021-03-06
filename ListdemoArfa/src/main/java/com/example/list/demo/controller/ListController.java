package com.example.list.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.list.demo.entity.RiskTable;
import com.example.list.demo.service.ListService;
import com.example.list.demo.utils.Result;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/mylist")
public class ListController {

    @Autowired
    private ListService listService;

    //传递父节点code
    @PostMapping(value = "/node/getNode")
    public Result getByOneParentNode(@RequestParam("parent_code") String parentcode){
        Map<String,Object> maps = new HashMap<>();
        List<RiskTable> byOneParentNode = listService.getByOneParentNode(parentcode);
        RiskTable getParentByNode = listService.getParentByNode(parentcode);
        maps.put("parent",getParentByNode);
        maps.put("children",byOneParentNode);
        return Result.success(maps);
    }

    /*删除节点*/
    @RequestMapping(value = "/node/deleteNode", method = {RequestMethod.GET,RequestMethod.POST})
    public Result deleteNode(@RequestParam("code") String code){
        String node = listService.selectNode(code);
        String[] codeList = node.split(",");
        for(int i=1;i<codeList.length;i++){
            listService.deleteNode(codeList[i]);
        }
        return Result.success("success",200);
    }

    @PostMapping("/node/insertRisk")
    public Result insertRisk(@RequestBody JSONObject jsonObject){
        RiskTable riskTable = new RiskTable();
        String code = jsonObject.getString("code");
        String riskName = jsonObject.getString("riskName");
        String parentCode = jsonObject.getString("parentCode");
        System.out.println("up"+code);
        riskTable.setCode(code);
        riskTable.setRiskName(riskName);
        riskTable.setParentCode(parentCode);

        listService.insertRisk(riskTable);
        return Result.success("插入成功!");
    }


    /**
     * 向下添加
     */
    @PostMapping("/node/down")
    public Result insertRiskDown(@RequestBody JSONObject jsonObject){
        RiskTable riskTable = new RiskTable();
        String  code = jsonObject.getString("code");
        String riskName = jsonObject.getString("riskName");
        String parentCode = jsonObject.getString("parentCode");
        riskTable.setCode(code);
        riskTable.setRiskName(riskName);
        riskTable.setParentCode(parentCode);
        listService.insertRiskDown(riskTable);
        return   Result.success("插入 down 成功!");
    }

    // 向上插入
    @PostMapping("/node/up")
    public Result insertRiskUp(@RequestBody JSONObject jsonObject){
        RiskTable riskTable = new RiskTable();
        String  code = jsonObject.getString("code");
        String riskName = jsonObject.getString("riskName");
        String parentCode = jsonObject.getString("parentCode");
        riskTable.setCode(code);
        riskTable.setRiskName(riskName);
        riskTable.setParentCode(parentCode);
        listService.insertRiskUp(riskTable);
        return   Result.success("插入 up 成功!");
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam("code") String code){
        listService.getByOneParentNode(code);
        System.out.println(code);
        return Result.success("ok");
    }


    @PostMapping("/insert")
    public Result insert(@RequestBody JSONObject jsonparam ) throws Exception {
        RiskTable riskTable = new RiskTable();

        System.out.println(jsonparam);
        JSONArray jsonArray = jsonparam.getJSONArray("xzq");
        List<RiskTable> list = new ArrayList<>();
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            String cd = object.getString("cd");
            String name = object.getString("nm");
            if (cd.length() == 2) {
                riskTable.setParentCode("0");
            } else {
                riskTable.setParentCode(cd.substring(0, cd.length() - 2));
            }

            riskTable.setRiskName(name);
            riskTable.setCode(cd);

            list.add(riskTable);
        }
        listService.insert(list);
        System.out.println(jsonArray);
        return Result.success("success");
    }
}
