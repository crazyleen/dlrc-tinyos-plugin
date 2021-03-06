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
package tinyos.dlrc.nesc12.parser.ast.nodes;

import tinyos.dlrc.nesc12.lexer.Token;
import tinyos.dlrc.nesc12.parser.ast.Range;

public abstract class AbstractTokenASTNode extends AbstractASTNode implements TokenASTNode {
    protected Token token;
    private Range range;
    
    public AbstractTokenASTNode( String name, Token token ){
        super( name );
        this.token = token;
    }
    
    public Range getRange() {
        if( range == null )
            return token;
        return range;
    }
    
    public void setRange( Range range ) {
        this.range = range;
    }
    
    public Token getToken() {
        return token;
    }
    
    public int getChildrenCount() {
        return 0;
    }
    
    public ASTNode getChild( int index ) {
        throw new ArrayIndexOutOfBoundsException( index );
    }
}
