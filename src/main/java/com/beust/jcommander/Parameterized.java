package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Encapsulate a field or a method annotated with @Parameter or @DynamicParameter
 */
public class Parameterized {

  // Either a method or a field
  private Field field;
  private Method method;
  private Method getter;

  // Either of these two
  private WrappedParameter wrappedParameter;
  private ParametersDelegate parametersDelegate;

  public Parameterized(WrappedParameter wp, ParametersDelegate pd,
      Field field, Method method) {
    wrappedParameter = wp;
    this.method = method;
    this.field = field;
    if (this.field != null) {
      setFieldAccessible(this.field);
    }
    parametersDelegate = pd;
  }

  /**
   * Recursive handler for describing the set of classes while
   * using the setOfClasses parameter as a collector
   *
   * @param inputClass the class to analyze
   * @param setOfClasses the set collector to collect the results
     */
  private static void describeClassTree(Class<?> inputClass, Set<Class<?>> setOfClasses) {
    // can't map null class
    if(inputClass == null) {
      return;
    }

    // don't further analyze a class that has been analyzed already
    if(Object.class.equals(inputClass) || setOfClasses.contains(inputClass)) {
      return;
    }

    // add to analysis set
    setOfClasses.add(inputClass);

    // perform super class analysis
    describeClassTree(inputClass.getSuperclass(), setOfClasses);

    // perform analysis on interfaces
    for(Class<?> hasInterface : inputClass.getInterfaces()) {
      describeClassTree(hasInterface, setOfClasses);
    }
  }

  /**
   * Given an object return the set of classes that it extends
   * or implements.
   *
   * @param arg object to describe
   * @return set of classes that are implemented or extended by that object
   */
  private static Set<Class<?>> describeClassTree(Class<?> inputClass) {
    if(inputClass == null) {
      return Collections.emptySet();
    }

    // create result collector
    Set<Class<?>> classes = Sets.newLinkedHashSet();

    // describe tree
    describeClassTree(inputClass, classes);

    return classes;
  }

  public static List<Parameterized> parseArg(Object arg) {
    List<Parameterized> result = Lists.newArrayList();

    Class<?> rootClass = arg.getClass();

    // get the list of types that are extended or implemented by the root class
    // and all of its parent types
    Set<Class<?>> types = describeClassTree(rootClass);

    // analyze each type
    for(Class<?> cls : types) {

      // check fields
      for (Field f : cls.getDeclaredFields()) {
        Annotation annotation = f.getAnnotation(Parameter.class);
        Annotation delegateAnnotation = f.getAnnotation(ParametersDelegate.class);
        Annotation dynamicParameter = f.getAnnotation(DynamicParameter.class);
        if (annotation != null) {
          result.add(new Parameterized(new WrappedParameter((Parameter) annotation), null,
                  f, null));
        } else if (dynamicParameter != null) {
          result.add(new Parameterized(new WrappedParameter((DynamicParameter) dynamicParameter), null,
                  f, null));
        } else if (delegateAnnotation != null) {
          result.add(new Parameterized(null, (ParametersDelegate) delegateAnnotation,
                  f, null));
        }
      }

      // check methods
      for (Method m : cls.getDeclaredMethods()) {
        m.setAccessible(true);
        Annotation annotation = m.getAnnotation(Parameter.class);
        Annotation delegateAnnotation = m.getAnnotation(ParametersDelegate.class);
        Annotation dynamicParameter = m.getAnnotation(DynamicParameter.class);
        if (annotation != null) {
          result.add(new Parameterized(new WrappedParameter((Parameter) annotation), null,
                  null, m));
        } else if (dynamicParameter != null) {
          result.add(new Parameterized(new WrappedParameter((DynamicParameter) dynamicParameter), null,
                  null, m));
        } else if (delegateAnnotation != null) {
          result.add(new Parameterized(null, (ParametersDelegate) delegateAnnotation,
                  null, m));
        }
      }
    }

    return result;
  }

  public WrappedParameter getWrappedParameter() {
    return wrappedParameter;
  }

  public Class<?> getType() {
    if (method != null) {
      return method.getParameterTypes()[0];
    } else {
      return field.getType();
    }
  }

  public String getName() {
    if (method != null) {
      return method.getName();
    } else {
      return field.getName();
    }
  }

  public Object get(Object object) {
    try {
      if (method != null) {
        if (getter == null) {
            getter = method.getDeclaringClass()
                .getMethod("g" + method.getName().substring(1));
        }
        return getter.invoke(object);
      } else {
        return field.get(object);
      }
    } catch (SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
      throw new ParameterException(e);
    } catch (NoSuchMethodException e) {
      // Try to find a field
      String name = method.getName();
      String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
      Object result = null;
      try {
        Field field = method.getDeclaringClass().getDeclaredField(fieldName);
        if (field != null) {
          setFieldAccessible(field);
          result = field.get(object);
        }
      } catch(NoSuchFieldException | IllegalAccessException ex) {
        // ignore
      }
      return result;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Parameterized other = (Parameterized) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (method == null) {
      if (other.method != null)
        return false;
    } else if (!method.equals(other.method))
      return false;
    return true;
  }

  public boolean isDynamicParameter(Field field) {
    if (method != null) {
      return method.getAnnotation(DynamicParameter.class) != null;
    } else {
      return this.field.getAnnotation(DynamicParameter.class) != null;
    }
  }

  private static void setFieldAccessible(Field f) {
    if (Modifier.isFinal(f.getModifiers())) {
      throw new ParameterException(
        "Cannot use final field " + f.getDeclaringClass().getName() + "#" + f.getName() + " as a parameter;"
        + " compile-time constant inlining may hide new values written to it.");
    }
    f.setAccessible(true);
  }

  private static String errorMessage(Method m, Exception ex) {
    return "Could not invoke " + m + "\n    Reason: " + ex.getMessage();
  }

  public void set(Object object, Object value) {
    try {
      if (method != null) {
        method.invoke(object, value);
      } else {
          field.set(object, value);
      }
    } catch (IllegalAccessException | IllegalArgumentException ex) {
      throw new ParameterException(errorMessage(method, ex));
    } catch (InvocationTargetException ex) {
      // If a ParameterException was thrown, don't wrap it into another one
      if (ex.getTargetException() instanceof ParameterException) {
        throw (ParameterException) ex.getTargetException();
      } else {
        throw new ParameterException(errorMessage(method, ex));
      }
    }
  }

  public ParametersDelegate getDelegateAnnotation() {
    return parametersDelegate;
  }

  public Type getGenericType() {
    if (method != null) {
      return method.getGenericParameterTypes()[0];
    } else {
      return field.getGenericType();
    }
  }

  public Parameter getParameter() {
    return wrappedParameter.getParameter();
  }

  /**
   * @return the generic type of the collection for this field, or null if not applicable.
   */
  public Type findFieldGenericType() {
    if (method != null) {
      return null;
    } else {
      if (field.getGenericType() instanceof ParameterizedType) {
        ParameterizedType p = (ParameterizedType) field.getGenericType();
        Type cls = p.getActualTypeArguments()[0];
        if (cls instanceof Class) {
          return cls;
        }
      }
    }

    return null;
  }

  public boolean isDynamicParameter() {
    return wrappedParameter.getDynamicParameter() != null;
  }

}
