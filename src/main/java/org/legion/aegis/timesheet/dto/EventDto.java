package org.legion.aegis.timesheet.dto;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;
import java.util.Date;

public class EventDto extends BaseDto {

    @MemberOf(value = {AppConsts.TIMESHEET_EVENT_COMMON, AppConsts.TIMESHEET_EVENT_TASK,
            AppConsts.TIMESHEET_EVENT_PUBLIC}, message = "请选择正确的事件类型")
    @ValidateWithMethod(methodName = "canPostPublic", message = "请选择正确的事件类型")
    private String type;
    @NotBlank(message = "标题不能为空")
    @Length(max = 100, message = "长度不得超过10个字符")
    private String title;
    @ValidateWithMethod(methodName = "validateColor", message = "请输入正确的16进制颜色代码（#000000 ~ #FFFFFF）")
    private String color;
    @ValidateWithMethod(methodName = "validateColor", message = "请输入正确的16进制颜色代码（#000000 ~ #FFFFFF）")
    private String textColor;
    @Length(max = 500, message = "内容不得超过500字符")
    private String content;
    private String isAllDay;
    @ValidateWithMethod(methodName = "validateTime", message = "请输入正确的时间格式")
    private String startAt;
    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validateTime", message = "请输入正确的时间格式"),
            @ValidateWithMethod(methodName = "validateEffective", message = "开始时间必须在结束时间之前")
    })
    private String endAt;

    private String chosenDate;

    private boolean validateColor(String color) {
        if (StringUtils.isNotBlank(color)) {
            return ValidationUtils.validateColor(color);
        }
        return true;
    }

    private boolean validateTime(String time) {
        if (!StringUtils.parseBoolean(isAllDay)) {
            Date date = DateUtils.parseDatetime(chosenDate + " " + time, "yyyy-MM-dd HH:mm");
            return date != null;
        }
        return true;
    }

    private boolean validateEffective(String time) {
        if (!StringUtils.parseBoolean(isAllDay)) {
            Date start = DateUtils.parseDatetime(chosenDate + " " + startAt, "yyyy-MM-dd HH:mm");
            Date end = DateUtils.parseDatetime(chosenDate + " " + endAt, "yyyy-MM-dd HH:mm");
            return start != null && end != null && start.before(end);
        }
        return true;
    }

    private boolean canPostPublic(String type) {
        if (AppConsts.TIMESHEET_EVENT_PUBLIC.equals(type)) {
            return AppConsts.ROLE_SYSTEM_ADMIN.equals(AppContext.getFromWebThread().getRoleId());
        }
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(String isAllDay) {
        this.isAllDay = isAllDay;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getChosenDate() {
        return chosenDate;
    }

    public void setChosenDate(String chosenDate) {
        this.chosenDate = chosenDate;
    }
}
