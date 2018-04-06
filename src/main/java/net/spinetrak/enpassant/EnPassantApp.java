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

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.spinetrak.enpassant.configuration.DSBZipFileProcessor;
import net.spinetrak.enpassant.core.dsb.daos.DSBOrganizationDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBPlayerDAO;
import net.spinetrak.enpassant.db.DSBDataCache;
import net.spinetrak.enpassant.db.DSBDataUpdater;
import net.spinetrak.enpassant.health.DSBDataHealthCheck;
import net.spinetrak.enpassant.resources.DSBDataResource;
import net.spinetrak.enpassant.resources.GitDataResource;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnPassantApp extends Application<EnPassantConfig>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(EnPassantApp.class);
  public static void main(final String[] args_) throws Exception
  {
    new EnPassantApp().run(args_);
  }

  @Override
  public String getName()
  {
    return "enpassant";
  }

  @Override
  public void initialize(final Bootstrap<EnPassantConfig> bootstrap_)
  {
    bootstrap_.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(bootstrap_.getConfigurationSourceProvider(),
                                     new EnvironmentVariableSubstitutor(false)
      )
    );
    bootstrap_.addBundle(new AssetsBundle("/assets/", "/"));
    bootstrap_.addBundle(new FlywayBundle<EnPassantConfig>()
    {
      @Override
      public DataSourceFactory getDataSourceFactory(final EnPassantConfig configuration_)
      {
        return configuration_.getDataSourceFactory();
      }

      @Override
      public FlywayFactory getFlywayFactory(final EnPassantConfig configuration_)
      {
        return configuration_.getFlywayFactory();
      }
    });
  }

  @Override
  public void run(final EnPassantConfig configuration_,
                  final Environment environment_)
  {
    final GitDataResource gitDataResource = new GitDataResource();
    environment_.jersey().register(gitDataResource);

    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment_, configuration_.getDataSourceFactory(), "postgresql");

    final Flyway flyway = configuration_.getFlywayFactory().build(
      configuration_.getDataSourceFactory().build(environment_.metrics(), "flyway"));
    flyway.repair();
    flyway.migrate();
    flyway.validate();

    final DSBZipFileProcessor dsbZipFileProcessor = configuration_.getDSBDataFactory().build(environment_);
    environment_.healthChecks().register("dsbZipFileProcessor", new DSBDataHealthCheck(dsbZipFileProcessor));

    final DSBOrganizationDAO dsbOrganizationDAO = jdbi.onDemand(DSBOrganizationDAO.class);
    final DSBPlayerDAO dsbPlayerDAO = jdbi.onDemand(DSBPlayerDAO.class);

    final DSBDataCache dsbDataCache = new DSBDataCache(dsbOrganizationDAO, dsbPlayerDAO);
    final DSBDataResource dsbDataResource = new DSBDataResource(dsbDataCache);
    environment_.jersey().register(dsbDataResource);

    final ScheduledExecutorService ses = environment_.lifecycle().scheduledExecutorService("dsbDataUpdater").build();
    final DSBDataUpdater dsbDataUpdater = new DSBDataUpdater(dsbOrganizationDAO, dsbPlayerDAO,
                                                             dsbZipFileProcessor);
    ses.scheduleWithFixedDelay(dsbDataUpdater, 60, configuration_.getDSBDataFactory().getRefreshInterval(),
                               TimeUnit.SECONDS);
    environment_.jersey().register(new JsonProcessingExceptionMapper(true));
  }
}
