package org.legion.aegis.general.controller;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.common.validation.Email;
import org.legion.aegis.general.dto.EmailDto;
import org.legion.aegis.general.service.EmailService;
import org.legion.aegis.general.vo.EmailVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class EmailBoxController {

    private final EmailService emailService;
    public static final String SESSION_KEY = "SESSION_EMAIL";
    public static final String SESSION_KEY_DRAFT = "SESSION_DRAFT";

    public EmailBoxController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/web/email/inbox")
    @RequiresLogin
    public ModelAndView inboxPage() {
        ModelAndView modelAndView = new ModelAndView("general/emailInbox");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.removeAttribute(SESSION_KEY);
        SessionManager.removeAttribute(SESSION_KEY_DRAFT);
        return modelAndView;
    }

    @GetMapping("/web/email/outbox")
    @RequiresLogin
    public ModelAndView outboxPage() {
        ModelAndView modelAndView = new ModelAndView("general/emailOutbox");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.removeAttribute(SESSION_KEY);
        SessionManager.removeAttribute(SESSION_KEY_DRAFT);
        return modelAndView;
    }

    @GetMapping("/web/email/draftBox")
    @RequiresLogin
    public ModelAndView draftBoxPage() {
        ModelAndView modelAndView = new ModelAndView("general/draftBox");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.removeAttribute(SESSION_KEY);
        SessionManager.removeAttribute(SESSION_KEY_DRAFT);
        return modelAndView;
    }

    @GetMapping("/web/email/recycle")
    @RequiresLogin
    public ModelAndView recycleBoxPage() {
        ModelAndView modelAndView = new ModelAndView("general/emailRecycleBox");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.removeAttribute(SESSION_KEY);
        SessionManager.removeAttribute(SESSION_KEY_DRAFT);
        return modelAndView;
    }

    @GetMapping("/web/email/compose")
    @RequiresLogin
    public ModelAndView composePage() {
        ModelAndView modelAndView = new ModelAndView("general/compose");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.removeAttribute(SESSION_KEY);
        SessionManager.removeAttribute(SESSION_KEY_DRAFT);
        return modelAndView;
    }

    @PostMapping("/web/email/send")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody sendEmail(EmailDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, "send");
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            List<MultipartFile> files = req.getFiles("attachments");
            files.removeIf(var -> var.getSize() <= 0);
            dto.setAttachments(files);
            if (StringUtils.isBlank(dto.getOutboxId())) {
                emailService.sendEmail(dto);
            } else {
                emailService.sendDraft(dto);
            }
        }
        return manager.respond();
    }

    @PostMapping("/web/email/inbox")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody inbox(@RequestBody SearchParam searchParam) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(emailService.searchInbox(searchParam));
        return manager.respond();
    }

    @PostMapping("/web/email/outbox")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody outbox(@RequestBody SearchParam searchParam) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(emailService.searchOutbox(searchParam));
        return manager.respond();
    }

    @PostMapping("/web/email/draftBox")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody draftBox(@RequestBody SearchParam searchParam) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(emailService.searchDraftBox(searchParam));
        return manager.respond();
    }

    @PostMapping("/web/email/recycle")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody recycleBox(@RequestBody SearchParam searchParam) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(emailService.searchRecycleBox(searchParam));
        return manager.respond();
    }

    @GetMapping("/web/email/{box}/{id}")
    @RequiresLogin
    public ModelAndView readEmail(@PathVariable("id") String id, @PathVariable("box") String box) throws Exception {
        ModelAndView modelAndView = new ModelAndView("general/read");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        EmailVO vo = null;
        if ("inbox".equals(box)) {
            vo = emailService.readEmail(StringUtils.parseIfIsLong(id));
        } else if ("outbox".equals(box)) {
            vo = emailService.readOutbox(StringUtils.parseIfIsLong(id));
        }
        modelAndView.addObject("email", vo);
        SessionManager.setAttribute(SESSION_KEY, vo);
        return modelAndView;
    }

    @PostMapping("/web/email/reply")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody reply(EmailDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, "reply");
        EmailVO vo = (EmailVO) SessionManager.getAttribute(SESSION_KEY);
        if (!violations.isEmpty() || vo == null) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            List<MultipartFile> files = req.getFiles("attachments");
            files.removeIf(var -> var.getSize() <= 0);
            dto.setAttachments(files);
            emailService.reply(dto, vo.getEmailId());
        }
        return manager.respond();
    }

    @PostMapping("/web/email/compose/draft")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody saveDraftOnCompose(EmailDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, "send");
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            List<MultipartFile> files = req.getFiles("attachments");
            files.removeIf(var -> var.getSize() < 0);
            dto.setAttachments(files);
            emailService.saveEmailDraft(dto);
        }
        return manager.respond();
    }

    @GetMapping("/web/email/draftBox/{outboxId}")
    @RequiresLogin
    public ModelAndView readDraft(@PathVariable("outboxId") String outboxId) throws Exception {
        AppContext context = AppContext.getFromWebThread();
        ModelAndView modelAndView = new ModelAndView("general/readDraft");
        modelAndView.addObject("role", context.getRoleId());
        EmailVO draft = emailService.readDraft(StringUtils.parseIfIsLong(outboxId));
        modelAndView.addObject("draft", draft);
        SessionManager.setAttribute(SESSION_KEY_DRAFT, draft);
        return modelAndView;
    }

    @GetMapping("/web/email/draftBox/{outboxId}/{attachId}")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody deleteAttachmentInDraft(@PathVariable("outboxId") String outboxId,
                                                    @PathVariable("attachId") String attachId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        emailService.deleteAttachmentInDraft(StringUtils.parseIfIsLong(outboxId),
                StringUtils.parseIfIsLong(attachId));
        return manager.respond();
    }

    @GetMapping("/web/email/draftBox/delete")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody deleteDraft() {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        EmailVO vo = (EmailVO) SessionManager.getAttribute(SESSION_KEY_DRAFT);
        emailService.deleteDraft(vo.getOutboxId());
        return manager.respond();
    }

    @GetMapping("/web/email/moveToRecycleBin")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody moveToRecycleBin() {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        EmailVO vo = (EmailVO) SessionManager.getAttribute(SESSION_KEY);
        emailService.moveToRecycleBin(vo.getInboxId());
        return manager.respond();
    }

    @GetMapping("/web/email/recover")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody recover() {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        EmailVO vo = (EmailVO) SessionManager.getAttribute(SESSION_KEY);
        emailService.recover(vo.getInboxId());
        return manager.respond();
    }
}
