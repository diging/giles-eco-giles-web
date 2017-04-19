package edu.asu.diging.gilesecosystem.web.controllers.admin;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.migrate.impl.MigrationManager;
import edu.asu.diging.gilesecosystem.web.migrate.impl.MigrationResult;

@Controller
public class MigrateUserDataController {

    @Autowired
    private MigrationManager migrateManager;
    
    @RequestMapping(value = "/admin/migrate")
    public String showPage(Model model) throws InterruptedException, ExecutionException {
        MigrationResult result = migrateManager.checkResults();
        if (result == null) {
            return "admin/migrate/running";
        }
        
        model.addAttribute("result", result);
        return "admin/migrate";
    }
    
    @RequestMapping(value = "/admin/migrate", method=RequestMethod.POST)
    public String startMigration(@RequestParam String username) throws UnstorableObjectException {
        if (username == null) {
            return "redirect:/admin/migrate";
        }
        migrateManager.runMigrations(username);
        return "redirect:/admin/migrate";
    }
}
