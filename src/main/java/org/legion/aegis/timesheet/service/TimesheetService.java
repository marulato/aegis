package org.legion.aegis.timesheet.service;

import org.legion.aegis.timesheet.dto.EventDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TimesheetService {

    @Transactional
    public void createCommonEvent(EventDto eventDto) {

    }
}
