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
package tinyos.dlrc.nesc12.parser.ast.nodes.nesc;

import tinyos.dlrc.nesc12.parser.ast.ASTException;
import tinyos.dlrc.nesc12.parser.ast.ASTVisitor;
import tinyos.dlrc.nesc12.parser.ast.AnalyzeStack;
import tinyos.dlrc.nesc12.parser.ast.elements.types.GenericType;
import tinyos.dlrc.nesc12.parser.ast.nodes.AbstractListASTNode;

public class InterfaceParameterList extends AbstractListASTNode<InterfaceParameter>{
    public InterfaceParameterList(){
        super( "InterfaceParameterList" );
    }
    
    public InterfaceParameterList( InterfaceParameter child ){
        this();
        add( child );
    }
    
    /**
     * Searches all types that are specified by this list.
     * @return the types, includes <code>null</code> values for holes.
     */
    public GenericType[] resolveGenericTypes(){
        if( isResolved( "generics" ))
            return resolved( "generics" );
        
        GenericType[] types = new GenericType[ getChildrenCount() ];
        for( int i = 0, n = getChildrenCount(); i<n; i++ ){
            InterfaceParameter parameter = getTypedChild( i );
            if( parameter != null ){
                types[i] = parameter.resolveGenericType();
            }
        }
        return resolved( "generics", types );
    }
    
    @Override
    public void resolve( AnalyzeStack stack ) {
        // TODO method not implemented
        super.resolve( stack );
    }
    
    @Override
    public InterfaceParameterList add( InterfaceParameter node ) {
        super.add( node );
        return this;
    }
    
    @Override
    protected void checkChild( InterfaceParameter child ) throws ASTException {
        
    }

    @Override
    protected boolean visit( ASTVisitor visitor ) {
        return visitor.visit( this );
    }
    
    @Override
    protected void endVisit( ASTVisitor visitor ) {
        visitor.endVisit( this );
    }
}
