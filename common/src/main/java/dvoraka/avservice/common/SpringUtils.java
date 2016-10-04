package dvoraka.avservice.common;

import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Spring utils.
 */
public final class SpringUtils {

    private SpringUtils() {
    }

    public static void printBeansInfo(ApplicationContext context) {
        List<String> list = Arrays.asList(context.getBeanDefinitionNames());
        Collections.sort(list);

        System.out.println("Beans:");
        list.forEach(System.out::println);
        System.out.println();
        System.out.println("Size: " + list.size());
    }
}
