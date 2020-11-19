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
@TableName("t_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 7187628714679791771L;

    public static final String TYPE_MENU = "0";

    public static final String TYPE_BUTTON = "1";

    @TableId(value = "MENU_ID", type = IdType.AUTO)
    private Long menuId;

    private Long parentId;

    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    private String menuName;

    @Size(max = 50, message = "{noMoreThan}")
    private String path;

    @Size(max = 100, message = "{noMoreThan}")
    private String component;

    @Size(max = 50, message = "{noMoreThan}")
    private String perms;

    private String icon;

    @NotBlank(message = "{required}")
    private String type;

    private Double orderNum;

    private Date createTime;

    private Date modifyTime;

    private transient String createTimeFrom;
    private transient String createTimeTo;

}