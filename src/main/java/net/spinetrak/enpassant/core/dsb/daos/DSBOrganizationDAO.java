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

import net.spinetrak.enpassant.core.dsb.dtos.DSBStats;
import net.spinetrak.enpassant.core.dsb.mappers.DSBMemberStatsMapper;
import net.spinetrak.enpassant.core.dsb.mappers.DSBOrganizationMapper;
import net.spinetrak.enpassant.core.dsb.mappers.DSBRatingsStatsMapper;
import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


public interface DSBOrganizationDAO
{
  @SqlUpdate("INSERT INTO dsb_organization (o_id, o_name, o_level, o_isclub, o_parentid) VALUES (:o.organizationId, :o.name, :o.level, :o.isClub, :o.parentId) ON CONFLICT (o_id) DO UPDATE SET o_name = :o.name, o_level = :o.level, o_isclub = :o.isClub, o_parentid = :o.parentId")
  void insertOrUpdate(@BindBean("o") final DSBOrganization organization_);

  @SqlQuery("SELECT o_id,o_name,o_level,o_isclub,o_parentid from dsb_organization where o_id = :id")
  @RegisterRowMapper(DSBOrganizationMapper.class)
  List<DSBOrganization> selectById(@Bind("id") String id_);

  @SqlQuery("SELECT o_id,o_name,o_level,o_isclub,o_parentid from dsb_organization where o_parentid = :id")
  @RegisterRowMapper(DSBOrganizationMapper.class)
  List<DSBOrganization> selectChildrenOf(@Bind("id") String id_);

  @SqlQuery("SELECT * from getDWZStatsByAgeForAssociationOrClub (:id)")
  @RegisterRowMapper(DSBRatingsStatsMapper.class)
  List<DSBStats> selectDWZStatsFor(@Bind("id") String orgId_);

  @SqlQuery("SELECT * from getELOStatsByAgeForAssociationOrClub (:id)")
  @RegisterRowMapper(DSBRatingsStatsMapper.class)
  List<DSBStats> selectELOStatsFor(@Bind("id") String orgId_);

  @SqlQuery("SELECT * from getMemberStatsByAgeForAssociationOrClub (:id)")
  @RegisterRowMapper(DSBMemberStatsMapper.class)
  List<DSBStats> selectMemberStatsFor(@Bind("id") String orgId_);

  @SqlQuery("SELECT * from getMembersWithoutDWZByAge (:id)")
  @RegisterRowMapper(DSBMemberStatsMapper.class)
  List<DSBStats> selectMembersWithoutDWZByAge(@Bind("id") String orgId_);

  @SqlQuery("SELECT * from getMembersWithoutELOByAge (:id)")
  @RegisterRowMapper(DSBMemberStatsMapper.class)
  List<DSBStats> selectMembersWithoutELOByAge(@Bind("id") String orgId_);
}
