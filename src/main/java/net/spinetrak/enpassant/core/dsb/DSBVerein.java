package net.spinetrak.enpassant.core.dsb;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DSBVerein
{
  private final String _id;
  private final String _name;
  private final List<DSBSpieler> _spieler = new ArrayList<>();
  private final DSBVerband _verband;

  public DSBVerein(@NotNull final String id_, @NotNull final String name_, @NotNull final DSBVerband verband_)
  {
    _id = id_;
    _name = name_;
    _verband = verband_;
  }

  public void add(@NotNull final DSBSpieler spieler_)
  {
    _spieler.add(spieler_);
  }

  public String getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public List<DSBSpieler> getSpieler()
  {
    return _spieler;
  }

  @Override
  public String toString()
  {
    return "DSBVerein{" +

      "id='" + _id + '\'' +
      ", name='" + _name + '\'' +
      '}';
  }
}
