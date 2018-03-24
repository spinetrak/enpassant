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

import net.spinetrak.enpassant.core.dsb.pojos.DSBClub;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DSBClubDAO
{
  @SqlUpdate("INSERT INTO dsb_organization (id, name, level, isclub, parentId) VALUES (:c.clubId, :c.name, " + (-1) + ", " + true + ", :c.associationId) ON CONFLICT (id) DO UPDATE SET name = :c.name, level = " + (-1) + ", isClub = " + true + ", parentId = :c.associationId")
  void insertOrUpdate(@BindBean("c") final DSBClub club_);

  @SqlQuery("SELECT * FROM dsb_organization WHERE isClub=true")
  @RegisterRowMapper(DSBClubMapper.class)
  List<DSBClub> select();

  @SqlQuery("SELECT * from dsb_organization where isClub=true and id = :id")
  @RegisterRowMapper(DSBClubMapper.class)
  List<DSBClub> select(@Bind("id") String id_);

  @SqlQuery("SELECT * from dsb_organization where isClub=true and parentId = :id")
  @RegisterRowMapper(DSBClubMapper.class)
  List<DSBClub> selectChildrenOf(@Bind("id") String id_);
}
