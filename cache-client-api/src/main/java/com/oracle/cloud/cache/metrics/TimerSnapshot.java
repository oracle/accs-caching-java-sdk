/*
 * File: TimerSnapshot.java
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

package com.oracle.cloud.cache.metrics;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

/**
 * Encapsulates duration and throughput statistics for a cache operation.
 *
 * @author Aleksandar Seovic  2016.07.18
 */
public class TimerSnapshot
{
    /**
     * The total number of invocations for a timed operation.
     */
    private final long count;

    /**
     * The mean rate at which events have occurred since the meter was created.
     */
    private final double meanRate;

    /**
     * The one-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     */
    private final double oneMinuteRate;

    /**
     * The five-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     */
    private final double fiveMinuteRate;

    /**
     * The fifteen-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     */
    private final double fifteenMinuteRate;

    /**
     * A statistical snapshot.
     */
    private final Snapshot snapshot;


    /**
     * Constructs a TimerSnapshot instance.
     *
     * @param timer the Timer to capture statistics from
     */
    TimerSnapshot(Timer timer)
    {
        count             = timer.getCount();
        meanRate          = timer.getMeanRate();
        oneMinuteRate     = timer.getOneMinuteRate();
        fiveMinuteRate    = timer.getFiveMinuteRate();
        fifteenMinuteRate = timer.getFifteenMinuteRate();
        snapshot          = timer.getSnapshot();
    }


    /**
     * Returns the total number of invocations for a timed operation.
     *
     * @return the total number of invocations for a timed operation
     */
    public long getCount()
    {
        return count;
    }


    /**
     * Returns the mean rate at which events have occurred since the meter was created.
     *
     * @return the mean rate at which events have occurred since the meter was created
     */
    public double getMeanRate()
    {
        return meanRate;
    }


    /**
     * Returns the one-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p>
     * This rate has the same exponential decay factor as the one-minute load average in the {@code
     * top} Unix command.
     * </p>
     * @return the one-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    public double getOneMinuteRate()
    {
        return oneMinuteRate;
    }


    /**
     * Returns the five-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p>
     * This rate has the same exponential decay factor as the five-minute load average in the {@code
     * top} Unix command.
     * <p>
     * @return the five-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    public double getFiveMinuteRate()
    {
        return fiveMinuteRate;
    }


    /**
     * Returns the fifteen-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created.
     * <p>
     * This rate has the same exponential decay factor as the fifteen-minute load average in the
     * {@code top} Unix command.
     *
     * @return the fifteen-minute exponentially-weighted moving average rate at which events have
     * occurred since the meter was created
     */
    public double getFifteenMinuteRate()
    {
        return fifteenMinuteRate;
    }


    /**
     * Returns the value at the given quantile.
     *
     * @param quantile a given quantile, in {@code [0..1]}
     * @return the value in the distribution at {@code quantile}
     */
    public double getPercentile(double quantile)
    {
        return snapshot.getValue(quantile);
    }


    /**
     * Returns the median (50th percentile) value in the distribution.
     *
     * @return the median (50th percentile) value
     */
    public double get50thPercentile()
    {
        return snapshot.getMedian();
    }


    /**
     * Returns the value at the 75th percentile in the distribution.
     *
     * @return the value at the 75th percentile
     */
    public double get75thPercentile()
    {
        return snapshot.get75thPercentile();
    }


    /**
     * Returns the value at the 95th percentile in the distribution.
     *
     * @return the value at the 95th percentile
     */
    public double get95thPercentile()
    {
        return snapshot.get95thPercentile();
    }


    /**
     * Returns the value at the 98th percentile in the distribution.
     *
     * @return the value at the 98th percentile
     */
    public double get98thPercentile()
    {
        return snapshot.get98thPercentile();
    }


    /**
     * Returns the value at the 99th percentile in the distribution.
     *
     * @return the value at the 99th percentile
     */
    public double get99thPercentile()
    {
        return snapshot.get99thPercentile();
    }


    /**
     * Returns the value at the 99.9th percentile in the distribution.
     *
     * @return the value at the 99.9th percentile
     */
    public double get999thPercentile()
    {
        return snapshot.get999thPercentile();
    }


    /**
     * Returns the highest value in the snapshot.
     *
     * @return the highest value
     */
    public long getMax()
    {
        return snapshot.getMax();
    }


    /**
     * Returns the average of the values in the snapshot.
     *
     * @return the average value
     */
    public double getAverage()
    {
        return snapshot.getMean();
    }


    /**
     * Returns the lowest value in the snapshot.
     *
     * @return the lowest value
     */
    public long getMin()
    {
        return snapshot.getMin();
    }


    /**
     * Returns the standard deviation of the values in the snapshot.
     *
     * @return the standard value
     */
    public double getStdDev()
    {
        return snapshot.getStdDev();
    }


    @Override
    public String toString()
    {
        return "[" + "count = " + count + ", mean rate = " + meanRate + ", 1-min rate = " + oneMinuteRate
               + ", 5-min rate = " + fiveMinuteRate + ", 15-min rate = " + fifteenMinuteRate + ", 50% = "
               + get50thPercentile() + ", 75% = " + get75thPercentile() + ", 95% = " + get95thPercentile() + ", 98% = "
               + get98thPercentile() + ", 99% = " + get99thPercentile() + ", 99.9% = " + get999thPercentile()
               + ", max = " + getMax() + ", avg = " + getAverage() + ", min = " + getMin() + ", stddev = "
               + getStdDev() + ']';
    }
}
