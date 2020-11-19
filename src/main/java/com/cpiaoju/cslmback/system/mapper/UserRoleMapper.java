package com.cpiaoju.cslmback.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpiaoju.cslmback.system.entity.UserRole;
import org.apache.ibatis.annotations.Param;

/**
 * @author ziyou
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户Id删除该用户的角色关系
     *
     * @param userId
     * @return
     */
    Boolean deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色Id删除该角色的用户关系
     *
     * @param roleId
     * @return
     */
    Boolean deleteByRoleId(@Param("roleId") Long roleId);
}