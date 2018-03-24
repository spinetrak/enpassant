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

public class DSBAssociation
{
  public final static int BEZIRK = 2;
  public final static int BUND = 0;
  public final static int KREIS = 3;
  public final static int LAND = 1;
  private final String _associationId;
  private final Map<String, DSBAssociation> _associations = new HashMap<>();
  private final Map<String, DSBClub> _clubs = new HashMap<>();
  private final int _level;
  private final String _name;
  private final String _parentId;
  private DSBClub _asClub;

  public DSBAssociation(@NotNull final String associationId_, final String parentId_, final int level,
                        @NotNull final String name_)
  {
    _associationId = associationId_.trim();
    _parentId = parentId_;
    _level = level;
    _name = name_.trim();
  }

  public void add(@NotNull final DSBClub club_)
  {
    _clubs.put(club_.getClubId(), club_);
  }

  public void add(@NotNull final DSBAssociation dsbAssociation_)
  {
    _associations.put(dsbAssociation_.getAssociationId(), dsbAssociation_);
  }

  public DSBClub asClub()
  {
    if (null != _asClub)
    {
      return _asClub;
    }
    else
    {
      _asClub = new DSBClub(_associationId, _name, _associationId);
    }
    return _asClub;
  }

  public DSBAssociation getAssociation(@NotNull final String id_)
  {
    final DSBAssociation association = _associations.get(id_);
    if (association != null)
    {
      return association;
    }
    for (final DSBAssociation subAssociation : _associations.values())
    {
      final DSBAssociation v = subAssociation.getAssociation(id_);
      if (v != null)
      {
        return v;
      }
    }
    return null;
  }

  public String getAssociationId()
  {
    return _associationId;
  }

  public Map<String, DSBAssociation> getAssociations()
  {
    return _associations;
  }

  public DSBClub getClub(@NotNull final String id_)
  {
    final DSBClub club = _clubs.get(id_);
    if (club != null)
    {
      return club;
    }
    for (final DSBAssociation association : _associations.values())
    {
      final DSBClub v = association.getClub(id_);
      if (v != null)
      {
        return v;
      }
    }
    return null;
  }

  public Map<String, DSBClub> getClubs()
  {
    return _clubs;
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

  @Override
  public String toString()
  {
    return "DSBAssociation{" +
      "associations=" + _associations +
      ", associationId='" + _associationId + '\'' +
      ", level=" + _level +
      ", name='" + _name + '\'' +
      ", parentId='" + _parentId + '\'' +
      ", asClub=" + _asClub +
      '}';
  }
}
