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

import net.spinetrak.enpassant.core.dsb.pojos.DSBVerband;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DSBVerbandDAO
{

  @SqlUpdate("INSERT INTO dsb_organization (zps, name, level, isclub, parent) VALUES (:v.id, :v.name, :v.level, " + false + ", :v.parentId) ON CONFLICT (zps) DO UPDATE SET name = :v.name, level = :v.level, isClub = " + false + ", parent = :v.parentId")
  void insertOrUpdate(@BindBean("v") final DSBVerband verband_);

  @SqlQuery("SELECT * from dsb_organization where isClub=false")
  @RegisterRowMapper(DSBVerbandMapper.class)
  List<DSBVerband> select();

  @SqlQuery("SELECT * from dsb_organization where isClub=false and zps = :id")
  @RegisterRowMapper(DSBVerbandMapper.class)
  List<DSBVerband> select(@Bind("id") String id_);

  @SqlQuery("SELECT * from dsb_organization where isClub=false and parent = :id")
  @RegisterRowMapper(DSBVerbandMapper.class)
  List<DSBVerband> selectChildrenOf(@Bind("id") String id_);

}
