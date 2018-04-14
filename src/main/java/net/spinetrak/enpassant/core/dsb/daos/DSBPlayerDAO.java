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

import net.spinetrak.enpassant.core.dsb.dtos.DSBPlayerStats;
import net.spinetrak.enpassant.core.dsb.mappers.DSBPlayerMapper;
import net.spinetrak.enpassant.core.dsb.mappers.DSBPlayerStatsMapper;
import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

import java.util.List;

@RegisterBeanMapper(value = DSBPlayer.class, prefix = "p")
@RegisterBeanMapper(value = DWZ.class, prefix = "d")
@RegisterBeanMapper(value = FIDE.class, prefix = "f")
public interface DSBPlayerDAO
{
  @SqlUpdate("INSERT INTO dwz (d_clubid, d_memberid, d_lasteval, d_dwz, d_index) VALUES (:d.clubId, :d.memberId, :d.lastEvaluation, :d.dwz, :d.index) ON CONFLICT (d_clubid, d_memberid, d_lasteval) DO NOTHING")
  void insertOrUpdateDWZ(@BindBean("d") final DWZ dwz_);

  @SqlUpdate("INSERT INTO fide (f_id, f_elo, f_title, f_country, f_lasteval) VALUES (:f.id, :f.elo, :f.title, :f.country, :f.lastEvaluation) ON CONFLICT (f_id, f_lasteval) DO NOTHING")
  void insertOrUpdateFIDE(@BindBean("f") final FIDE fide_);

  @SqlUpdate("INSERT INTO dsb_player (p_clubid, p_memberid, p_dsbid, p_fideid, p_name, p_status, p_gender, p_yob, p_eligibility) VALUES (:p.clubId, :p.memberId, :p.dsbId, :p.fideId, :p.name, :p.status, :p.gender, :p.yoB, :p.eligibility) ON CONFLICT (p_clubid, p_memberid) DO UPDATE SET p_dsbid = :p.dsbId, p_fideid = :p.fideId, p_name = :p.name, p_status = :p.status, p_gender = :p.gender, p_yob = :p.yoB")
  void insertOrUpdatePlayer(@BindBean("p") final DSBPlayer player_);

  @SqlQuery("SELECT * FROM dsb_player where p_clubid = :clubId")
  List<DSBPlayer> selectByClubId(@Bind("clubId") final String clubId_);

  @SqlQuery("SELECT * FROM dsb_player where p_clubid = :clubId and p_memberid = :memberId")
  List<DSBPlayer> selectByClubIdAndMemberId(@Bind("clubId") final String clubId_,
                                            @Bind("memberId") final String memberId_);

  @SqlQuery("SELECT * FROM dsb_player where p_dsbid = :dsbId")
  List<DSBPlayer> selectByDSBId(@Bind("dsbId") final int dsbId_);

  @SqlQuery("SELECT * FROM dwz where d_clubid = :p.clubId and d_memberid = :p.memberId")
  List<DWZ> selectDWZByPlayer(@BindBean("p") final DSBPlayer player_);

  @SqlQuery("SELECT * FROM fide where f_id = :p.fideId")
  List<FIDE> selectFIDEByPlayer(@BindBean("p") final DSBPlayer player_);

  @SqlQuery("SELECT d.d_dwz, d.d_lasteval, f.f_elo, f.f_lasteval FROM dsb_player p LEFT OUTER JOIN dwz d ON p.p_clubid=d.d_clubid AND p.p_memberid=d.d_memberid LEFT OUTER JOIN fide f ON p.p_fideid = f.f_id WHERE p.p_clubid = :clubId AND p.p_memberid = :memberId")
  @RegisterRowMapper(DSBPlayerStatsMapper.class)
  List<DSBPlayerStats> selectPlayerStatsFor(@Bind("clubId") final String clubId_,
                                            @Bind("memberId") final String memberId_);


  @SqlQuery("SELECT * FROM getPlayersByOrganization (:id)")
  @UseRowReducer(DSBPlayerMapper.class)
  List<DSBPlayer> selectPlayersFor(@Bind("id") String id_);

}
