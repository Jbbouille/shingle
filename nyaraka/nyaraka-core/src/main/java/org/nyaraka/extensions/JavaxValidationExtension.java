package org.nyaraka.extensions;

import static java.util.Arrays.asList;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.nyaraka.Extension;
import org.nyaraka.model.Model;
import com.fasterxml.jackson.databind.BeanProperty;
import com.google.common.collect.Sets;
import org.nyaraka.model.Constraint;

public class JavaxValidationExtension implements Extension {
    @Override
    public void enrich(Parameter javaParameter, org.nyaraka.model.Parameter parameter) {
        parameter.constraints.addAll(constraints(asList(javaParameter.getAnnotations())));
    }

    @Override
    public void enrich(BeanProperty beanProperty, Model.IModel model) {
        model.getConstraints().addAll(constraints(beanProperty.getMember().annotations()));
    }

    private List<Constraint> constraints(Iterable<Annotation> annotations) {
        List<Constraint> res = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getAnnotation(javax.validation.Constraint.class) != null) {
                res.add(new Constraint(annotation.annotationType().getSimpleName(),
                                       args(annotation)));
            }
        }
        return res;
    }

    private static Map<String, String> args(Annotation a) {
        Map<String, String> values = new HashMap<>();
        Set<String> exclude = Sets.newHashSet("equals", "toString", "hashCode", "annotationType", "groups", "payload", "message");
        Arrays.stream(a.getClass().getDeclaredMethods())
              .filter(m -> !exclude.contains(m.getName()))
              .forEach(m -> addValue(a, m, values));
        return values;
    }

    private static void addValue(Annotation a, Method m, Map<String, String> values) {
        try {
            Object local = m.invoke(a);
            if (local.getClass().isArray()) {
                values.put(m.getName(), Arrays.stream((Object[]) local).map(Object::toString).collect(Collectors.joining(", ")));
            } else {
                values.put(m.getName(), local.toString());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
