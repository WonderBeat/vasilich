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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;

public class Aiml implements AIMLElement
{
  /*
  Attributes
  */

  private final Topic defaultTopic = new Topic("*");  
  private final List<Topic> topics = new LinkedList<Topic>(Arrays.asList(new Topic[] {defaultTopic}));

  private final List<Category> categories = new LinkedList<Category>();
  
  private String version;

  /*
  Constructors
  */
  
  public Aiml(Attributes attributes)
  {
    version = attributes.getValue(0);
  }
  
  public Aiml(Category... categories)
  {
    this.categories.addAll(Arrays.asList(categories));
  }
  
  /*
  Method Section
  */
  
  public void appendChild(AIMLElement child)
  {
    if (child instanceof Category)
    {
      Category category = (Category) child;
      category.setTopic(defaultTopic);
      defaultTopic.appendChild(category);
      categories.add(category);
    }
    else
    {
      Topic topic = (Topic) child;
      topics.add(topic);
      categories.addAll(topic.categories());
    }
  }
  
  public void appendChildren(List<AIMLElement> children)
  {
    for (AIMLElement child : children)
      appendChild(child);
  }
  
  public List<Category> children()
  {
    return categories;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == null || !(obj instanceof Aiml))
      return false;
    else
      return categories.equals(((Aiml) obj).categories);
  }
  
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    for (Category i : categories)
    {
      result.append(i);
      result.append('\n');
    }
    
    return result.toString();
  }
  
  /*
  Properties
  */
  
  public String getVersion()
  {
    return version;
  }
}
