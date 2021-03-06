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
package tinyos.dlrc.nesc12.parser.ast.elements.values;

import tinyos.dlrc.nesc12.parser.ast.elements.Type;

/**
 * An unknown value is a value whose existance is known, but the parser
 * is not able to find the exact value. e.g. the value of the "unique" function
 * does exists, but is not known. 
 * @author Benjamin Sigg
 */
public class UnknownValue extends AbstractValue{
	private Type type;
	
	public UnknownValue( Type type ){
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	public int sizeOf() {
		return type.sizeOf();
	}

	public String toLabel() {
		return "?";
	}

	public void resolveNameRanges() {
		if( type != null ){
			type.resolveNameRanges();
		}
	}

	public String getBindingType() {
		return "Value";
	}

	public String getBindingValue() {
		return "unknown";
	}
}
