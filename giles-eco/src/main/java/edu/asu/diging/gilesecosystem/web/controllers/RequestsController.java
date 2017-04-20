package edu.asu.diging.gilesecosystem.web.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.gilesecosystem.web.service.IRequestManager;

@Controller
public class RequestsController {
    
    @Autowired
    private IRequestManager requestManager;

    @RequestMapping(value = "/admin/requests")
    public String showRequestsPage(Model model) throws InterruptedException, ExecutionException {
        if (requestManager.getResendingResults() == null) {
            return "admin/requests/inProgress";
        }
        model.addAttribute("resendResult", requestManager.getResendingResults());
        return "admin/requests";
    }
    
    @RequestMapping(value = "/admin/requests/resend", method = RequestMethod.POST)
    public String resendMessages(Model model) throws InterruptedException, ExecutionException {
        if (requestManager.getResendingResults() == null) {
            return "admin/requests/inProgress";
        }
        requestManager.startResendingRequests();
        return  "redirect:/admin/requests";
    }
}
