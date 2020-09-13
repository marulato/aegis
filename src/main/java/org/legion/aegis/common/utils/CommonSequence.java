package org.legion.aegis.common.utils;

import org.legion.aegis.admin.entity.Sequence;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.general.dao.SequenceDAO;

public class CommonSequence {

    public static long getNextSequenceValue(String name) {
        long value = -1L;
        SequenceDAO sequenceDAO = SpringUtils.getBean(SequenceDAO.class);
        Sequence sequence = sequenceDAO.getSequence(name);
        if (sequence != null) {
            value = sequence.getValue() + sequence.getStep();
            if (sequence.getMaxValue()!= null && sequence.getMaxValue() > 0 && value > sequence.getMaxValue()) {
                value = sequence.getMaxValue();
                sequence.setValue(value);
            } else if (sequence.getMinValue() != null && sequence.getMinValue() >= 0 && value < sequence.getMinValue()) {
                sequence.setValue(value);
                value = sequence.getMinValue();
            } else {
                sequence.setValue(value);
            }
            JPAExecutor.update(sequence);
        }
        return value;
    }

    public static void createDefaultSequence(String name) {
        if (StringUtils.isNotBlank(name)) {
            Sequence sequence = new Sequence();
            sequence.setName(name);
            sequence.setMinValue(0L);
            sequence.setValue(0L);
            sequence.setMaxValue(Long.MAX_VALUE);
            sequence.setStep(1);
            JPAExecutor.save(sequence);
        }
    }
}
