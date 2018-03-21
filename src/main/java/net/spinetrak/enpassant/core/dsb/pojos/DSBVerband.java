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

package net.spinetrak.enpassant.core.dsb.pojos;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class DSBVerband
{
  public final static int BEZIRK = 2;
  public final static int BUND = 0;
  public final static int KREIS = 3;
  public final static int LAND = 1;
  private final String _id;
  private final int _level;
  private final String _name;
  private final String _parentId;
  private final Map<String, DSBVerband> _verbaende = new HashMap<>();
  private final Map<String, DSBVerein> _vereine = new HashMap<>();
  private DSBVerein _asVerein;

  public DSBVerband(@NotNull final String id_, final String parentId_, final int level,
                    @NotNull final String name_)
  {
    _id = id_.trim();
    _parentId = parentId_;
    _level = level;
    _name = name_.trim();
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

  public int getLevel()
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
