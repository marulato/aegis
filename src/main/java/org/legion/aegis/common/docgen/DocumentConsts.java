package org.legion.aegis.common.docgen;

import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

public class DocumentConsts {

    public static final Rectangle PAGE_A4 = PageSize.A4;
    public static final Rectangle PAGE_A4_ACROSS = PageSize.A4.rotate();
    public static final int ALIGN_CENTER = Element.ALIGN_CENTER;
    public static final int ALIGN_LEFT = Element.ALIGN_LEFT;
    public static final int ALIGN_RIGHT = Element.ALIGN_RIGHT;

    public static final String DOC_STATUS_ACTIVE  = "ACTIVE";
    public static final String DOC_STATUS_VOIDED  = "VOIDED";
    public static final String DOC_STATUS_DELETED = "DELETED";

    public static final String DOC_CATEGORY_REGISTRATION    = "REG";
    public static final String DOC_CATEGORY_NOTIFICATION    = "NOTICE";
    public static final String DOC_CATEGORY_APPLICATION     = "APP";
    public static final String DOC_CATEGORY_CERTIFICATION   = "CERT";

    public static final String DOC_TYPE_REG_RESUME      = "REG_RESUME";
    public static final String DOC_TYPE_REG_OFFER       = "REG_OFFER";
    public static final String DOC_TYPE_REG_MER         = "REG_MER";
    public static final String DOC_TYPE_REG_GRADUATION  = "REG_GRAD";
    public static final String DOC_TYPE_REG_DIPLOMA     = "REG_DIPLOMA";
    public static final String DOC_TYPE_REG_CONTRACT    = "REG_CONTRACT";

    public static final String DOC_UPLOADED_FOR_NEW_EMP      = "New Employee Registration";
    public static final String DOC_UPLOADED_FOR_PERSONAL_APP = "Personal Application";

}
