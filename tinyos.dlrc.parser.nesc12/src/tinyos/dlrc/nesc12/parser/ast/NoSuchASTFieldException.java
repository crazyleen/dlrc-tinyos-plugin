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
package tinyos.dlrc.nesc12.parser.ast;

import tinyos.dlrc.nesc12.parser.ast.nodes.AbstractASTNode;

public class NoSuchASTFieldException extends ASTException {
    public NoSuchASTFieldException( AbstractASTNode node, String message, Throwable cause ) {
        super( node, message, cause );
    }

    public NoSuchASTFieldException( AbstractASTNode node, String message ) {
        super( node, message );
    }

    public NoSuchASTFieldException( AbstractASTNode node, Throwable cause ) {
        super( node, cause );
    }

    public NoSuchASTFieldException( AbstractASTNode node ) {
        super( node );
    }    
}
