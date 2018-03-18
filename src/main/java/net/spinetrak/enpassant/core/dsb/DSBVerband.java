package net.spinetrak.enpassant.core.dsb;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class DSBVerband
{
  public final static String BEZIRK = "BEZIRK";
  public final static String BUND = "BUND";
  public final static String KREIS = "KREIS";
  public final static String LAND = "LAND";
  private final String _id;
  private final String _level;
  private final String _name;
  private final String _parentId;
  private final Map<String, DSBVerband> _verbaende = new HashMap<>();
  private final Map<String, DSBVerein> _vereine = new HashMap<>();
  private DSBVerein _asVerein;

  public DSBVerband(@NotNull final String id_, @NotNull final String parentId_, @NotNull final String level,
                    @NotNull final String name_)
  {
    _id = id_;
    _parentId = parentId_;
    _name = name_;
    _level = level;
  }

  public void add(@NotNull final DSBVerein verein_)
  {
    _vereine.put(verein_.getId(), verein_);
  }

  public void add(@NotNull final DSBVerband dsbVerband_)
  {
    _verbaende.put(dsbVerband_.getId(), dsbVerband_);
  }

  public DSBVerein asVerein()
  {
    if (null != _asVerein)
    {
      return _asVerein;
    }
    else
    {
      _asVerein = new DSBVerein(_id, _name, this);
    }
    return _asVerein;
  }


  public String getId()
  {
    return _id;
  }

  public String getLevel()
  {
    return _level;
  }

  public String getName()
  {
    return _name;
  }

  public String getParentId()
  {
    return _parentId;
  }


  public Map<String, DSBVerband> getVerbaende()
  {
    return _verbaende;
  }


  public DSBVerband getVerband(@NotNull final String id_)
  {
    final DSBVerband verband = _verbaende.get(id_);
    if (verband != null)
    {
      return verband;
    }
    for (final DSBVerband subverband : _verbaende.values())
    {
      final DSBVerband v = subverband.getVerband(id_);
      if (v != null)
      {
        return v;
      }
    }
    return null;
  }

  public DSBVerein getVerein(@NotNull final String id_)
  {
    final DSBVerein verein = _vereine.get(id_);
    if (verein != null)
    {
      return verein;
    }
    for (final DSBVerband verband : _verbaende.values())
    {
      final DSBVerein v = verband.getVerein(id_);
      if (v != null)
      {
        return v;
      }
    }
    return null;
  }

  public Map<String, DSBVerein> getVereine()
  {
    return _vereine;
  }

  @Override
  public String toString()
  {
    return "DSBVerband{" +
      "id='" + _id + '\'' +
      ", level='" + _level + '\'' +
      ", name='" + _name + '\'' +

      ", parentId='" + _parentId + '\'' +
      ", verbaende=" + _verbaende +
      ", vereine=" + _vereine +
      '}';
  }
}
