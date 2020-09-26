package org.zenith.legion.routine.dto;

import org.springframework.beans.BeanUtils;
import org.zenith.legion.common.utils.DateUtils;
import org.zenith.legion.common.utils.StringUtils;
import org.zenith.legion.routine.entity.ScheduleEvent;

import java.io.Serializable;

public class CalendarEvent implements Serializable {

    private Long eventId;
    private Integer groupId;
    private String title;
    private String content;
    private String eventType;
    private String start;
    private String end;
    private String color;
    private String textColor;
    private String backgroundColor;
    private boolean allDay;
    private boolean editable;

    public CalendarEvent() {}

    public CalendarEvent(ScheduleEvent event) {
        if (event != null) {
            eventId = event.getCalendarEventId();
            groupId = event.getGroupId();
            title = event.getTitle();
            content = event.getContent();
            eventType = event.getEventType();
            start = DateUtils.getStandardDate(event.getStart());
            end = DateUtils.getStandardDate(event.getEnd());
            color = event.getColor();
            textColor = event.getTextColor();
            backgroundColor = event.getBackgroundColor();
            allDay = StringUtils.parseBoolean(event.getAllDay());
            editable = StringUtils.parseBoolean(event.getEditable());
        }
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
