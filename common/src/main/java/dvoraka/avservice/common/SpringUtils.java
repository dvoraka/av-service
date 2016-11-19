package dvoraka.avservice.common;

import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Various Spring utilities.
 */
public final class SpringUtils {

    private SpringUtils() {
    }

    /**
     * Prints sorted list of bean names according to a given context.
     *
     * @param context the context
     */
    public static void printBeansList(ApplicationContext context) {
        Objects.requireNonNull(context, "Context must not be null!");

        List<String> list = Arrays.asList(context.getBeanDefinitionNames());
        Collections.sort(list);

        System.out.println("Beans:"); //NOSONAR
        list.forEach(System.out::println); //NOSONAR
        System.out.println("\nSize: " + list.size()); //NOSONAR
    }
}
