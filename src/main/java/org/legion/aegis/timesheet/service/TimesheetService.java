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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimesheetService {

    private final TimesheetDAO timesheetDAO = SpringUtils.getBean(TimesheetDAO.class);

    @Transactional
    public void createCommonEvent(EventDto eventDto) throws Exception {
        if (eventDto != null) {
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
                event.setStartAt(DateUtils.parseDatetime(eventDto.getChosenDate() + " " +
                        eventDto.getStartAt(), "yyyy-MM-dd HH:mm"));
                event.setEndAt(DateUtils.parseDatetime(eventDto.getChosenDate() + " " +
                        eventDto.getEndAt(),"yyyy-MM-dd HH:mm"));
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
}
