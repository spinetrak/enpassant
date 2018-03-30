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

import net.spinetrak.enpassant.core.dsb.pojos.DSBAssociation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DSBAssociationTree
{
  private DSBAssociation _dsbAssociation;
  private List<DSBAssociationTree> children = new ArrayList<>();
  private String id = null;
  private String text = null;

  public List<DSBAssociationTree> getChildren()
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

  public void setDsbAssociation(final DSBAssociation dsbAssociation_)
  {
    _dsbAssociation = dsbAssociation_;
    if (_dsbAssociation != null)
    {
      id = _dsbAssociation.getAssociationId();
      text = _dsbAssociation.getAssociationId() + ": " + _dsbAssociation.getName();
      children.addAll(makeNestedList(_dsbAssociation.getAssociations()));
    }
  }

  private List<DSBAssociationTree> makeNestedList(final Map<String, DSBAssociation> associations_)
  {
    final List<DSBAssociationTree> nestedList = new ArrayList<>();
    for (final DSBAssociation dsbAssociation : associations_.values())
    {
      final DSBAssociationTree dsbAssociationTree = new DSBAssociationTree();
      dsbAssociationTree.setDsbAssociation(dsbAssociation);
      nestedList.add(dsbAssociationTree);
    }
    return nestedList;
  }
}
