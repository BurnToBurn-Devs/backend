package com.burntoburn.easyshift.exception.template;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

public class TemplateException extends BusinessException {
    public TemplateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static TemplateException scheduleTemplateNotFound() {
        return new TemplateException(TemplateErrorCode.SCHEDULE_TEMPLATE_NOT_FOUND);
    }
    public static TemplateException shiftTemplateNotFound() {
        return new TemplateException(TemplateErrorCode.SHIFT_TEMPLATE_NOT_FOUND);
    }

    public static TemplateException duplicateScheduleTemplate() {
        return new TemplateException(TemplateErrorCode.DUPLICATE_SCHEDULE_TEMPLATE);
    }
}
