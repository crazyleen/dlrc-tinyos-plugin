/*
 * Dlrc 2, NesC development in Eclipse.
 * Copyright (C) 2009 DLRC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Web:  http://tos-ide.ethz.ch
 * Mail: tos-ide@tik.ee.ethz.ch
 */
package tinyos.dlrc.views;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class ComparableComparator<T extends Comparable<T>> extends ViewerComparator implements Comparator<T>{
	public int compare(T o1, T o2) {
		return o1.compareTo( o2 );
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public int compare( Viewer viewer, Object e1, Object e2 ){
        return compare( (T)e1, (T)e2 );
    }
    
    @Override
    public boolean isSorterProperty( Object element, String property ){
    	return true;
    }
}
