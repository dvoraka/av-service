package dvoraka.avservice.rest.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Spring Web initializer.
 */
public class SpringWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger log = LogManager.getLogger(SpringWebInitializer.class);

    private static final String DEFAULT_PROFILES = "core, rest, rest-local, db";
    private static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";


    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{SpringWebConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        String profiles = System.getProperty(SPRING_PROFILES_ACTIVE_KEY);
        if (profiles == null) {
            servletContext.setInitParameter(SPRING_PROFILES_ACTIVE_KEY, DEFAULT_PROFILES);
        } else {
            servletContext.setInitParameter(SPRING_PROFILES_ACTIVE_KEY, profiles);
        }

        log.info("Active profiles: {}",
                servletContext.getInitParameter(SPRING_PROFILES_ACTIVE_KEY));
    }
}
