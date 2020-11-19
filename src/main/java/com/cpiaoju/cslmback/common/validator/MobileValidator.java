package com.cpiaoju.cslmback.common.validator;


import cn.hutool.core.util.StrUtil;
import com.cpiaoju.cslmback.common.annotation.IsMobile;
import com.cpiaoju.cslmback.common.entity.Regexp;
import com.cpiaoju.cslmback.common.util.CslmUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验是否为合法的手机号码
 *
 * @author ziyou
 */
public class MobileValidator implements ConstraintValidator<IsMobile, String> {

    @Override
    public void initialize(IsMobile isMobile) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if (StrUtil.isBlank(s)) {
                return true;
            } else {
                String regex = Regexp.MOBILE_REG;
                return CslmUtil.match(regex, s);
            }
        } catch (Exception e) {
            return false;
        }
    }
}
