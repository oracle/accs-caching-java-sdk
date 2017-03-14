package functional;

import java.io.IOException;

import com.oracle.cloud.cache.basic.io.JsonSerializer;
import com.oracle.cloud.cache.basic.io.Serializer;
import functional.model.Order;
import functional.model.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link JsonSerializer}.
 *
 * @author Tim Middleton  2016.10.24
 */
public class JsonSerializerTest
{
    /**
     * Serializer to be used for tests.
     */
    private Serializer serializer = new JsonSerializer();


    /**
     * Tests the {@link JsonSerializer}.
     *
     * @throws IOException if any I/O related issues.
     */
    @Test
    public void testSerializer() throws IOException
    {
        // test POJO
        Person person    = new Person("Tim");
        byte[] bytes     = serializer.serialize(person);
        Person newPerson = serializer.deserialize(bytes, Person.class);

        assertEquals(newPerson, person);
        assertEquals("{\"name\":\"Tim\"}", new String(bytes));

        // test String
        bytes = serializer.serialize("a string");

        String value = serializer.deserialize(bytes, String.class);

        assertEquals("a string", value);

        // test Integer
        bytes = serializer.serialize(new Integer(10));

        Integer integerResult = serializer.deserialize(bytes, Integer.class);

        // test an object graph
        Order order = new Order(1, System.currentTimeMillis(), 100.0f, person);

        bytes = serializer.serialize(order);

        Order newOrder = serializer.deserialize(bytes, Order.class);

        assertEquals(newOrder, order);
    }
}

