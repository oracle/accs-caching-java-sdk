/*
 * File: Order.java
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
 * Order class for tests.
 *
 * @author Tim Middleton  2016.10.24
 */
public class Order
{
    /**
     * The order id.
     */
    private int orderId;

    /**
     * The date of the order.
     */
    private long orderDate;

    /**
     * The value of the order.
     */
    private float orderValue;

    /**
     * The {@link Person} placing the order.
     */
    private Person orderPerson;


    /**
     * No-args constructor for JsonSerializer.
     */
    public Order()
    {
    }


    /**
     * Constructs and Order with the supplied values.
     *
     * @param orderId       the order id
     * @param orderDate     the date of the order
     * @param orderValue    the value of the order
     * @param orderPerson   the {@link Person} placing the order
     */
    public Order(int    orderId,
                 long   orderDate,
                 float  orderValue,
                 Person orderPerson)
    {
        this.orderId     = orderId;
        this.orderDate   = orderDate;
        this.orderValue  = orderValue;
        this.orderPerson = orderPerson;
    }


    /**
     * Returns the order id.
     *
     * @return the order id
     */
    public int getOrderId()
    {
        return orderId;
    }


    /**
     * Sets the order id.
     *
     * @param orderId the order id
     */
    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }


    /**
     * Returns the date of the order.
     *
     * @return  the date of the order
     */
    public long getOrderDate()
    {
        return orderDate;
    }


    /**
     * Sets the the date of the order.
     *
     * @param orderDate  the date of the order
     */
    public void setOrderDate(long orderDate)
    {
        this.orderDate = orderDate;
    }


    /**
     * Returns the value of the order.
     *
     * @return the value of the order
     */
    public float getOrderValue()
    {
        return orderValue;
    }


    /**
     * Sets the value of the order.
     *
     * @param orderValue  he value of the order
     */
    public void setOrderValue(float orderValue)
    {
        this.orderValue = orderValue;
    }


    /**
     * Sets the value of the order.
     *
     * @return
     */
    public Person getOrderPerson()
    {
        return orderPerson;
    }


    /**
     * Returns the {@link Person} placing the order.
     *
     * @param orderPerson  the {@link Person} placing the order
     */
    public void setOrderPerson(Person orderPerson)
    {
        this.orderPerson = orderPerson;
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

        Order order = (Order) o;

        if (orderId != order.orderId)
        {
            return false;
        }

        if (orderDate != order.orderDate)
        {
            return false;
        }

        if (Float.compare(order.orderValue, orderValue) != 0)
        {
            return false;
        }

        return orderPerson != null ? orderPerson.equals(order.orderPerson) : order.orderPerson == null;

    }


    @Override
    public int hashCode()
    {
        int result = orderId;

        result = 31 * result + (int) (orderDate ^ (orderDate >>> 32));
        result = 31 * result + (orderValue != +0.0f ? Float.floatToIntBits(orderValue) : 0);
        result = 31 * result + (orderPerson != null ? orderPerson.hashCode() : 0);

        return result;
    }
}
