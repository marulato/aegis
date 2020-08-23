package org.legion.aegis.common.utils;


import org.legion.aegis.admin.entity.Sequence;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.general.dao.SequenceDAO;

import java.security.SecureRandom;
import java.util.Date;

public class MiscGenerator {

    private static final String SEQ_STAFF_ID = "STAFF_ID";
    private static final char[] NUMBER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] UPPER_CASE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                                            'N', 'O', 'P', 'Q', 'R', 'S','T', 'U', 'V', 'W', 'X', 'Y','Z'};
    private static final char[] LOWER_CASE = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                            'n', 'o', 'p', 'q', 'r', 's','t', 'u', 'v', 'w', 'x', 'y','z'};
    private static final char[] SYMBOL = {'!', '@', '$', '&', '/', '~', '*', '?', '^', '%', '#'};

    private static final char[] RANDOM_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                                                'N', 'O', 'P', 'Q', 'R', 'S','T', 'U', 'V', 'W', 'X', 'Y','Z',
                                                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                                'n', 'o', 'p', 'q', 'r', 's','t', 'u', 'v', 'w', 'x', 'y','z',
                                                '!', '@', '$', '&', '/', '~', '*', '?', '^', '%', '#'};



    public static String getNextStaffId(Date joinedDate, Integer deptId) {
        if (joinedDate != null && deptId != null) {
            String deptIdStr = String.valueOf(deptId);
            if (deptIdStr.length() == 1) {
                deptIdStr = "0" + deptIdStr;
            }
            String yy =DateUtils.getDateString(joinedDate, "yy");
            String nextSeq = String.valueOf(getNextSequenceValue(SEQ_STAFF_ID));
            if (nextSeq.length() == 1) {
                nextSeq = "00" + nextSeq;
            } else if (nextSeq.length() == 2) {
                nextSeq = "0" + nextSeq;
            }
            return yy + deptIdStr + nextSeq;
        }
        return null;
    }

    public static String generateInitialPassword() {
        return generateInitialPassword(10);
    }

    public static String generateInitialPassword(int length) {
        if (length <= 0) {
            length = 10;
        }
        SecureRandom random = new SecureRandom();
        StringBuilder pwd = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = RANDOM_CHAR[random.nextInt(RANDOM_CHAR.length)];
            pwd.append(ch);
        }
        return pwd.toString();
    }

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
}
