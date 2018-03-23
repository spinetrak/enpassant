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

package net.spinetrak.enpassant.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DSBDataFactory
{
  @NotNull
  private Integer refreshInterval;
  @NotEmpty
  private String url;

  public DSBZipFileProcessor build(@NotNull final Environment environment_)
  {
    final ScheduledExecutorService ses = environment_.lifecycle().scheduledExecutorService(
      "dsbZipFileProcessor").build();
    final DSBZipFileProcessor dsbZipFileProcessor = new DSBZipFileProcessor(getUrl());
    ses.scheduleWithFixedDelay(dsbZipFileProcessor, 10, getRefreshInterval(), TimeUnit.SECONDS);
    return dsbZipFileProcessor;
  }

  @JsonProperty
  public int getRefreshInterval()
  {
    return refreshInterval;
  }

  @JsonProperty
  public void setRefreshInterval(final int refreshInterval_)
  {
    refreshInterval = refreshInterval_;
  }

  @JsonProperty
  public void setUrl(final String url_)
  {
    url = url_;
  }

  @JsonProperty
  private String getUrl()
  {
    return url;
  }
}
