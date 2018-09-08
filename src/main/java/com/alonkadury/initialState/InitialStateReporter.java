/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 spinetrak
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.alonkadury.initialState;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;


public class InitialStateReporter implements Runnable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(InitialStateReporter.class);
  private final API _account;
  private final Bucket _bucket;
  private final MetricRegistry _metrics;

  public InitialStateReporter(final String initialStateAPIKey_, final MetricRegistry metrics_)
  {
    _account = new API(initialStateAPIKey_);
    _bucket = new Bucket("enpassant", "enpassant");
    _account.createBucket(_bucket);
    _metrics = metrics_;
  }

  @Override
  public void run()
  {
    update();
  }

  private Data[] collectGauges()
  {
    final List<Data> data = new ArrayList<>();

    final SortedMap<String, Gauge> gauges = _metrics.getGauges();
    for (final Map.Entry<String, Gauge> entries : gauges.entrySet())
    {
      final String key = entries.getKey();
      final Gauge gauge = entries.getValue();
      final Object value = gauge.getValue();
      if (value instanceof Number)
      {
        if (value instanceof Double && Double.isNaN((double) value))
        {
          continue;
        }
        data.add(new Data<>(key, value));
      }
    }
    return data.toArray(new Data[data.size()]);
  }

  private Data[] collectMeters()
  {
    final List<Data> data = new ArrayList<>();

    final SortedMap<String, Meter> meters = _metrics.getMeters();
    for (final Map.Entry<String, Meter> entries : meters.entrySet())
    {
      final String key = entries.getKey();
      final Meter meter = entries.getValue();
      data.add(new Data<>(key + "_count", meter.getCount()));
      data.add(new Data<>(key + "_1m", meter.getOneMinuteRate()));
      data.add(new Data<>(key + "_5m", meter.getFiveMinuteRate()));
      data.add(new Data<>(key + "_15m", meter.getFifteenMinuteRate()));
      data.add(new Data<>(key + "_mean", meter.getMeanRate()));
    }
    return data.toArray(new Data[data.size()]);
  }

  private Data[] collectTimers()
  {
    final List<Data> data = new ArrayList<>();

    final SortedMap<String, Timer> timers = _metrics.getTimers();
    for (final Map.Entry<String, Timer> entries : timers.entrySet())
    {
      final String key = entries.getKey();
      final Timer timer = entries.getValue();
      data.add(new Data<>(key + "_count", timer.getCount()));
      data.add(new Data<>(key + "_1m", timer.getOneMinuteRate()));
      data.add(new Data<>(key + "_5m", timer.getFiveMinuteRate()));
      data.add(new Data<>(key + "_15m", timer.getFifteenMinuteRate()));
      data.add(new Data<>(key + "_mean", timer.getMeanRate()));

      final Snapshot snapshot = timer.getSnapshot();
      data.add(new Data<>(key + "_95pct", snapshot.get95thPercentile()));
      data.add(new Data<>(key + "_max", snapshot.getMax()));
      data.add(new Data<>(key + "_min", snapshot.getMax()));
    }
    return data.toArray(new Data[data.size()]);
  }

  private void update()
  {
    try
    {
      _account.createBulkData(_bucket, collectGauges());
      synchronized (_account)
      {
        _account.wait(500);
      }
      _account.createBulkData(_bucket, collectMeters());
      synchronized (_account)
      {
        _account.wait(500);
      }
      _account.createBulkData(_bucket, collectTimers());
    }
    catch (final Exception ex_)
    {
      LOGGER.error("Exception: ", ex_);
    }
  }
}
