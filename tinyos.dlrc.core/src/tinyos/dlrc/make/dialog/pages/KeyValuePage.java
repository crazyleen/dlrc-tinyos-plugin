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
package tinyos.dlrc.make.dialog.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tinyos.dlrc.make.dialog.AbstractMakeTargetDialogPage;
import tinyos.dlrc.make.dialog.IMakeTargetInformation;
import tinyos.dlrc.make.dialog.pages.CustomizationControls.Selection;
import tinyos.dlrc.make.targets.MakeTargetPropertyKey;
import tinyos.dlrc.make.targets.MakeTargetSkeleton;

public abstract class KeyValuePage<T> extends AbstractMakeTargetDialogPage<MakeTargetSkeleton> implements ICustomizeablePage{
    private static final String ID_ADD = "add";
    private static final String ID_EDIT = "edit";
    private static final String ID_DELETE = "delete";
    private static final String ID_UP = "up";
    private static final String ID_DOWN = "down";
    
    private Table table;
    private Buttons buttons;
    
    private CustomizationControls customization;
    
    public KeyValuePage( boolean showCustomization, String title, String message ){
        super( title );
        
        setDefaultMessage( message );
        
        if( showCustomization ){
        	customization = new CustomizationControls();
        	customization.setPage( this );
        }
    }
    
	public abstract String getNewDialogTitle();

	public abstract String getEditDialogTitle();

	public abstract String getDialogExample();

	public abstract String getValueName();

	public abstract String getKeyName();
	
	/**
	 * Checks the validity of the table.
	 * @param table the table, the first index denotes the column, the second index is the row.
	 * @return an error message or <code>null</code>
	 */
	protected abstract String checkValid( String[][] table );
    
	/**
	 * Checks whether the content of this page is valid.
	 * @return an error message or <code>null</code>
	 */
	protected String checkValid(){
		int rows = table.getItemCount();
		int columns = table.getColumnCount();
		
		String[][] content = new String[columns][rows];
		
        for( int i = 0; i < rows; i++ ){
            TableItem item = table.getItem( i );
            for( int j = 0; j < columns; j++ ){
            	content[j][i] = item.getText( j );
            }
        }
        
        return checkValid( content );
	}
	
	protected void recheckValid(){
		String error = checkValid();
		if( error == null )
			setDefaultMessage();
		else
			setError( error );
	}
	
    public void setCustomEnabled( boolean enabled ){
    	table.setEnabled( enabled );
    	buttons.setCustomEnabled( enabled );
    	contentChanged();
    }

    public void createControl( Composite parent ){
        Composite base = new Composite( parent, SWT.NONE );
        base.setLayout( new GridLayout( 2, false ) );
        setControl( base );
        
    	if( customization != null ){
    		customization.createControl( base, true );
    		customization.getControl().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    	}
        
        table = new Table( base, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI );
        table.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        table.setHeaderVisible( true );
        
        createTableColumns( table );
        
        buttons = new Buttons();
        buttons.createControl( base );
        buttons.getControl().setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );
    }
    
    protected void createTableColumns( Table table ){
        TableColumn key = new TableColumn( table, SWT.LEFT );
        key.setText( getKeyName() );
        key.setResizable( true );
        key.setMoveable( false );
        key.setWidth( 100 );
        
        TableColumn value = new TableColumn( table, SWT.LEFT );
        value.setText( getValueName() );
        value.setResizable( true );
        value.setMoveable( false );
        value.setWidth( 200 );    	
    }
    
    @Override
    protected void contentChanged(){
    	recheckValid();
    	super.contentChanged();
    }
    
    protected abstract KeyValueDialog<T> createDialog( Shell shell );
    
    protected T openDialog( Shell shell, T oldValue ){
    	KeyValueDialog<T> dialog = createDialog( shell );
    	String key = null;
    	String value = null;
    	if( oldValue != null ){
    		key = getKey( oldValue );
    		value = getValue( oldValue );
    	}
    	if( dialog.open( key, value, oldValue )){
    		return dialog.get();
    	}
    	else{
    		return null;
    	}
    }
    
    protected void doAdd(){
    	T entry = openDialog( getControl().getShell(), null );
    	if( entry != null ){
    	    TableItem item = new TableItem( table, SWT.NONE );
    	    show( entry, item );
    	    contentChanged();
        }
    }
    
    protected void doEdit(){
        int index = table.getSelectionIndex();
        if( index >= 0 ){
            TableItem item = table.getItem( index );
            T entry = create( item.getText( 0 ), item.getText( 1 ), item );
            entry = openDialog( getControl().getShell(), entry );
            if( entry != null ){
            	show( entry, item );
            }
            contentChanged();
        }
    }
    
    protected void doDelete(){
        int[] indices = table.getSelectionIndices();
        if( indices != null ){
            table.remove( indices );
            contentChanged();
        }
    }
    
    protected void doUp(){
        doMove( -1 );
    }
    
    protected void doDown(){
        doMove( 1 );
    }
    
    protected void doMove( int delta ){
        int index = table.getSelectionIndex();
        if( index < 0 )
            return;
        
        int next = index + delta;
        if( next < 0 || next >= table.getItemCount() )
            return;
        
        TableItem first = table.getItem( index );
        TableItem second = table.getItem( next );
        
        int size = table.getColumnCount();
        
        String[] firstTemp = new String[ size ];
        String[] secondTemp = new String[ size ];
        
        for( int i = 0; i < size; i++ ){
        	firstTemp[i] = first.getText( i );
        	secondTemp[i] = second.getText( i );
        }
        
        first.setText( secondTemp );
        second.setText( firstTemp );
        
        table.setSelection( next );
        contentChanged();
    }

    protected abstract String getKey( T entry );
    
    protected abstract String getValue( T entry );
    
    protected abstract MakeTargetPropertyKey<T[]> getKey();
    
    protected abstract T create( String key, String value, TableItem row );
    
    public void show( MakeTargetSkeleton maketarget, IMakeTargetInformation information ){
        MakeTargetPropertyKey<T[]> key = getKey();
        
        if( customization != null ){
        	customization.setSelection(
        			maketarget.isUseLocalProperty( key ),
        			maketarget.isUseDefaultProperty( key ));
        }
        
        T[] entries = maketarget.getLocalProperty( key );
        show( entries, information );
    }
    
    public void show( T[] entries, IMakeTargetInformation information ){
    	table.removeAll();

    	if( entries != null ){
    		for( T entry : entries ){
    			TableItem item = new TableItem( table, SWT.NONE );
    			show( entry, item );
    		}
    	}

    	recheckValid();	
    }
    
    protected void show( T entry, TableItem item ){
    	item.setText( new String[]{ getKey( entry ), getValue( entry ) } );
    }

    public void store( MakeTargetSkeleton maketarget ){
        MakeTargetPropertyKey<T[]> key = getKey();
        T[] entries = getEntries();
        
        maketarget.putLocalProperty( key, entries );
        
        if( customization != null ){
        	Selection selection = customization.getSelection();
        	maketarget.setUseLocalProperty( key, selection.isLocal() );
        	maketarget.setUseDefaultProperty( key, selection.isDefaults() );
        }
    }
    
    public T[] getEntries(){
    	int size = table.getItemCount();

        MakeTargetPropertyKey<T[]> key = getKey();
        T[] entries = key.array( size );
        for( int i = 0; i < size; i++ ){
            TableItem item = table.getItem( i );
            entries[i] = create( item.getText( 0 ), item.getText( 1 ), item );
        }
        
        return entries;
    }
    
    private class Buttons extends NavigationButtons{
        public Buttons(){
            super( new String[]{
                    ID_ADD, ID_EDIT, ID_DELETE, ID_UP, ID_DOWN
            }, new String[]{
                    "Add", "Edit", "Delete", "Up", "Down"
            } );
        }
        
        @Override
        protected void doOperation( String id ){
            if( ID_ADD.equals( id )){
                doAdd();
            }
            if( ID_EDIT.equals( id )){
                doEdit();
            }
            if( ID_DELETE.equals( id )){
                doDelete();
            }
            if( ID_UP.equals( id )){
                doUp();
            }
            if( ID_DOWN.equals( id )){
                doDown();
            }
        }
    }
}
