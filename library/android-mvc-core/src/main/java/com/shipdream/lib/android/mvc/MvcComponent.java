package com.shipdream.lib.android.mvc;

import com.shipdream.lib.poke.Component;
import com.shipdream.lib.poke.Provider;
import com.shipdream.lib.poke.ProviderByClassType;
import com.shipdream.lib.poke.exception.ProviderConflictException;
import com.shipdream.lib.poke.exception.ProviderMissingException;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

//TODO: documents
public class MvcComponent extends Component {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public MvcComponent(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Provider<T> findProvider(final Class<T> type, Annotation qualifier) throws ProviderMissingException {
        Provider<T> provider = null;
        try {
            provider = super.findProvider(type, qualifier);
        } catch (ProviderMissingException e) {
            //ignore since we will try to auto locate the impl class
        }
        if (provider == null) {
            Class<? extends T> impClass;
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                //Non concrete class needs to find its implementation class
                try {
                    impClass = (Class<T>) Class.forName(getClassName(type));
                } catch (ClassNotFoundException e) {
                    String msg = String.format("Can't find implementation class for %s. Make sure class %s exists, or its implementation is registered to graph's root component",
                            type.getName(), getClassName(type));
                    throw new ProviderMissingException(msg);
                }
            } else {
                //The type is a class then it's a construable by itself.
                impClass = type;
            }

            provider = new ProviderByClassType<>(type, impClass);

            if ((qualifier != null && !qualifier.equals(provider.getQualifier()))
                    || provider.getQualifier() != null) {
                String msg;
                if (qualifier == null) {
                    msg = String.format("Can't find implementation class for %s. Make sure class %s exists, or its implementation is registered to graph's root component",
                            type.getName(), getClassName(type));
                } else {
                    msg = String.format("Can't find implementation class for %s. Make sure class %s exists, or its implementation is registered to graph's root component",
                            type.getName(), getClassName(type) + "@" + qualifier.toString());
                }
                throw new ProviderMissingException(msg);
            }

            try {
                register(provider);
            } catch (ProviderConflictException e) {
                //Should not happen since otherwise it should have been found already
                e.printStackTrace();
            }
        }
        return provider;
    }

    @Override
    public Component register(@NotNull Provider provider) throws ProviderConflictException {
        super.register(provider);
        provider.registerCreationListener(new Provider.CreationListener() {
            @Override
            public void onCreated(Provider provider, Object instance) {
                if (instance instanceof Bean) {
                    final Bean bean = (Bean) instance;

                    bean.onCreated();
                    logger.trace("+++Bean created - '{}'.",
                            provider.type().getSimpleName());
                }
            }
        });
        return this;
    }

    private static String getClassName(Class type) {
        String pkg = type.getPackage().getName();
        String implClassName = pkg + ".internal." + type.getSimpleName() + "Impl";
        return implClassName;
    }

}
