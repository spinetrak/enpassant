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
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.flyway.FlywayFactory;
import net.spinetrak.enpassant.configuration.DSBDataFactory;
import net.spinetrak.enpassant.core.utils.Converters;
import net.spinetrak.enpassant.db.EnPassantDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class EnPassantConfig extends Configuration
{
  private final static Logger LOGGER = LoggerFactory.getLogger(EnPassantConfig.class);
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();
  @Valid
  @NotNull
  @JsonProperty("dsbData")
  private DSBDataFactory dsbData;
  @Valid
  @NotNull
  private FlywayFactory flyway = new FlywayFactory();

  @JsonProperty("dsbData")
  DSBDataFactory getDSBDataFactory()
  {
    return dsbData;
  }

  @JsonProperty("database")
  DataSourceFactory getDataSourceFactory()
  {
    if (Converters.noNullsorEmpties(System.getenv("JDBC_DATABASE_URL")))
    {
      LOGGER.info("Using JDBC_DATABASE_URL from environment...");
      DatabaseConfiguration databaseConfiguration = EnPassantDBConfig.create(
        System.getenv("JDBC_DATABASE_URL"));
      database = (DataSourceFactory) databaseConfiguration.getDataSourceFactory(null);
    }
    return database;
  }

  FlywayFactory getFlywayFactory()
  {
    return flyway;
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
