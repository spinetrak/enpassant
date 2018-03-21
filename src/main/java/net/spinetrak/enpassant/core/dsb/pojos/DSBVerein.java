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

public class DSBVerein
{
  private final String _id;
  private final String _name;
  private final List<DSBSpieler> _spieler = new ArrayList<>();
  private final DSBVerband _verband;

  public DSBVerein(@NotNull final String id_, @NotNull final String name_, @NotNull final DSBVerband verband_)
  {
    _id = id_.trim();
    _name = name_.trim();
    _verband = verband_;
  }

  public void add(@NotNull final DSBSpieler spieler_)
  {
    _spieler.add(spieler_);
  }

  public String getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public List<DSBSpieler> getSpieler()
  {
    return _spieler;
  }

  public DSBVerband getVerband()
  {
    return _verband;
  }

  @Override
  public String toString()
  {
    return "DSBVerein{" +

      "id='" + _id + '\'' +
      ", name='" + _name + '\'' +
      '}';
  }
}
