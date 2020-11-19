package com.cpiaoju.cslmback.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cpiaoju.cslmback.common.entity.QueryRequest;
import com.cpiaoju.cslmback.system.entity.Dept;

import java.util.List;
import java.util.Map;

/**
 * @author ziyou
 */
public interface DeptService extends IService<Dept> {

    Map<String, Object> findDepts(QueryRequest request, Dept dept);

    List<Dept> findDepts(Dept dept, QueryRequest request);

    void createDept(Dept dept);

    void updateDept(Dept dept);

    void deleteDepts(String[] deptIds);
}
