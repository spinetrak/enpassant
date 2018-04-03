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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSBOrganization
{
  public final static int BEZIRK = 2;
  public final static int BUND = 0;
  public final static int KREIS = 3;
  public final static int LAND = 1;
  public static final int VEREIN = -1;
  private final Map<String, DSBOrganization> _organizations = new HashMap<>();
  private final List<DSBPlayer> _players = new ArrayList<>();
  private boolean _isClub;
  private int _level = -1;
  private String _name;
  private String _organizationId;
  private String _parentId;


  public void add(@NotNull final DSBOrganization dsbOrganization_)
  {
    _organizations.put(dsbOrganization_.getOrganizationId(), dsbOrganization_);
  }

  public void add(@NotNull final DSBPlayer player_)
  {
    _players.add(player_);
  }

  public void add(@NotNull final List<DSBPlayer> players_)
  {
    _players.addAll(players_);
  }

  public boolean getIsClub()
  {
    return _isClub;
  }

  public int getLevel()
  {
    return _level;
  }

  public String getName()
  {
    return _name;
  }

  public DSBOrganization getOrganization(@NotNull final String id_)
  {
    final DSBOrganization organization = _organizations.get(id_);
    if (organization != null)
    {
      return organization;
    }
    for (final DSBOrganization subOrganization : _organizations.values())
    {
      final DSBOrganization v = subOrganization.getOrganization(id_);
      if (v != null)
      {
        return v;
      }
    }
    return null;
  }

  public String getOrganizationId()
  {
    return _organizationId;
  }

  public Map<String, DSBOrganization> getOrganizations()
  {
    return _organizations;
  }

  public String getParentId()
  {
    return _parentId;
  }

  public List<DSBPlayer> getPlayers()
  {
    return _players;
  }

  public void setIsClub(final boolean isClub_)
  {
    _isClub = isClub_;
  }

  public void setLevel(final int level_)
  {
    _level = level_;
  }

  public void setName(final String name_)
  {
    _name = name_;
  }

  public void setOrganizationId(final String organizationId_)
  {
    _organizationId = organizationId_;
  }

  public void setParentId(final String parentId_)
  {
    _parentId = parentId_;
  }

  @Override
  public String toString()
  {
    return "DSBOrganization{" +
      "organizations=" + _organizations +
      ", organizationId='" + _organizationId + '\'' +
      ", level=" + _level +
      ", name='" + _name + '\'' +
      ", parentId='" + _parentId + '\'' +
      ", isClub=" + _isClub +
      '}';
  }
}
