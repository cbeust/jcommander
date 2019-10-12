package com.beust.jcommander;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;



public class ConstructorOrValueOfConverter {

    public static IStringConverter tryCreateConverter(String aOptionName, Class aTargetType) {

        Method m = fetchPublicStaticValueOfMethodWithCorrectReturnType(aOptionName, aTargetType);
        if(m!=null) return new ValueOfConverter(aOptionName, m);

        Constructor cons = fetchPublicStringConstructor(aTargetType);
        if(cons!=null) return new ConstructorConverter(aOptionName, cons);

        throw new ParameterException("Can't find any implicit converter mechanism for option '"+aOptionName+"' and type '"+aTargetType+"'");
    }

    private static Constructor fetchPublicStringConstructor(Class aTargetType) {
        try {
            Constructor cons = aTargetType.getDeclaredConstructor(String.class);
            if(cons==null || !Modifier.isPublic(cons.getModifiers())) {
                return null;
            }
            return cons;
        }
        catch(NoSuchMethodException e) {
            return null;
        }
    }

    private static  Method fetchPublicStaticValueOfMethodWithCorrectReturnType(String aOptionName, Class aTargetType) {
        try {
            Method method = aTargetType.getDeclaredMethod("valueOf", String.class);
            if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) {
                throw new ParameterException("Can't find any implicit converter mechanism for option '" + aOptionName + "' and type '" + aTargetType + "'");
            }
            if (!aTargetType.isAssignableFrom(method.getReturnType())) {
                throw new ParameterException("The converting valueOf method for option '" + aOptionName + "' returns type '" + method.getReturnType() + "' which is not compatible to the expected type '" + aTargetType + "'");
            }
            return method;
        }
        catch(NoSuchMethodException e) {
            return null;
        }
    }

    private static class ConstructorConverter implements IStringConverter {
        private final Constructor constructor;
        private final String optionName;

        public ConstructorConverter(String aOptionName, Constructor aConstructor) {
            if(aConstructor==null) throw new NullPointerException();

            optionName = aOptionName;
            constructor = aConstructor;
        }

        @Override
        public Object convert(String value) {
            try {
                return constructor.newInstance(value);
            }
            catch(InstantiationException|IllegalAccessException|InvocationTargetException e) {
                throw new ParameterException("Can't convert '"+value+"' for option '"+optionName+"'", e);
            }
        }
    }

    private static class ValueOfConverter implements IStringConverter {
        private final Method valueOf;
        private final String optionName;

        public ValueOfConverter(String aOptionName, Method aMethod) {
            if(aMethod==null) throw new NullPointerException();

            optionName = aOptionName;
            valueOf = aMethod;
        }

        @Override
        public Object convert(String value) {
            try {
                return valueOf.invoke(null, value);
            }
            catch(IllegalAccessException|InvocationTargetException e) {
                throw new ParameterException("Can't convert '"+value+"' for option '"+optionName+"'", e);
            }
        }
    }
}
