/*
Copyleft (C) 2005 Hio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import org.xml.sax.Attributes;
import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Match;

public class Get extends TemplateElement
{
  /*
  Attributes
  */
  
  private String name;
  
  /*
  Constructors
  */

  public Get(Attributes attributes)
  {
    name = attributes.getValue(0);
  }

  public Get(String name)
  {
    this.name = name;
  }
  
  /*
  Methods
  */
  
  public boolean equals(Object compared)
  {
    if (compared == null || !(compared instanceof Get))
      return false;
    else
      return name.equals(((Get) compared).name);
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }

  public String process(Match match)
  {
    try
    {
      String value = (String) match.getCallback().getContext().property("predicate." + name);
      return (value != null ? value : "");
    }
    catch (NullPointerException e)
    {
      return "";
    }
  }
}
