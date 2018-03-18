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

package net.spinetrak.enpassant.db;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class is basically a hack to use the Heroku DATABASE_URL instead of the database configuration in the
 * Dropwizard example.yml.
 * <p>
 * TODO Make this not so ugly
 * See https://github.com/alexroussos/dropwizard-heroku-example/blob/master/src/main/java/com/example/helloworld/ExampleDatabaseConfiguration.java
 */
public class EnPassantDBConfig implements DatabaseConfiguration
{
  private final static Logger LOGGER = LoggerFactory.getLogger(EnPassantDBConfig.class);
  private static DatabaseConfiguration _databaseConfiguration;

  public static DatabaseConfiguration create(String databaseUrl)
  {
    LOGGER.info("Creating DB for " + databaseUrl);
    if (databaseUrl == null)
    {
      throw new IllegalArgumentException("The DATABASE_URL environment variable must be set before running the app " +
                                           "example: DATABASE_URL=\"postgres://username:password@host:5432/dbname\"");
    }
    _databaseConfiguration = null;
    try
    {
      URI dbUri = new URI(databaseUrl);
      final String user = dbUri.getUserInfo().split(":")[0];
      final String password = dbUri.getUserInfo().split(":")[1];
      final String url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
        + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
      _databaseConfiguration = new DatabaseConfiguration()
      {
        DataSourceFactory dataSourceFactory;

        @Override
        public DataSourceFactory getDataSourceFactory(Configuration configuration)
        {
          if (dataSourceFactory != null)
          {
            return dataSourceFactory;
          }
          DataSourceFactory dsf = new DataSourceFactory();
          dsf.setUser(user);
          dsf.setPassword(password);
          dsf.setUrl(url);
          dsf.setDriverClass("org.postgresql.Driver");
          dataSourceFactory = dsf;
          return dsf;
        }
      };
    }
    catch (URISyntaxException e)
    {
      LOGGER.info(e.getMessage());
    }
    return _databaseConfiguration;
  }

  @Override
  public DataSourceFactory getDataSourceFactory(Configuration configuration)
  {
    LOGGER.info("Getting DataSourceFactory");
    if (_databaseConfiguration == null)
    {
      throw new IllegalStateException("You must first call DatabaseConfiguration.create(dbUrl)");
    }
    return (DataSourceFactory) _databaseConfiguration.getDataSourceFactory(null);
  }
}
