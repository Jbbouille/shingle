package fr.shingle.extensions;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.shingle.model.Model;
import com.fasterxml.jackson.databind.BeanProperty;
import com.google.common.base.Strings;
import fr.shingle.Extension;

public class XmlJavaTypeAdapterExtension implements Extension {


    @Override
    public void enrich(BeanProperty beanProperty, Model.IModel model) {
        if (!Strings.isNullOrEmpty(model.getExtensions().get("example"))) {
            return;
        }

        XmlJavaTypeAdapter adapter = beanProperty.getAnnotation(XmlJavaTypeAdapter.class);
        if (adapter == null) {
            return;
        }

        exampleOf(beanProperty.getType().getRawClass()).ifPresent(example -> {
            try {
                Object value = adapter.value().newInstance().marshal(example);
                model.getExtensions().put("example", value.toString());
            } catch (Exception e) {
                // Nothing to do, if we can't generate an example, well, it's no biggie
                return;
            }
        });
    }

    private Optional<Object> exampleOf(Class<?> clazz) {
        if (clazz == Date.class) {
            return Optional.of(new Date());
        }

        if (clazz == Locale.class) {
            return Optional.of(Locale.getDefault());
        }

        return Optional.empty();
    }


}
