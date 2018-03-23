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

import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DSBPlayerDAO
{
  @SqlUpdate("INSERT INTO dwz (zps, member, lasteval, dwz, index) VALUES (:d.club, :d.id, :d.lastEvaluation, :d.dwz, :d.index) ON CONFLICT (zps, member, lasteval) DO NOTHING")
  void insertOrUpdateDWZ(@BindBean("d") final DWZ dwz_);

  @SqlUpdate("INSERT INTO fide (id, elo, title, country, lasteval) VALUES (:f.id, :f.elo, :f.title, :f.country, :f.lastEvaluation) ON CONFLICT (id, lasteval) DO NOTHING")
  void insertOrUpdateFIDE(@BindBean("f") final FIDE fide_);

  @SqlUpdate("INSERT INTO dsb_player (zps, member, dsbid, fideid, name, status, gender, yob, eligibility) VALUES (:p.club, :p.id, " + (-1) + ", :p.fideId, :p.name, :p.status, :p.gender, :p.yob, :p.eligibility) ON CONFLICT (zps,member) DO UPDATE SET dsbid = " + (-1) + ", fideid = :p.fideId, name = :p.name, status = :p.status, gender = :p.gender, yob = :p.yob")
  void insertOrUpdatePlayer(@BindBean("p") final DSBPlayer player_);

  @SqlQuery("SELECT * FROM dsb_player")
  @RegisterRowMapper(DSBPlayerMapper.class)
  List<DSBPlayer> select();

  @SqlQuery("SELECT * FROM dsb_player where zps = :club and member = :id")
  @RegisterRowMapper(DSBPlayerMapper.class)
  List<DSBPlayer> select(@Bind("club") final String clubId_, @Bind("id") final String memberId_);

  @SqlQuery("SELECT * FROM dsb_player where zps = :zps")
  @RegisterRowMapper(DSBPlayerMapper.class)
  List<DSBPlayer> select(@Bind("zps") final String clubId_);

  @SqlQuery("SELECT * FROM dwz where zps = :p.club and member = :p.id")
  @RegisterRowMapper(DWZMapper.class)
  List<DWZ> selectDWZ(@BindBean("p") final DSBPlayer player_);

  @SqlQuery("SELECT * FROM fide where id = :p.fideId")
  @RegisterRowMapper(FIDEMapper.class)
  List<FIDE> selectFIDE(@BindBean("p") final DSBPlayer player_);
}
