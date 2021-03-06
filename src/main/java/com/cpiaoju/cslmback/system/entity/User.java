package com.cpiaoju.cslmback.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cpiaoju.cslmback.common.annotation.IsMobile;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = -4852732617765810959L;
    /**
     * 账户状态
     */
    public static final String STATUS_VALID = "1";

    public static final String STATUS_LOCK = "0";

    public static final String DEFAULT_AVATAR = "default.jpg";

    /**
     * 性别
     */
    public static final String SEX_MALE = "0";

    public static final String SEX_FEMALE = "1";

    public static final String SEX_UNKNOW = "2";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "1234qwer";

    @TableId(value = "USER_ID", type = IdType.AUTO)
    private Long userId;

    @Size(min = 4, max = 10, message = "{range}")
    private String username;

    private String password;

    private Long deptId;

    private transient String deptName;

    @Size(max = 50, message = "{noMoreThan}")
    @Email(message = "{email}")
    private String email;

    @IsMobile(message = "{mobile}")
    private String mobile;

    @NotBlank(message = "{required}")
    private String status;

    private Date createTime;

    private Date modifyTime;

    private Date lastLoginTime;

    @NotBlank(message = "{required}")
    private String ssex;

    @Size(max = 100, message = "{noMoreThan}")
    private String description;

    private String avatar;

    @NotBlank(message = "{required}")
    private transient String roleId;
    private transient String roleName;

    /**
     * 排序字段
     */
    private transient String sortField;

    /**
     * 排序规则 ascend 升序 descend 降序
     */
    private transient String sortOrder;

    private transient String createTimeFrom;
    private transient String createTimeTo;

    /**
     * shiro-redis v3.1.0 必须要有 getAuthCacheKey()或者 getId()方法
     * # Principal id field name. The field which you can get unique id to identify this principal.
     * # For example, if you use UserInfo as Principal class, the id field maybe userId, userName, email, etc.
     * # Remember to add getter to this id field. For example, getUserId(), getUserName(), getEmail(), etc.
     * # Default value is authCacheKey or id, that means your principal object has a method called "getAuthCacheKey()" or "getId()"
     *
     * @return userId as Principal id field name
     */
    public Long getAuthCacheKey() {
        return userId;
    }


}
