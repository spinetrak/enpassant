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

package net.spinetrak.enpassant.core.dsb.daos;

import net.spinetrak.enpassant.core.dsb.pojos.DSBSpieler;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DSBSpielerDAO
{
  @SqlUpdate("INSERT INTO dwz (zps, member, lasteval, dwz, index) VALUES (:s.verein, :s.id, :s.dwz.lastEvaluation, :s.dwz.dwz, :s.dwz.index) ON CONFLICT (zps, member, lasteval) DO NOTHING")
  void insertOrUpdateDWZ(@BindBean("s") final DSBSpieler spieler_);

  @SqlUpdate("INSERT INTO fide (id, elo, title, country, lasteval) VALUES (:s.fide.id, :s.fide.elo, :s.fide.title, :s.fide.country, :s.dwz.lastEvaluation) ON CONFLICT (id, lasteval) DO NOTHING")
  void insertOrUpdateFIDE(@BindBean("s") final DSBSpieler spieler_);

  @SqlUpdate("INSERT INTO dsb_player (zps, member, dsbid, name, status, gender, yob, eligibility) VALUES (:s.verein, :s.id, " + (-1) + ", :s.name, :s.status, :s.gender, :s.yob, :s.eligibility) ON CONFLICT (zps,member) DO UPDATE SET dsbid = " + (-1) + ", name = :s.name, status = :s.status, gender = :s.gender, yob = :s.yob")
  void insertOrUpdateSpieler(@BindBean("s") final DSBSpieler spieler_);

  @SqlQuery("SELECT * FROM dsb_player")
  @RegisterRowMapper(DSBSpielerMapper.class)
  List<DSBSpieler> selectPlayers();

  @SqlQuery("SELECT * FROM dsb_player where zps = :id")
  @RegisterRowMapper(DSBSpielerMapper.class)
  List<DSBSpieler> selectPlayers(@Bind("id") final String vereinId_);
}
