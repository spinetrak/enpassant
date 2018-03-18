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

package net.spinetrak.enpassant;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import net.spinetrak.enpassant.configuration.DSBDataFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class TheConfiguration extends Configuration
{

  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();
  @Valid
  @NotNull
  @JsonProperty("dsbData")
  private DSBDataFactory dsbData;

  @JsonProperty("dsbData")
  DSBDataFactory getDSBDataFactory()
  {
    return dsbData;
  }

  @JsonProperty("database")
  DataSourceFactory getDataSourceFactory()
  {
    return database;
  }

  @JsonProperty("dsbData")
  public void setDSBDataFactory(final DSBDataFactory factory_)
  {
    dsbData = factory_;
  }

  @JsonProperty("database")
  public void setDataSourceFactory(final DataSourceFactory factory_)
  {
    database = factory_;
  }
}
