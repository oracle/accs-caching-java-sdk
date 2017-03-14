/*
 * File: Person.java
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

package functional.model;

/**
 * Person class for object tests.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class Person
{
    /**
     * The name of the person.
     */
    private String name;


    /**
     * No-args constructor for JSON.
     */
    public Person()
    {
    }


    /**
     * Creates a new person with a given name.
     *
     * @param sName name of the person
     */
    public Person(String sName)
    {
        this.name = sName;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Person person = (Person) o;

        return name != null ? name.equals(person.name) : person.name == null;
    }


    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }


    /**
     * Returns the name of a person.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }
}
