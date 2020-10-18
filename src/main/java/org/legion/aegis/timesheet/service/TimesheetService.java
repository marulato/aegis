package org.legion.aegis.timesheet.service;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.timesheet.dao.TimesheetDAO;
import org.legion.aegis.timesheet.dto.EventDto;
import org.legion.aegis.timesheet.entity.CommonEvent;
import org.legion.aegis.timesheet.entity.Timesheet;
import org.legion.aegis.timesheet.vo.EventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimesheetService {

    private final TimesheetDAO timesheetDAO = SpringUtils.getBean(TimesheetDAO.class);
    private static final Logger log = LoggerFactory.getLogger(TimesheetService.class);

    @Transactional
    public void saveCommonEvent(EventDto eventDto) throws Exception {
        if (eventDto != null && StringUtils.isBlank(eventDto.getTimesheetId())) {
            CommonEvent event = BeanUtils.mapFromDto(eventDto, CommonEvent.class);
            event.setIsEditable(AppConsts.YES);
            if (StringUtils.isBlank(event.getColor())) {
                event.setColor("#3788D8");
            }
            if (StringUtils.isBlank(event.getTextColor())) {
                event.setTextColor("#FFFFFF");
            }
            if (StringUtils.isBlank(event.getIsAllDay())) {
                event.setIsAllDay(AppConsts.NO);
                //range
                if (eventDto.getChosenDate().contains(":")) {
                    String[] dateRange = eventDto.getChosenDate().split(":");
                    if (dateRange.length == 2) {
                        event.setStartAt(DateUtils.parseDatetime(dateRange[0] + " " +
                                eventDto.getStartAt(), "yyyy-MM-dd HH:mm"));
                        event.setEndAt(DateUtils.addDay(DateUtils.parseDatetime(dateRange[1] + " " +
                                eventDto.getEndAt(),"yyyy-MM-dd HH:mm"), -1));
                    }
                } else {
                    //single
                    event.setStartAt(DateUtils.parseDatetime(eventDto.getChosenDate() + " " +
                            eventDto.getStartAt(), "yyyy-MM-dd HH:mm"));
                    event.setEndAt(DateUtils.parseDatetime(eventDto.getChosenDate() + " " +
                            eventDto.getEndAt(),"yyyy-MM-dd HH:mm"));
                }
            } else if (AppConsts.YES.equals(event.getIsAllDay())) {
                event.setStartAt(DateUtils.parseDatetime(eventDto.getChosenDate(), "yyyy-MM-dd"));
            }
            event.createAuditValues(AppContext.getFromWebThread());
            timesheetDAO.createCommonEvent(event);
            Timesheet timesheet = new Timesheet();
            timesheet.setEventReferralTbl(CommonEvent.TABLE_NAME);
            timesheet.setEventReferralId(event.getId());
            timesheet.setIsAutoAdded(AppConsts.NO);
            timesheet.setUserAcctId(AppContext.getFromWebThread().getUserId());
            JPAExecutor.save(timesheet);
            log.info("New Timesheet Event Created -> Type: " + event.getType());
        } else if (eventDto != null && StringUtils.parseIfIsLong(eventDto.getTimesheetId()) != null) {
            Timesheet timesheet = timesheetDAO.retrieveTimesheetById(StringUtils.parseIfIsLong(eventDto.getTimesheetId()));
            AppContext context = AppContext.getFromWebThread();
            if (timesheet != null && context.getUserId().equals(timesheet.getUserAcctId())) {
                CommonEvent event = timesheetDAO.retrieveCommonEventById(timesheet.getEventReferralId());
                if (event != null && !AppConsts.TIMESHEET_EVENT_TASK.equals(event.getType())) {
                    event.setTitle(eventDto.getTitle());
                    event.setContent(eventDto.getContent());
                    event.setColor(eventDto.getColor());
                    event.setTextColor(eventDto.getTextColor());
                    if (StringUtils.isBlank(event.getColor())) {
                        event.setColor("#3788D8");
                    }
                    if (StringUtils.isBlank(event.getTextColor())) {
                        event.setTextColor("#FFFFFF");
                    }
                    String start = DateUtils.getDateString(event.getStartAt(), "yyyy-MM-dd") + " " + eventDto.getStartAt();
                    String end = DateUtils.getDateString(event.getEndAt(), "yyyy-MM-dd") + " " + eventDto.getEndAt();
                    if (AppConsts.YES.equals(eventDto.getIsAllDay())) {
                        event.setIsAllDay(AppConsts.YES);
                        event.setStartAt(DateUtils.parseDatetime(start, "yyyy-MM-dd"));
                        if (DateUtils.getDaysBetween(DateUtils.truncate(event.getStartAt()), DateUtils.truncate(event.getEndAt())) >= 1) {
                            event.setEndAt(DateUtils.parseDatetime(end, "yyyy-MM-dd HH:mm"));
                        } else {
                            event.setEndAt(null);
                        }
                    } else {
                        event.setIsAllDay(AppConsts.NO);
                        event.setStartAt(DateUtils.parseDatetime(start, "yyyy-MM-dd HH:mm"));
                        event.setEndAt(DateUtils.parseDatetime(end, "yyyy-MM-dd HH:mm"));
                    }
                    JPAExecutor.update(event);
                    log.info("Timesheet Event Updated -> " + timesheet.getEventReferralTbl() + " : " + timesheet.getEventReferralId());
                }
            }
        }
    }

    public List<EventVO> searchEvents(Map<String, String> params) {
        if (params != null) {
            Map<String, Object> sp = new HashMap<>();
            sp.put("userId", StringUtils.parseIfIsLong(params.get("userId")));
            sp.put("startAt", DateUtils.parseDatetime(params.get("startAt")));
            sp.put("endAt", DateUtils.parseDatetime(params.get("endAt")));
            List<EventVO> selfEvents = timesheetDAO.searchCommonEvents(sp);
            selfEvents.removeIf(var -> AppConsts.TIMESHEET_EVENT_PUBLIC.equals(var.getEventType()));
            List<EventVO> publicEvents = timesheetDAO.searchPublicEvents(sp);
            selfEvents.addAll(publicEvents);
            return selfEvents;
        }
        return new ArrayList<>();
    }

    public EventVO getCommonEvent(Long timesheetId) {
        Timesheet timesheet = timesheetDAO.retrieveTimesheetById(timesheetId);
        if (timesheet != null) {
            CommonEvent event = timesheetDAO.retrieveCommonEventById(timesheet.getEventReferralId());
            if (!AppConsts.TIMESHEET_EVENT_TASK.equals(event.getType())) {
                EventVO vo = new EventVO();
                AppContext context = AppContext.getFromWebThread();
                vo.setId(event.getId());
                vo.setTimesheetId(timesheet.getId());
                vo.setTitle(event.getTitle());
                vo.setContent(event.getContent());
                vo.setColor(event.getColor());
                vo.setTextColor(event.getTextColor());
                vo.setAllDay(StringUtils.parseBoolean(event.getIsAllDay()));
                vo.setEventType(event.getType());
                vo.setCommon(AppConsts.YES);
                vo.setStartAt(DateUtils.getDateString(event.getStartAt(), "HH:mm"));
                vo.setEndAt(DateUtils.getDateString(event.getEndAt(), "HH:mm"));
                if (DateUtils.getDaysBetween(DateUtils.truncate(event.getStartAt()), DateUtils.truncate(event.getEndAt())) >= 1) {
                    vo.setRangeEvent(true);
                }
                vo.setEditable(!AppConsts.TIMESHEET_EVENT_PUBLIC.equals(event.getType()) ||
                        AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId()));
                return vo;
            }
        }
        return null;
    }

    @Transactional
    public void deleteTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetDAO.retrieveTimesheetById(timesheetId);
        AppContext context = AppContext.getFromWebThread();
        if (timesheet != null && context.getUserId().equals(timesheet.getUserAcctId())) {
            if (CommonEvent.TABLE_NAME.equals(timesheet.getEventReferralTbl())) {
                CommonEvent event = timesheetDAO.retrieveCommonEventById(timesheet.getEventReferralId());
                JPAExecutor.delete(event);
                JPAExecutor.delete(timesheet);
                log.info("Timesheet Deleted -> " + timesheet);
            }
        }
    }
}
