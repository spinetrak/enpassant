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

package net.spinetrak.enpassant.core.dsb.dtos;

import net.spinetrak.enpassant.core.dsb.pojos.DSBOrganization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DSBOrganizationTree
{
  private DSBOrganization _dsbOrganization;
  private List<DSBOrganizationTree> children = new ArrayList<>();
  private String id = null;
  private String text = null;

  public List<DSBOrganizationTree> getChildren()
  {
    Collections.sort(children, (tree1_, tree2_) -> tree1_.getId().compareTo(tree2_.getId()));
    return children;
  }

  public String getId()
  {
    return id;
  }

  public String getText()
  {
    return text;
  }

  public void setDsbOrganization(final DSBOrganization dsbOrganization_)
  {
    _dsbOrganization = dsbOrganization_;
    if (_dsbOrganization != null)
    {
      id = _dsbOrganization.getOrganizationId();
      text = _dsbOrganization.getOrganizationId() + ": " + _dsbOrganization.getName();
      children.addAll(makeNestedList(_dsbOrganization.getOrganizations()));
    }
  }

  private List<DSBOrganizationTree> makeNestedList(final Map<String, DSBOrganization> organizations_)
  {
    final List<DSBOrganizationTree> nestedList = new ArrayList<>();
    for (final DSBOrganization dsbOrganization : organizations_.values())
    {
      final DSBOrganizationTree dsbOrganizationTree = new DSBOrganizationTree();
      dsbOrganizationTree.setDsbOrganization(dsbOrganization);
      nestedList.add(dsbOrganizationTree);
    }
    return nestedList;
  }
}
