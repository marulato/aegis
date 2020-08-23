package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.Mapper;
import org.legion.aegis.general.entity.BatchJobStatus;

@Mapper
public interface BatchJobDAO {

    BatchJobStatus getLastRunningStatus(String batchJobId);
}
