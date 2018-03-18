package net.spinetrak.enpassant;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import net.spinetrak.enpassant.configuration.DSBDataFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TheConfiguration extends Configuration
{

  @Valid
  @NotNull
  @JsonProperty("dsbData")
  private DSBDataFactory dsbData;

  @JsonProperty("dsbData")
  public DSBDataFactory getDSBDataFactory()
  {
    return dsbData;
  }

  @JsonProperty("dsbData")
  public void setDSBDataFactory(final DSBDataFactory factory_)
  {
    dsbData = factory_;
  }
}
