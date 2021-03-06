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
package tinyos.dlrc.preprocessor.lexer.streams;

import java.io.IOException;
import java.util.List;

import java_cup.runtime.Symbol;
import tinyos.dlrc.preprocessor.lexer.PreprocessorToken;
import tinyos.dlrc.preprocessor.lexer.Stream;

/**
 * A stream that has a list of {@link Symbol}s as source, the <code>Symbol</code>s
 * can be copied in order to prevent <code>Symbol</code>-recycling.
 * @author Benjamin Sigg
 */
public class SymbolStream extends Stream{
    private List<Symbol> symbols;
    private int index;
    private boolean copy;
    
    public SymbolStream( List<Symbol> symbols, boolean copy ){
        this.copy = copy;
        this.symbols = symbols; 
    }
    
    @Override
    protected Symbol next() throws IOException {
        if( index < symbols.size() ){
            Symbol next = symbols.get( index++ );
            if( copy )
                return states.symbol( (PreprocessorToken)next.value );
            else
                return next;
        }
        else
            return null;
    }
    
    @Override
    public void popped() {
        // ignore
    }
    
    @Override
    public void disable() throws IOException {
    	// ignore
    }
    
    @Override
    public void enable() throws IOException {
    	// ignore
    }
    
    @Override
    public void pushed() {
        index = 0;
    }
}
