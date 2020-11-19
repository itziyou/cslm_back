package com.cpiaoju.cslmback.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ziyou
 */
@Data
@TableName("t_dept")
public class Dept implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;

    @TableId(value = "DEPT_ID", type = IdType.AUTO)
    private Long deptId;

    private Long parentId;

    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    private String deptName;

    private Double orderNum;

    private Date createTime;

    private Date modifyTime;

    private transient String createTimeFrom;

    private transient String createTimeTo;

}