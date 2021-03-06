/*
 * File: Options.java
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not use this file except in compliance with the Universal Permissive
 * License (UPL), Version 1.0 (the "License.")
 *
 * You may obtain a copy of the License at https://opensource.org/licenses/UPL.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.oracle.cloud.cache.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * An immutable collection of zero or more values (options),
 * internally arranged as a map, keyed by the concrete type of each option in
 * the collection.
 *
 * @param <T> the base type of the options in the collection
 * @author bko  2015.07.24
 */
@SuppressWarnings("unchecked")
public class Options<T>
{
    /**
     * A constant to represent an empty {@link Options} collection.
     */
    private static final Options EMPTY = new EmptyOptions();

    /**
     * The map of the options, keyed by their concrete class.
     */
    private LinkedHashMap<Class<? extends T>, T> mapOptions;
    private Class<T>                             clsType;


    /**
     * Constructs an empty {@link Options} collection.
     *
     * @param clsType the {@link Class} of the base type of the options
     *                in the collection
     */
    private Options(Class<T> clsType)
    {
        this.mapOptions = new LinkedHashMap<>();
        this.clsType    = clsType;
    }


    /**
     * Constructs an {@link Options} collection based on an array of option
     * values.
     *
     * @param clsType  the {@link Class} of the base type of the options
     *                 in the collection
     * @param aOptions the array of options to add to the collection
     */
    private Options(Class<T> clsType,
                    T[]      aOptions)
    {
        this.mapOptions = new LinkedHashMap<>();
        this.clsType    = clsType;

        addAll(aOptions);
    }


    /**
     * Returns the option for a specified concrete type from the collection.
     *
     * <p>
     * Should the option not exist in the collection, the following attempts are made (in order).
     * First, look for and evaluate the annotated "public static U getter()" method.
     * Next, look for and evaluate the annotated "public static U value = ...;" field.
     * Next, look for and evaluate the annotated public no-argument constructor.
     * Next, if the class is an enum, look for an annotated field on an enum.
     * If all the above fail, return a  <code>null</code>.</p>
     *
     * @param clzOption the concrete type of option to obtain
     * @param <U>       the type of value
     * @return the option of the specified type, or if undefined, the
     * suitable default value (or <code>null</code> if one can't be
     * determined)
     */
    public <U extends T> U get(Class<U> clzOption)
    {
        return get(clzOption, getDefaultFor(clzOption));
    }


    /**
     * Returns the option of a specified concrete type from the collection.
     * Should the type of option not exist, the specified default is returned.
     *
     * @param clzOption  the type of option to obtain
     * @param optDefault the option to return if the specified type is not defined
     * @param <U>        the type of value
     * @return the option of the specified type, or
     * the default if it's not defined
     */
    public <U extends T> U get(Class<U> clzOption,
                               U        optDefault)
    {
        if (clzOption == null)
        {
            return null;
        }
        else
        {
            T option = mapOptions.get(clzOption);

            if (option == null)
            {
                return optDefault;
            }
            else
            {
                return (U) option;
            }
        }
    }


    /**
     * Determines if an option of the specified concrete type is in the
     * collection.
     *
     * @param clzOption the class of option
     * @param <O>       the type of option
     * @return <code>true</code>, if the class of option is in the {@link Options}
     * <code>false</code> otherwise
     */
    public <O extends T> boolean contains(Class<O> clzOption)
    {
        return get(clzOption) != null;
    }


    /**
     * Determines if the specified option (and type) is in the {@link Options}.
     *
     * @param option the option
     * @return <code>true</code> if is in the {@link Options},
     * <code>false</code> otherwise
     */
    public boolean contains(T option)
    {
        if (option == null)
        {
            return false;
        }

        Class<? extends T> clzOption = getClassOf(option);
        Object             value     = get(clzOption);

        return value != null && value.equals(option);
    }


    /**
     * Returns an {@link Iterable} over all of the options in the collection
     * that are an instance of the specified class.
     *
     * @param clz the required class
     * @param <O> the type of option
     * @return the options of the required class
     */
    public <O> Iterable<O> getInstancesOf(Class<O> clz)
    {
        return mapOptions.values().stream().filter(clz::isInstance).map(value -> (O) value)
        .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Returns the current collection of options as an array.
     *
     * @return an array of options
     */
    public T[] asArray()
    {
        T[] aOptions = (T[]) new Object[mapOptions.size()];
        int i        = 0;

        for (T option : mapOptions.values())
        {
            aOptions[i++] = option;
        }

        return aOptions;
    }


    @Override
    public String toString()
    {
        StringBuilder bldrResult = new StringBuilder();

        bldrResult.append("Options{");

        boolean fFirst = true;

        for (T option : mapOptions.values())
        {
            if (fFirst)
            {
                fFirst = false;
            }
            else
            {
                bldrResult.append(", ");
            }

            bldrResult.append(option);
        }

        bldrResult.append("}");

        return bldrResult.toString();
    }


    /**
     * Constructs an {@link Options} collection given an array of options.
     *
     * @param clsType  the {@link Class} of the base type of the options
     *                 in the collection
     * @param aOptions the array of options
     * @param <T>      the type of options
     * @return an {@link Options} collection
     */
    @SafeVarargs
    public static <T> Options<T> from(Class<T> clsType,
                                      T...     aOptions)
    {
        return aOptions == null || aOptions.length == 0 ? empty() : new Options<>(clsType, aOptions);
    }


    /**
     * Constructs an empty {@link Options} collection.
     *
     * @param <T> the type of options
     * @return an empty {@link Options} collection
     */
    public static <T> Options<T> empty()
    {
        return EMPTY;
    }


    /**
     * Adds an option to the collection, replacing an
     * existing option of the same concrete type if one exists.
     *
     * @param option the option to add
     * @return the {@link Options} to permit fluent-style method calls
     */
    private Options<T> add(T option)
    {
        Class<T> clz = getClassOf(option);

        mapOptions.put(clz, option);

        return this;
    }


    /**
     * Adds an array of options to the collection, replacing
     * existing options of the same concrete type where they exist.
     *
     * @param aOptions the options to add
     * @return the {@link Options} to permit fluent-style method calls
     */
    private Options<T> addAll(T[] aOptions)
    {
        if (aOptions != null)
        {
            for (T option : aOptions)
            {
                add(option);
            }
        }

        return this;
    }


    /**
     * Adds all of the options in the specified {@link Options}
     * to this collection, replacing existing options of the same concrete
     * type where they exist.
     *
     * @param options the {@link Options} to add
     * @return the {@link Options} to permit fluent-style method calls
     */
    private Options<T> addAll(Options<? extends T> options)
    {
        for (T option : options.asArray())
        {
            add(option);
        }

        return this;
    }


    /**
     * Returns the concrete type of an option.
     *
     * @param option the option
     * @return the concrete {@link Class} that directly extends / implements
     * the value interface
     * or <code>null</code> if the value is <code>null</code>
     */
    private Class<T> getClassOf(T option)
    {
        return option == null ? null : getClassOf(option.getClass());
    }


    /**
     * Returns the concrete type that directly implements / extends the {@link #clsType}
     * option {@link Class}.
     *
     * @param classOfOption the class that somehow implements or extends {@link #clsType}
     * @return the concrete {@link Class} that directly extends / implements the {@link #clsType}
     * class or <code>null</code> if the specified {@link Class} doesn't implement or extend
     * the {@link #clsType} class
     */

    private <O extends T> Class<O> getClassOf(Class<?> classOfOption)
    {
        if (clsType.equals(classOfOption))
        {
            return (Class<O>) classOfOption;
        }

        // the hierarchy of classes we've visited
        // (so that we can traverse it later to find non-abstract classes)
        Stack<Class<?>> hierarchy = new Stack<>();

        while (classOfOption != null)
        {
            // remember the current class
            hierarchy.push(classOfOption);

            for (Class<?> interfaceClass : classOfOption.getInterfaces())
            {
                if (clsType.equals(interfaceClass))
                {
                    // when the option class is directly implemented by a class,
                    // we return the first non-abstract class in the hierarchy.
                    while (classOfOption != null
                           && Modifier.isAbstract(classOfOption.getModifiers())
                           &&!classOfOption.isInterface())
                    {
                        classOfOption = hierarchy.isEmpty() ? null : hierarchy.pop();
                    }

                    return (Class<O>) (classOfOption.isSynthetic() ? interfaceClass : classOfOption);
                }
                else if (clsType.isAssignableFrom(interfaceClass))
                {
                    // ensure that we have a concrete class in our hierarchy
                    while (classOfOption != null
                           && Modifier.isAbstract(classOfOption.getModifiers())
                           &&!classOfOption.isInterface())
                    {
                        classOfOption = hierarchy.isEmpty() ? null : hierarchy.pop();
                    }

                    if (classOfOption == null)
                    {
                        // when the hierarchy is entirely abstract, we can't determine a concrete Option type
                        return null;
                    }
                    else
                    {
                        // when the option is a super class of an interface,
                        // we return the interface that's directly extending it.

                        // We should search to find the interface that is directly
                        // extending the option type and not just assume that the interfaceClass
                        // is directly implementing it
                        return (Class<O>) interfaceClass;
                    }
                }
            }

            classOfOption = classOfOption.getSuperclass();
        }

        return null;
    }


    /**
     * Attempts to determine a default value for a given class.
     *
     * <p>Aan attempt is made to determine a suitable default based on the use
     * of the {@link Default} annotation in the specified class, firstly by
     * looking for and evaluating the annotated "public static U getter()"
     * method, failing that, looking for and evaluating the annotated
     * "public static U value = ...;" field, failing that, looking for an
     * evaluating the annotated public no args constructor and finally, failing
     * that, looking for an annotated field on an enum
     * (assuming the class is an enum).  Failing these approaches,
     * <code>null</code> is returned.</p>
     *
     * @param clzOption the class
     * @param <U>       the type of value
     * @return a default value or <code>null</code> if a default can't be
     * determined
     */
    protected <U extends T> U getDefaultFor(Class<U> clzOption)
    {
        if (clzOption == null)
        {
            return null;
        }
        else
        {
            for (Method method : clzOption.getMethods())
            {
                int modifiers = method.getModifiers();

                if (method.getAnnotation(Default.class) != null
                    && method.getParameterCount() == 0
                    && Modifier.isStatic(modifiers)
                    && Modifier.isPublic(modifiers)
                    && clzOption.isAssignableFrom(method.getReturnType()))
                {
                    try
                    {
                        return (U) method.invoke(null);
                    }
                    catch (Exception e)
                    {
                        // carry on... perhaps we can use another approach?
                    }
                }
            }
        }

        for (Field field : clzOption.getFields())
        {
            int modifiers = field.getModifiers();

            if (field.getAnnotation(Default.class) != null
                && Modifier.isStatic(modifiers)
                && Modifier.isPublic(modifiers)
                && clzOption.isAssignableFrom(field.getType()))
            {
                try
                {
                    return (U) field.get(null);
                }
                catch (Exception e)
                {
                    // carry on... perhaps we can use another approach?
                }
            }
        }

        try
        {
            Constructor constructor = clzOption.getConstructor();

            int         modifiers   = constructor.getModifiers();

            if (constructor.getAnnotation(Default.class) != null && Modifier.isPublic(modifiers))
            {
                try
                {
                    return (U) constructor.newInstance();
                }
                catch (Exception e)
                {
                    // carry on... perhaps we can use another approach?
                }
            }
        }
        catch (NoSuchMethodException e)
        {
            // carry on... there's no no-args constructor
        }

        // couldn't find a default so let's return null
        return null;
    }


    /**
     * An optimized {@link Options} implementation for representing empty
     * {@link Options}.
     *
     * @param <T> the type of the {@link Options}
     */
    private static final class EmptyOptions<T> extends Options<T>
    {
        /**
         * The empty array of options.
         */
        private static final Object[] EMPTY =
        {
        };


        /**
         * Constructs an {@link EmptyOptions}.
         */
        EmptyOptions()
        {
            super(null);
        }


        @Override
        public <U extends T> U get(Class<U> clzOption)
        {
            return getDefaultFor(clzOption);
        }


        @Override
        public <U extends T> U get(Class<U> clzOption,
                                   U        optDefault)
        {
            return optDefault;
        }


        @Override
        public <O extends T> boolean contains(Class<O> clzOption)
        {
            return false;
        }


        @Override
        public boolean contains(T option)
        {
            return false;
        }


        @Override
        public <O> Iterable<O> getInstancesOf(Class<O> clz)
        {
            return Collections.EMPTY_SET;
        }


        @Override
        public T[] asArray()
        {
            return (T[]) EMPTY;
        }


        @Override
        public String toString()
        {
            return "EmptyOptions{}";
        }
    }


    /**
     * Defines how an {@link Options} collection may automatically determine a
     * suitable default value for a specific class of option at runtime
     * when said option does not exist in an {@link Options} collection.
     * <p>
     * For example, the {@link Default} annotation can be used to specify that
     * a public static no-args method can be used to determine a default value.
     * <pre><code>
     * public class Color {
     *     ...
     *     &#64;Options.Default
     *     public static Color getDefault() {
     *         ...
     *     }
     *     ...
     * }
     * </code></pre>
     *
     * <p>
     * Similarly, the {@link Default} annotation can be used to specify a
     * public static field to use for determining a default value.
     * <pre><code>
     * public class Color {
     *     ...
     *     &#64;Options.Default
     *     public static Color BLUE = ...;
     *     ...
     * }
     * </code></pre>
     * <p>
     * Alternatively, the {@link Default} annotation can be used to specify that
     * the public no-args constructor for a public class may be used for
     * constructing a default value.
     * <pre><code>
     * public class Color {
     *     ...
     *     &#64;Options.Default
     *     public Color() {
     *         ...
     *     }
     *     ...
     * }
     * </code></pre>
     * <p>
     * Lastly, when used with an enum, the {@link Default} annotation
     * can be used to specify the default enum constant.
     * <pre><code>
     * public enum Color {
     *     RED,
     *
     *     GREEN,
     *
     *     &#64;Options.Default
     *     BLUE;   // blue is the default color
     * }
     * </code></pre>
     *
     * @see Options#get(Class)
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
    public @interface Default
    {
    }
}
 