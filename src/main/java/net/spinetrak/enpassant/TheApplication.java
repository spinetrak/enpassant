package net.spinetrak.enpassant;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.spinetrak.enpassant.configuration.DSBDataClient;
import net.spinetrak.enpassant.health.DSBDataHealthCheck;
import net.spinetrak.enpassant.resources.DSBDataResource;

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
    bootstrap_.addBundle(new AssetsBundle("/assets/", "/"));
  }

  @Override
  public void run(final TheConfiguration configuration_,
                  final Environment environment_)
  {
    final DSBDataClient dsbDataClient = configuration_.getDSBDataFactory().build(environment_);
    environment_.jersey().register(new DSBDataResource(dsbDataClient.getDSBVerband()));
    environment_.healthChecks().register("dsbData", new DSBDataHealthCheck(dsbDataClient));
  }

}
