package edu.asu.diging.gilesecosystem.web.core.velocity.impl;

import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.core.velocity.IVelocityBuilder;

@Service
@PropertySource("classpath:/config.properties")
public class VelocityBuilder implements IVelocityBuilder {

    private VelocityEngine engine;

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() throws Exception {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        engine.init();
    }

    @Override
    public String getRenderedTemplate(String templateName, Map<String, Object> contextProperties)
            throws ResourceNotFoundException, ParseErrorException, Exception {
        Template template = engine.getTemplate(templateName);
        VelocityContext context = new VelocityContext();

        for (String key : contextProperties.keySet()) {
            context.put(key, contextProperties.get(key));
        }

        context.put("giles_url", env.getProperty("giles_url"));

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
