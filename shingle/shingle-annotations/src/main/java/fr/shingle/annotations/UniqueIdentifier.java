package fr.shingle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In case more than one resource share the same verb and path, you can use this annotation to disambiguate between them.
 * The annotation value will be stored in the id field of the resource and can be used by clients, e.g. shingle-ui to generate
 * unique bookmarkable urls.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UniqueIdentifier {
    String value();
}
