package org.legion.aegis.timesheet.dto;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.timesheet.dao.TimesheetDAO;
import org.legion.aegis.timesheet.entity.Timesheet;

import java.util.Date;

public class EventDto extends BaseDto {

    @MemberOf(value = {AppConsts.TIMESHEET_EVENT_COMMON, AppConsts.TIMESHEET_EVENT_TASK,
            AppConsts.TIMESHEET_EVENT_PUBLIC}, message = "请选择正确的事件类型", profile = {"create"})
    @ValidateWithMethod(methodName = "canPostPublic", message = "请选择正确的事件类型", profile = {"create"})
    private String type;
    @NotBlank(message = "标题不能为空", profile = {"create", "update"})
    @Length(max = 100, message = "长度不得超过10个字符", profile = {"create", "update"})
    private String title;
    @ValidateWithMethod(methodName = "validateColor", message = "请输入正确的16进制颜色代码（#000000 ~ #FFFFFF）", profile = {"create", "update"})
    private String color;
    @ValidateWithMethod(methodName = "validateColor", message = "请输入正确的16进制颜色代码（#000000 ~ #FFFFFF）", profile = {"create", "update"})
    private String textColor;
    @Length(max = 500, message = "内容不得超过500字符", profile = {"create", "update"})
    private String content;
    private String isAllDay;
    @ValidateWithMethod(methodName = "validateTime", message = "请输入正确的时间格式", profile = {"create", "update"})
    private String startAt;
    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validateTime", message = "请输入正确的时间格式", profile = {"create", "update"}),
            @ValidateWithMethod(methodName = "validateEffective", message = "开始时间必须在结束时间之前", profile = {"create", "update"})
    })
    private String endAt;

    @ValidateWithMethod(methodName = "validateDate", message = "请选择正确的时间", profile = {"create"})
    private String chosenDate;

    @ValidateWithMethod(methodName = "canAccess", message = "请求被拒绝", profile = {"update"})
    private String timesheetId;

    private boolean validateColor(String color) {
        if (StringUtils.isNotBlank(color)) {
            return ValidationUtils.validateColor(color);
        }
        return true;
    }

    private boolean validateTime(String time) {
        if (!StringUtils.parseBoolean(isAllDay)) {
            Date date = DateUtils.parseDatetime(time, "HH:mm");
            return date != null;
        }
        return true;
    }

    private boolean validateEffective(String time) {
        if (!StringUtils.parseBoolean(isAllDay)) {
            Date date1 = DateUtils.parseDatetime(startAt, "HH:mm");
            Date date2 = DateUtils.parseDatetime(endAt, "HH:mm");
            return date1 != null && date2 != null && date1.before(date2);
        }
        return true;
    }

    private boolean canPostPublic(String type) {
        if (AppConsts.TIMESHEET_EVENT_PUBLIC.equals(type)) {
            return AppConsts.ROLE_SYSTEM_ADMIN.equals(AppContext.getFromWebThread().getRoleId());
        }
        return true;
    }

    private boolean validateDate(String chosenDate) {
        if (chosenDate.contains(":")) {
            String[] dates = chosenDate.split(":");
            if (dates.length == 2) {
                Date date1 = DateUtils.parseDatetime(dates[0]);
                Date date2 = DateUtils.parseDatetime(dates[1]);
                return date1 != null && date2 != null && date1.before(date2);
            }
        } else {
            return DateUtils.parseDatetime(chosenDate) != null;
        }
        return false;
    }

    private boolean canAccess(String timesheetId) {
        if (StringUtils.isNotBlank(timesheetId)) {
            TimesheetDAO dao = SpringUtils.getBean(TimesheetDAO.class);
            Timesheet timesheet = dao.retrieveTimesheetById(StringUtils.parseIfIsLong(timesheetId));
            AppContext context = AppContext.getFromWebThread();
            return timesheet != null && timesheet.getUserAcctId().equals(context.getUserId());
        }
        return false;
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

    public String getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(String timesheetId) {
        this.timesheetId = timesheetId;
    }
}
