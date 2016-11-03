package edu.asu.giles.aspects.access.github;

import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Service;

@Service
public class GitHubTemplateFactory {

    public GitHubTemplate createTemplate(String token) {
        return new GitHubTemplate(token);
    }
}
