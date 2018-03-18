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
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.spinetrak.enpassant.configuration.DSBDataClient;
import net.spinetrak.enpassant.core.dsb.daos.DSBSpielerDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVerbandDAO;
import net.spinetrak.enpassant.core.dsb.daos.DSBVereinDAO;
import net.spinetrak.enpassant.core.dsb.etl.DSBDataTransformer;
import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import net.spinetrak.enpassant.health.DSBDataHealthCheck;
import net.spinetrak.enpassant.resources.DSBDataResource;
import org.jdbi.v3.core.Jdbi;

public class TheApplication extends Application<TheConfiguration>
{

  public static void main(final String[] args_) throws Exception
  {
    new TheApplication().run(args_);
  }

  @Override
  public String getName()
  {
    return "enpassant";
  }

  @Override
  public void initialize(final Bootstrap<TheConfiguration> bootstrap_)
  {
    bootstrap_.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(bootstrap_.getConfigurationSourceProvider(),
                                     new EnvironmentVariableSubstitutor(false)
      )
    );

    bootstrap_.addBundle(new AssetsBundle("/assets/", "/"));
  }

  @Override
  public void run(final TheConfiguration configuration_,
                  final Environment environment_)
  {
    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment_, configuration_.getDataSourceFactory(), "postgresql");

    final DSBVerbandDAO dsbVerbandDAO = jdbi.onDemand(DSBVerbandDAO.class);
    final DSBVereinDAO dsbVereinDAO = jdbi.onDemand(DSBVereinDAO.class);
    final DSBSpielerDAO dsbSpielerDAO = jdbi.onDemand(DSBSpielerDAO.class);


    final DSBDataClient dsbDataClient = configuration_.getDSBDataFactory().build(environment_);
    final DSBVerband dsbVerband = dsbDataClient.getDSBVerband();
    DSBDataTransformer.updateDatabase(dsbVerbandDAO, dsbVereinDAO, dsbSpielerDAO, dsbVerband);

    environment_.jersey().register(new DSBDataResource(dsbVerband, jdbi));
    environment_.healthChecks().register("dsbData", new DSBDataHealthCheck(dsbDataClient));
  }

}
