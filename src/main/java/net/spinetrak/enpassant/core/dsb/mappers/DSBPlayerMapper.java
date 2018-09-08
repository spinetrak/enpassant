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

package net.spinetrak.enpassant.core.dsb.mappers;

import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;
import net.spinetrak.enpassant.core.dsb.pojos.DWZ;
import net.spinetrak.enpassant.core.fide.FIDE;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class DSBPlayerMapper implements LinkedHashMapRowReducer<String, DSBPlayer>
{
  @Override
  public void accumulate(final Map<String, DSBPlayer> map_, final RowView rowView_)
  {
    final DSBPlayer dsbPlayer = map_.computeIfAbsent(
      rowView_.getColumn("p_clubid", String.class) + "-" + rowView_.getColumn("p_memberid", String.class),
      id -> rowView_.getRow(DSBPlayer.class));

    if (rowView_.getColumn("d_index", Integer.class) != null)
    {
      dsbPlayer.addDWZ(rowView_.getRow(DWZ.class));
    }
    if (rowView_.getColumn("f_id", Integer.class) != null)
    {
      dsbPlayer.addFIDE(rowView_.getRow(FIDE.class));
    }
  }
}
