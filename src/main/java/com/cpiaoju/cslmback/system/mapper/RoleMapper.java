package com.cpiaoju.cslmback.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpiaoju.cslmback.system.entity.Role;

import java.util.List;

/**
 * @author ziyou
 */
public interface RoleMapper extends BaseMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
}