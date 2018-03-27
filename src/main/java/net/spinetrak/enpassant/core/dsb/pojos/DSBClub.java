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
import java.util.List;

public class DSBClub
{
  private final List<DSBPlayer> _players = new ArrayList<>();
  private String _associationId;
  private String _clubId;
  private String _name;

  public void add(@NotNull final DSBPlayer player_)
  {
    _players.add(player_);
  }

  public void add(@NotNull final List<DSBPlayer> players_)
  {
    _players.addAll(players_);
  }

  public String getAssociationId()
  {
    return _associationId;
  }

  public String getClubId()
  {
    return _clubId;
  }

  public String getName()
  {
    return _name;
  }

  public List<DSBPlayer> getPlayers()
  {
    return _players;
  }

  public void setAssociationId(final String associationId_)
  {
    _associationId = associationId_;
  }

  public void setClubId(final String clubId_)
  {
    _clubId = clubId_;
  }

  public void setName(final String name_)
  {
    _name = name_;
  }

  @Override
  public String toString()
  {
    return "DSBClub{" +
      "associationId='" + _associationId + '\'' +
      ", clubId='" + _clubId + '\'' +
      ", name='" + _name + '\'' +
      '}';
  }
}
